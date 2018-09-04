package test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mongoDBtoTDB.MongoDBAPI;
import mongoDBtoTDB.MongoToTDB;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.bson.Document;
import org.junit.Test;

import java.util.List;

public class MongoDBConnectTest {
/*
    @Test
    public void connectTest() {
        String address = "ds223542.mlab.com";
        int PORT = 23542;
        String DBName = "sysu";
        String user = "sysu";
        String password = "sysu2018";
        String owlIRI = "http://www.sysu.com/";
        String modelName = "sysuData";
        String owlPath = "G:\\暑假-传统建筑\\protege存储\\sysu.owl";
        String tdbPath = "DataBase\\sysu_TDB";
        // 新建一个ontmodel并设为可推理的
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        MongoToTDB mongoToTDB = new MongoToTDB();
        mongoToTDB.importOWL(ontModel, owlPath);  // 导入OWL文件
        mongoToTDB.importMongoDBDate(address, PORT, DBName, user, password, owlIRI, ontModel);    // 导入实际数据
        mongoToTDB.saveModelToTDB(tdbPath, ontModel, modelName);  // 保存进TDB
        // SPARQL测试
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<http://www.sysu.com/>";
        // 学院包含哪些专业
        String strq = "SELECT ?n ?xn WHERE {" +
                "?s rdf:type :campus." +
                "?s :name ?n." +
                "?x rdf:type :specialty." +
                "?s :contain ?x." +
                "?x :name ?xn." +
                "}";
        // 广州校区包含哪些专业
        String strq2 = "SELECT ?xn WHERE {" +
                "?s rdf:type :zone." +
                "?s :name '广州校区'." +
                "?x rdf:type :specialty." +
                "?s :contain ?x." +
                "?x :name ?xn." +
                "}";
        String strq3 = "SELECT ?s ?o WHERE {" +
                "?s :name ?o." +
                "}";
        Query query = QueryFactory.create(prefix + strq2);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ontModel);
        ResultSet results = qe.execSelect();
        // 输出结果
        ResultSetFormatter.out(System.out, results, query);
        // 释放资源
        qe.close();
    }
*/
    @Test
    public void queryTest() {
        // 与TDB建立连接
        String tdbPath = "G:\\Geography-KG\\Task_3\\SpringMVC-Demo\\GeoKG\\src\\main\\resources\\sysu_TDB";
        String modelName = "sysuData";
        Dataset ds = TDBFactory.createDataset(tdbPath);
        // SPARQL测试
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<http://www.sysu.com/>";
        // 学院包含哪些专业
        String strq = "SELECT ?n ?xn WHERE {" +
                "?s rdf:type :campus." +
                "?s :name ?n." +
                "?x rdf:type :specialty." +
                "?s :contain ?x." +
                "?x :name ?xn." +
                "}";
        // 广州校区包含哪些专业
        String strq2 = "SELECT ?s ?xn WHERE {" +
                "?s rdf:type :zone." +
                "?s :name '广州校区'." +
                "?x rdf:type :specialty." +
                "?s :contain ?x." +
                "?x :name ?xn." +
                "}";
        String strq3 = "SELECT ?s ?p ?o WHERE {" +
                "?s ?p ?o." +
                "}";
        Query query = QueryFactory.create(prefix + strq3);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ds.getNamedModel(modelName));
        /*
        Model resultModelA = qe.execDescribe();
        System.out.println("OK");
        StmtIterator resultIterA = resultModelA.listStatements();
        // 将结果返回
        while (resultIterA.hasNext()) {
            Statement stmt = resultIterA.next();
            System.out.println(stmt.toString());
            Resource sub = stmt.getSubject();
            Property pre = stmt.getPredicate();
            RDFNode obj = stmt.getObject();
            System.out.println(sub + "  " + pre + "  " + obj);
        }
        System.out.println("查询结束");
        */
        ResultSet results = qe.execSelect();
        // 输出结果
        ResultSetFormatter.out(System.out, results, query);
        // 释放资源
        qe.close();
        ds.close();
    }

}