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

    @Test
    /*
    * 将指定mongoDB数据库内的数据转换成三元组格式并与.owl融合，存入TDB
    * 生成TDB可在DataBase目录下看到
    * */
    public void connectTest() {
        String address = "ds131903.mlab.com";
        int PORT = 31903;
        String DBName = "liyuan";
        String user = "sysu";
        String password = "sysu2018";
        String owlIRI = "http://www.liyuan.com/";
        String modelName = "http://www.Graph.com/liyuanData"; // 命名图 -> 最好用绝对路径
        String owlPath = "myData/liyuan.owl";
        String tdbPath = "DataBase/liyuan_TDB";
        // 新建一个ontmodel并设为可推理的
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        MongoToTDB mongoToTDB = new MongoToTDB();
        mongoToTDB.importOWL(ontModel, owlPath);  // 导入OWL文件
        mongoToTDB.importMongoDBDate(address, PORT, DBName, user, password, owlIRI, ontModel);    // 导入实际数据
        mongoToTDB.saveModelToTDB(tdbPath, ontModel, modelName);  // 保存进TDB
        // SPARQL测试
        /*
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
        // 广州校区包含哪些学院
        String strq2 = "SELECT ?xn WHERE {" +
                "?s rdf:type :zone." +
                "?s :name '广州校区'." +
                "?x rdf:type :faculty." +
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
        */
    }


    @Test
    // select查询测试
    public void queryTest() {
        // 与TDB建立连接
        String owlIRI = "http://www.liyuan.com/";
        String modelName = "http://www.Graph.com/liyuanData"; // 命名图 -> 最好用绝对路径
        String tdbPath = "DataBase/liyuan_TDB";
        Dataset ds = TDBFactory.createDataset(tdbPath);
        // SPARQL测试
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<" + owlIRI + ">";
        // 广州有哪些建筑
        String strq1 = "SELECT ?n ?xn WHERE {" +
                "?dis rdf:type :district." +
                "?dis :name '广州'." +
                "?build rdf:type :building." +
                "?dis :contain ?build." +
                "?build :name ?xn." +
                "}";
        // 建筑的材料
        String strq2 = "SELECT ?bn ?mn WHERE {" +
                "?build rdf:type :building." +
                "?mater rdf:type :material." +
                "?build :materials ?mater." +
                "?build :name ?bn." +
                "?mater :name ?mn." +
                "}";
        // all
        String stre = "SELECT ?b ?xn WHERE {" +
                      "?b :has_picture ?x." +
                      "?x :name ?xn." +
                      "}";
        String queryString = "SELECT ?pn ?pp WHERE {" +
                ":building\\/5bc31bd756943146d48ecd73" + " :has_picture ?pic." +
                "?pic :name ?pn." +
                "?pic :path ?pp." +
                "}";
        Query query = QueryFactory.create(prefix + queryString);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ds.getNamedModel(modelName));
        ResultSet results = qe.execSelect();
        // 输出结果
        ResultSetFormatter.out(System.out, results, query);
        // 释放资源
        qe.close();
        ds.close();
    }

}