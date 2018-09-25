package com.geokg.utlis;

import org.apache.jena.query.*;
import org.apache.jena.tdb.TDBFactory;

import java.util.ResourceBundle;

public class TDBClient{
    private String owlIRI;
    private String path;
    private String modelName;
    private String prefix;
    private Dataset tdb;
    private Query query;
    private QueryExecution queryExecution;

    public TDBClient(){
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config/tdb");
        this.owlIRI = resourceBundle.getString("tdb.owlIRI");
        this.path = this.getClass().getResource(resourceBundle.getString("tdb.path")).getPath();
        this.modelName = resourceBundle.getString("tdb.model.name");
        this.prefix = resourceBundle.getString("tdb.prefix")
                      + "PREFIX :<" + this.owlIRI + ">";
        this.tdb = TDBFactory.createDataset(this.path);
        System.out.println("================TDB连接================");
    }

    public String getOwlIRI() {
        return owlIRI;
    }

    public void setOwlIRI(String owlIRI) {
        this.owlIRI = owlIRI;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Dataset getTdb() {
        return tdb;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setTdb(Dataset tdb) {
        this.tdb = tdb;
    }

    public QueryExecution getQueryExecution() {
        return queryExecution;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    public void setQueryExecution(String queryString) {
        this.query = QueryFactory.create(prefix + queryString);
        this.queryExecution = QueryExecutionFactory.create(this.query, this.tdb.getNamedModel(this.modelName));
        System.out.println("================可以开始查询================");
    }

    public QueryExecution createQueryExecution(String queryString) {
        System.out.println("================开始查询================");
        this.query = QueryFactory.create(prefix + queryString);
        System.out.println(this.query);
        this.queryExecution = QueryExecutionFactory.create(this.query, this.tdb.getNamedModel(this.modelName));
        return this.queryExecution;
    }

    protected void finalize() {
        this.queryExecution.close();
        this.tdb.close();
        System.out.println("================查询结束，释放资源================");
    }
}