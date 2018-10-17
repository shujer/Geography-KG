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
    private String owlIRI = "http://www.liyuan.com/";
    private String tdbName = "liyuan_TDB";
    private String modelName = "http://www.Graph.com/liyuanData";
    private String dataValue = "字面值";  // 标识字面量属性值
    private String cutChar = "/";   // IRI的切分符号，这里是/
    private String pictures_path = "G:/pictures/立园/"; // 图片存放位置
    // 前缀定义
    private String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                          "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                          "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                          "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                          "PREFIX :<" + owlIRI + ">";
    /*
    * 返回的值将owlIRI替换成:
    * */
    public String cutOffPrefix(String str) {
        if (str.contains(owlIRI)) {
            return str.replace(owlIRI, ":");
        }
        return str;
    }
    // sub为building对应的IRI，找出一个此building的图片路径返回
    public String get_picture_of_building(Dataset ds, String sub) {
        int index = sub.indexOf('/');
        String sub_ch = sub.substring(0, index) + "\\" + sub.substring(index);
        String queryString = "SELECT ?pn ?pp WHERE {" +
                              sub_ch + " :has_picture ?pic." +
                             "?pic :name ?pn." +
                             "?pic :path ?pp." +
                             "}";
        Query query = QueryFactory.create(prefix + queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, ds.getNamedModel(modelName));
        ResultSet results = qe.execSelect();
        if (results.hasNext()) {    // 有图片，取第一个就好
            QuerySolution sol = results.nextSolution();
            String pic_name = sol.get("pn").toString();
            String pic_path = sol.get("pp").toString();
            pic_path = this.getClass().getResource("/" + pic_path).getPath();
            System.out.println("image://" + pic_path + pic_name);
            //return "image://" + pictures_path + pic_name;
            return "image://https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3127997783,2847505974&fm=58&bpow=3075&bpoh=2216";
        }
        return "circle";    // 没有图片就不展示
    }




    @RequestMapping(value = "/echartsview/sparql.do")
    @ResponseBody
    public void sparqlQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryString = request.getParameter("sparql");    // sparql查询语句
        System.out.println(queryString);
        // TDB路径和model名称
        String tdbPath = this.getClass().getResource("/" + tdbName).getPath();
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
                Node node1 = new Node(0, str1, 2, str1, "circle");
                nodesSet.add(node1);
                for (int i = 1; i < colNum; i++) {
                    String str2 = sol.get(colString[i]).toString().replace(owlIRI, ":");
                    Node node2 = new Node(0, str2, 2, str2, "circle");
                    String str3 = sol.get(colString[i-1]).toString().replace(owlIRI, ":");
                    Node node3 = new Node(0, str3, 2, str3, "circle");
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
                String symbol1 = "circle";
                String symbol2 = "circle";
                // 得到图片
                if (catego1.equals("building")) {   // 将建筑用图片展示
                    symbol1 = get_picture_of_building(ds, id1);
                }
                if (catego2.equals("building")) {
                    symbol2 = get_picture_of_building(ds, id2);
                }
                Node node1 = new Node(categoriesList.indexOf(catego1), str1, 2, id1, symbol1);
                Node node2 = new Node(categoriesList.indexOf(catego2), str2,2, id2, symbol2);
                Node res1 = new Node(categoriesList.indexOf(categoRes), strRes, 2,idRes, "circle");
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
?build :has_picture ?bn.
}
// 立园test
construct {
?build :has_part ?part.
?part :materials ?mater.
?build :name ?bn.
?part :name ?pn.
?mater :name ?mn.
}
where {
?build rdf:type :building.
?build :has_part ?part.
?part :materials ?mater.
?build :name ?bn.
?part :name ?pn.
?mater :name ?mn.
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
        int lens = categoriesList.size();
        Node[] nodes = nodesSet.toArray(new Node[nodesSet.size()]);
        Link[] links = linksSet.toArray(new Link[linksSet.size()]);
        Map<String,Object> option= new HashMap<String,Object>();
        option.put("nodes", nodes);
        option.put("links", links);
        option.put("categories", categories);

        Gson gson = new Gson();
        String options = gson.toJson(option);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.append(options);
    }
}
