import org.apache.jena.query.*;
import org.apache.jena.tdb.TDBFactory;

public class TDBPortlTest {
    public static void main(String[] args) {
        String tdbPath = "DataBase\\myTDB1";
        //String tdbPath = "G:\\暑假-传统建筑\\apache-jena-fuseki-3.8.0\\run\\databases\\mytest";
        String modelURI = "test";
        /*
        // 连接TDB
        Dataset ds = TDBFactory.createDataset(tdbPath);
        ds.begin(ReadWrite.WRITE);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        String owlPath = "G:\\暑假-传统建筑\\protege存储\\sysu.owl";
        FileManager.get().readModel(model, owlPath);
        ds.addNamedModel(modelURI, model);
        ds.commit();
        ds.end();
        ds.close();
        */

        // 获取TDB
        Dataset ds = TDBFactory.createDataset(tdbPath);

        // 查询
        // SPARQL
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<http://www.sysu.com/>";
        String strq1 = "SELECT ?n ?xn WHERE {" +
                "?s rdf:type :校区." +
                "?s :名称 ?n." +
                "?s :包含 ?x." +
                "?x :名称 ?xn." +
                "}";
        String strq2 = "SELECT ?s ?p ?o WHERE {" +
                "?s ?p ?o." +
                "}";
        Query query = QueryFactory.create(prefix + strq1);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ds.getNamedModel(modelURI));
        ResultSet results = qe.execSelect();
        // 输出结果
        ResultSetFormatter.out(System.out, results, query);

        // 关闭连接
        ds.close();


    }
}
