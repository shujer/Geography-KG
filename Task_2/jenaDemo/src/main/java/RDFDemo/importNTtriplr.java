package RDFDemo;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class importNTtriplr {
    public static void main(String[] args) {
        // owl & 数据
        String owlPath = "file:C:\\Users\\lanse\\Documents\\GitHub\\Geography-KG\\Task_2\\owl本体\\sysu.owl";
        String dataPath = "file:C:\\Users\\lanse\\Documents\\GitHub\\Geography-KG\\Task_2\\owl本体\\sysu_add.nt";
        OntModel ontmodel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        ontmodel.read(owlPath);
        //ontmodel.read(dataPath);
        InputStream in = FileManager.get().open(dataPath);
        ontmodel.read(in, "", "N3");
        // 将数据存入TDB
/*
        String tdbPath = "DataBase\\sysu_TDB";
        // 连接TDB
        Dataset ds = TDBFactory.createDataset(tdbPath);
        ds.begin(ReadWrite.WRITE);
        ds.addNamedModel("sysuTDB", ontmodel);
        ds.commit();
        ds.end();
        ds.close();
*/
        // SPARQL测试
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<http://www.sysu.com/>";
        String strq = "SELECT ?n ?xn WHERE {" +
                "?s rdf:type :学院." +
                "?s :名称 ?n." +
                "?x rdf:type :专业." +
                "?s :包含 ?x." +
                "?x :名称 ?xn." +
                "}";
        String strq2 = "SELECT ?xn WHERE {" +
                "?s rdf:type :学院." +
                "?s :名称 '化学学院'." +
                "?x rdf:type :专业." +
                "?s :包含 ?x." +
                "?x :名称 ?xn." +
                "}";
        Query query = QueryFactory.create(prefix + strq2);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ontmodel);
        ResultSet results = qe.execSelect();
        // 输出结果
        ResultSetFormatter.out(System.out, results, query);
        // 释放资源
        qe.close();
    }


}
