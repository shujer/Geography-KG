package com.geokg.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.geokg.pojo.Link;
import com.geokg.pojo.Node;
import com.geokg.redis.JedisUtil;
import com.geokg.utlis.TDBClient;
import com.google.gson.Gson;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubmitController {
    private final static int cacheSeconds = 3600;

    private String owlIRI = ResourceBundle.getBundle("config/tdb").getString("tdb.owlIRI");

    /*
     * @desc 返回SPARQL查询结果
     */
    @RequestMapping(value = "/echartsview/sparql.do")
    @ResponseBody
    public void sparqlQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String queryString = request.getParameter("sparql");    // sparql查询语句
        if(queryString != null) queryString = queryString.trim();
        else queryString = "";

        String options;
        if(JedisUtil.exists(queryString)) {
            options = getNodeandLinkFromRedis(queryString);
        }
        else {
            options = getNodeandLinkFromDB(queryString);
        }
        System.out.println(options);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.append(options);
    }

    @SuppressWarnings("unchecked")
    private String getNodeandLinkFromRedis(String queryString) {
        System.out.println("=================从Redis中获取查询结果=================");
        String result = JedisUtil.get(queryString);
        System.out.println(result);
        return result;
    }

    private String getNodeandLinkFromDB(String queryString){
        System.out.println("=================从TDB中获取查询结果=================");
        System.out.println(owlIRI);
        TDBClient tdbClient = new TDBClient();
        QueryExecution qe = tdbClient.createQueryExecution(queryString);
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
        Node[] nodes = nodesSet.toArray(new Node[nodesSet.size()]);
        Link[] links = linksSet.toArray(new Link[linksSet.size()]);
        Map<String,Object> option= new HashMap<String,Object>();
        option.put("nodes", nodes);
        option.put("links", links);
        Gson gson = new Gson();
        String options = gson.toJson(option);

        JedisUtil.set(queryString,options, cacheSeconds);
        return options;
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