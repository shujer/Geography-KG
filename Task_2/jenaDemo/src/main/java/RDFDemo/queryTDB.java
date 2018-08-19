package RDFDemo;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;

public class queryTDB {
    public ResultSet SelectQuery(Dataset ds, String queryString) {
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(queryString, ds);
        ResultSet results = qe.execSelect();
        return results;
    }

    public ResultSet SelectQuery(Model model, String queryString) {
        // 执行查询
        QueryExecution qe = QueryExecutionFactory.create(queryString, model);
        ResultSet results = qe.execSelect();
        return results;
    }
    //updateFile是含有更新操作的sparql文件
    public void UpdateQuery(Dataset ds, String updateFile) {
        UpdateAction.readExecute(updateFile, ds);
    }

}
