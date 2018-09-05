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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubmitController {
    private String owlIRI = "http://www.sysu.com/";
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
        String tdbPath = this.getClass().getResource("/sysu_TDB").getPath();
        String modelName = "sysuData";
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
        if (test.contains("select")) {  // select查询
            ResultSet results = qe.execSelect();
            // 得到每一列的变量名
            List<String> colList = results.getResultVars();
            int colNum = colList.size();
            String[] colString = colList.toArray(new String[colNum]);
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Node node1 = new Node(0, sol.get(colString[0]).toString().replace(owlIRI, ":"), 2);
                nodesSet.add(node1);
                for (int i = 1; i < colNum; i++) {
                    Node node2 = new Node(0, sol.get(colString[i]).toString().replace(owlIRI, ":"), 2);
                    Link link1 = new Link(sol.get(colString[i-1]).toString().replace(owlIRI, ":"), "connect", sol.get(colString[i]).toString().replace(owlIRI, ":"));
                    nodesSet.add(node2);
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
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                Resource sub = stmt.getSubject();
                Property pre = stmt.getPredicate();
                RDFNode obj = stmt.getObject();
                Node node1 = new Node(0,sub.toString().replace(owlIRI, ":"),2);
                Node node2 = new Node(0,obj.toString().replace(owlIRI, ":"),2);
                Link link1 = new Link(sub.toString().replace(owlIRI, ":"), pre.toString().replace(owlIRI, ":"), obj.toString().replace(owlIRI, ":"));
                nodesSet.add(node1);
                nodesSet.add(node2);
                linksSet.add(link1);
                System.out.println(stmt.toString().replace(owlIRI, ":"));
            }
        }
        /*
construct {
?x :contain ?z.
?x :name ?xn.
?z :name ?zn.
?z :area ?m.
}
where {
?x rdf:type :zone.
?z rdf:type :campus.
?x :contain ?z.
?x :name ?xn.
?z :name ?zn.
?z :area ?m.
}
        * */

        System.out.println("查询结束");
        // 释放资源
        qe.close();
        // 关闭连接
        ds.close();
// 将结果返回
        Node[] nodes = nodesSet.toArray(new Node[nodesSet.size()]);
        Link[] links = linksSet.toArray(new Link[linksSet.size()]);
        Map<String,Object> option= new HashMap<String,Object>();
        option.put("nodes", nodes);
        option.put("links", links);
        Gson gson = new Gson();
        String options = gson.toJson(option);
        System.out.println(options);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.append(options);
    }
}
