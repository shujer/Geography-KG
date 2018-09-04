package demo.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import demo.pojo.Link;
import demo.pojo.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;

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
        String test = queryString.toLowerCase();    // 不区分大小写
        if (test.contains("select")) {  // select查询
            ResultSet results = qe.execSelect();
            ResultSetFormatter.out(System.out, results, query);
        } else if (test.contains("construct")) {    // construct查询
            Model results = qe.execConstruct();
            StmtIterator iter = results.listStatements();
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                System.out.println(stmt.toString());
            }
        } else if (test.contains("describe")) {
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
}
where {
?x rdf:type :zone.
?z rdf:type :campus.
?x :name ?xn.
?z :name ?zn.
}
        * */
        //ArrayList nodes = new ArrayList();
        //ArrayList links = new ArrayList();
        System.out.println("查询结束");
        // 释放资源
        qe.close();
        // 关闭连接
        ds.close();
// 将结果返回

        Node node1 = new Node(0,"乔布斯",2);
        Node node2 = new Node(0,"丽萨-乔布斯",2);
        Link link1 = new Link("丽萨-乔布斯","女儿","乔布斯");
        Node[] ns = new Node[2];
        Link[] ls = new Link[1];
        ns[0] = node1;
        ns[1] = node2;
        ls[0] = link1;
        Map<String,Object> option= new HashMap<String,Object>();
        option.put("nodes", ns);
        option.put("links", ls);
        Gson gson = new Gson();
        String options = gson.toJson(option);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.append(options);
    }
}
