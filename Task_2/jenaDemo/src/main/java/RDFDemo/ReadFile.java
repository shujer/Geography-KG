package RDFDemo;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.query.*;

public class ReadFile {
    public static void main(String[] args) {
        // 本体model
        OntModel ontmodel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        ontmodel.read("G:\\暑假-传统建筑\\protege存储\\sysu.owl");

        // SPARQL
        String prefix="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                "PREFIX xsd:<http://www.w3.org/2000/10/XMLSchema#>"+
                "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX :<http://www.sysu.com/>";
        String strq = "SELECT ?n ?xn WHERE {" +
                            "?s rdf:type :校园." +
                            "?s :名称 ?n." +
                            "?s :包含 ?x." +
                            "?x :名称 ?xn." +
                        "}";
        Query query = QueryFactory.create(prefix + strq);
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(query, ontmodel);
        ResultSet results = qe.execSelect();
        // 输出结果
        ResultSetFormatter.out(System.out, results, query);
        // 释放资源
        qe.close();
    }
}
