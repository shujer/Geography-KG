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
    @RequestMapping(value = "/echartsview/sparql.do")
    @ResponseBody
    public void sparqlQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryString = request.getParameter("sparql");    // sparql查询语句
        System.out.println(queryString);
        // TDB路径和model名称
        String tdbPath = "G:\\Geography-KG\\Task_3\\SpringMVC-Demo\\GeoKG\\src\\main\\resources\\sysu_TDB";
        String modelName = "sysuData";
        // 前缀定义
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<http://www.sysu.com/>";
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
            ResultSetFormatter.out(System.out, results, query);
        } else if (test.contains("construct")) {    // construct查询
            Model results = qe.execConstruct();
            StmtIterator iter = results.listStatements();
            // 将结果转换成echart需要的格式
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                Resource sub = stmt.getSubject();
                Property pre = stmt.getPredicate();
                RDFNode obj = stmt.getObject();
                Node node1 = new Node(0,sub.toString(),2);
                Node node2 = new Node(0,obj.toString(),2);
                Link link1 = new Link(sub.toString(), pre.toString(), obj.toString());
                nodesSet.add(node1);
                nodesSet.add(node2);
                linksSet.add(link1);
                System.out.println(stmt.toString());
            }
        } else if (test.contains("describe")) {     // describe查询
            Model results = qe.execDescribe();
            StmtIterator iter = results.listStatements();
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                System.out.println(stmt.toString());
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
