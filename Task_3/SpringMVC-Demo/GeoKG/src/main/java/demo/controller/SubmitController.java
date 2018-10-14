package demo.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import demo.pojo.Link;
import demo.pojo.Node;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubmitController {
    private String owlIRI = "http://www.geokg.com/";
    private String tdbName = "geokg_TDB";
    private String modelName = "http://www.Graph.com/geokgData";
    private String dataValue = "字面值";  // 标识字面量属性值
    private String cutChar = "/";   // IRI的切分符号，这里是/
    /*
    * 返回的值将owlIRI替换成:
    * */
    public String cutOffPrefix(String str) {
        if (str.contains(owlIRI)) {
            return str.replace(owlIRI, ":");
        }
        return str;
    }
    @RequestMapping(value = "/echartsview/sparql.do")
    @ResponseBody
    public void sparqlQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryString = request.getParameter("sparql");    // sparql查询语句
        System.out.println(queryString);
        // TDB路径和model名称
        String tdbPath = this.getClass().getResource("/" + tdbName).getPath();
        // 前缀定义
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<" + owlIRI + ">";
        // 连接TDB查询
        Dataset ds = TDBFactory.createDataset(tdbPath);
        System.out.println("TDB连接");
        Query query = QueryFactory.create(prefix + queryString);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ds.getNamedModel(modelName));
        String test = queryString.toLowerCase();
        Set<Node> nodesSet = new HashSet<Node>();
        Set<Link> linksSet = new HashSet<Link>();
        List<String> categoriesList = new ArrayList<String>();  // 节点类型
        categoriesList.add(dataValue);  // 代表所有字面值属性值的节点
        if (test.contains("select")) {  // select查询
            ResultSet results = qe.execSelect();
            // 得到每一列的变量名
            List<String> colList = results.getResultVars();
            int colNum = colList.size();
            String[] colString = colList.toArray(new String[colNum]);
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                String str1 = sol.get(colString[0]).toString().replace(owlIRI, ":");
                Node node1 = new Node(0, str1, 2, str1);
                nodesSet.add(node1);
                for (int i = 1; i < colNum; i++) {
                    String str2 = sol.get(colString[i]).toString().replace(owlIRI, ":");
                    Node node2 = new Node(0, str2, 2, str2);
                    String str3 = sol.get(colString[i-1]).toString().replace(owlIRI, ":");
                    Node node3 = new Node(0, str3, 2, str3);
                    Link link1 = new Link(str3, "connect", str2);
                    nodesSet.add(node2);
                    nodesSet.add(node3);
                    linksSet.add(link1);
                }
            }
            ResultSetFormatter.out(System.out, results, query);
        } else if (test.contains("construct") || test.contains("describe")) {
            Model results = null;
            if (test.contains("construct")) {    // construct查询
                results = qe.execConstruct();
            } else {                             // describe查询
                results = qe.execDescribe();
            }
            StmtIterator iter = results.listStatements();
            // 将结果转换成echart需要的格式
            Property pre_name = results.getProperty(owlIRI + "name");
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                Resource sub = stmt.getSubject();
                Property pre = stmt.getPredicate();
                RDFNode obj = stmt.getObject();
                String str1 = sub.toString().replace(owlIRI, ":");
                String str2 = obj.toString().replace(owlIRI, ":");
                String strlink = pre.toString().replace(owlIRI, ":");
                String strRes = strlink.substring(1);   // 去掉:
                String catego1 = dataValue;
                String catego2 = dataValue;
                String categoRes = strRes;
                // 过滤掉原本的name关系，在echarts展示中使用name节点来代替id节点
                if (strlink.equals(":name")) {     // 过滤掉原本的name关系
                    System.out.println(stmt.toString().replace(owlIRI, ":") + " 丢弃");
                    continue;
                }
                if (str1.contains(":")) {   // 实体节点
                    Statement stmt_t = results.getRequiredProperty(sub, pre_name);
                    catego1 = str1.split(cutChar)[0].substring(1);  // 用此节点在IRI中的类别做其echarts的展示类别
                    if (categoriesList.indexOf(catego1) < 0) {      // 新类别
                        categoriesList.add(catego1);
                    }
                    str1 = stmt_t.getObject().toString();    // 得到此节点所对应的name,令其替代id
                }
                if (str2.contains(":")) {   // 实体节点
                    Statement stmt_t = results.getRequiredProperty(results.getResource(obj.toString()), pre_name);
                    catego2 = str2.split(cutChar)[0].substring(1);  // 用此节点在IRI中的类别做其echarts的展示类别
                    if (categoriesList.indexOf(catego2) < 0) {      // 新类别
                        categoriesList.add(catego2);
                    }
                    str2 = stmt_t.getObject().toString();    // 得到此节点所对应的name,令其替代id
                }
                // 关系节点的类别，以关系名来划分
                if (categoriesList.indexOf(categoRes) < 0) {
                    categoriesList.add(categoRes);
                }
                String id1 = sub.toString().replace(owlIRI, ":");
                String id2 = obj.toString().replace(owlIRI, ":");
                String idRes = id1 + strlink;

                Node node1 = new Node(categoriesList.indexOf(catego1), str1, 2, id1);
                Node node2 = new Node(categoriesList.indexOf(catego2), str2,2, id2);
                Node res1 = new Node(categoriesList.indexOf(categoRes), strRes, 2,idRes);
                Link link1 = new Link(id1, "", idRes);
                Link link2 = new Link(idRes, "", id2);
                nodesSet.add(node1);
                nodesSet.add(node2);
                nodesSet.add(res1);
                linksSet.add(link1);
                linksSet.add(link2);
                System.out.println(stmt.toString().replace(owlIRI, ":"));
            }
        }
        /*
// 每个地区有哪些建筑
construct {
?dis :contain ?build.
?dis :name ?dn.
?build :name ?bn.
}
where {
?dis rdf:type :district.
?build rdf:type :building.
?dis :contain ?build.
?dis :name ?dn.
?build :name ?bn.
}


// 有玻璃（彩色）搭建的建筑
construct {
?build :materials ?mater.
?mater :name '玻璃（彩色）'.
?build :name ?bn.
}
where {
?mater rdf:type :material.
?build rdf:type :building.
?build :materials ?mater.
?mater :name '玻璃（彩色）'.
?build :name ?bn.
}


        * */

        System.out.println("查询结束");
        // 释放资源
        qe.close();
        // 关闭连接
        ds.close();
// 将结果返回
        String[] categories = new String[categoriesList.size()];
        categoriesList.toArray(categories);
        Node[] nodes = nodesSet.toArray(new Node[nodesSet.size()]);
        Link[] links = linksSet.toArray(new Link[linksSet.size()]);
        Map<String,Object> option= new HashMap<String,Object>();
        option.put("nodes", nodes);
        option.put("links", links);
        option.put("categories", categories);
        Gson gson = new Gson();
        String options = gson.toJson(option);
        System.out.println(options);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.append(options);
    }
}
