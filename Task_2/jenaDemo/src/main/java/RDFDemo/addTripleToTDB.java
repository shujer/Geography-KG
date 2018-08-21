package RDFDemo;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.*;

public class addTripleToTDB {
    void addTriple(Dataset ds, String modelURI, String s, String p, String o) {
        ds.begin(ReadWrite.WRITE);
        Model model = null;
        try {
            model = ds.getNamedModel(modelURI);
            // 存在当前三元组则不添加
            Selector selector = new SimpleSelector(
                    (s != null) ? model.createResource(s) : null,
                    (p != null) ? model.createProperty(p) : null,
                    (o != null) ? model.createResource(o) : null
            );
            StmtIterator iter = model.listStatements(selector);
            if (iter.hasNext()) {
                System.out.println("已有三元组，不重复添加");
            } else {
                Statement stmt = model.createStatement(
                        model.createResource(s),
                        model.createProperty(p),
                        model.createResource(o)
                );
                model.add(stmt);
                model.commit();
                ds.commit();
            }
        } finally {
            if (model != null) {
                model.close();
            }
            ds.end();
        }
    }
}
