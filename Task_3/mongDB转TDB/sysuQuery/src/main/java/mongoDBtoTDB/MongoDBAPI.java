package mongoDBtoTDB;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.jena.base.Sys;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bson.Document;

import javax.print.Doc;
import java.util.*;

import org.bson.types.ObjectId;

public class MongoDBAPI {
    String belongTo = "belong_to";
    private static final String PLEASE_SEND_IP = "没有传入ip或者端口号";
    private static final String PLEASE_INSTANCE_MONGOCLIENT = "请实例化MongoClient";
    private static final String PLEASE_SEND_MONGO_REPOSITORY = "请指定要删除的mongo库";
    private static final String DELETE_MONGO_REPOSITORY_EXCEPTION = "删除mongo库异常";
    private static final String DELETE_MONGO_REPOSITORY_SUCCESS = "批量删除mongo库成功";
    private static final String NOT_DELETE_MONGO_REPOSITORY = "未删除mongo库";
    private static final String DELETE_MONGO_REPOSITORY = "成功删除mongo库：";
    private static final String CREATE_MONGO_COLLECTION_NOTE = "请指定要创建的库";
    private static final String NO_THIS_MONGO_DATABASE = "未找到指定mongo库";
    private static final String CREATE_MONGO_COLLECTION_SUCCESS = "创建mongo库成功";
    private static final String CREATE_MONGO_COLLECTION_EXCEPTION = "创建mongo库错误";
    private static final String NOT_CREATE_MONGO_COLLECTION = "未创建mongo库collection";
    private static final String CREATE_MONGO_COLLECTION_SUCH = "创建mongo库collection：";
    private static final String NO_FOUND_MONGO_COLLECTION = "未找到mongo库collection";
    private static final String INSERT_DOCUMEN_EXCEPTION = "插入文档失败";
    private static final String INSERT_DOCUMEN_SUCCESSS = "插入文档成功";
    private static final Logger logger = LoggerFactory.getLogger(MongoDBAPI.class);

    public MongoDBAPI() { }
    /*
    * 通过用户认证获取mongoDB连接
    * */
    public MongoDatabase getMongoClientByCredential(String address, int PORT, String DBName, String user, String psw) {
        ServerAddress serverAddress = new ServerAddress(address, PORT);
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        addrs.add(serverAddress);
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential.createScramSha1Credential(user, DBName, psw.toCharArray());
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(credential);
        // 通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(addrs, credentials);
        // 连接数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DBName);
        System.out.println("Connect to " + address + ":" + PORT + "/" + DBName + " successfully");
        return mongoDatabase;
    }
    /*
    * 获取Collection
    * */
    public MongoCollection<Document> getMongoCollection(MongoDatabase mongoDatabase, String collection) {
        MongoCollection<Document> collectionDocuments = null;
        try {
            if (mongoDatabase != null) {
                 collectionDocuments = mongoDatabase.getCollection(collection);
            } else {
                throw new RuntimeException("MongoClient不能够为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collectionDocuments;
    }
    /*
    * 获取当前数据库内所有collection的名称
    * */
    public List<String> getAllCollectionNames(MongoDatabase mongoDatabase) {
        List<String> CollectionNames = new ArrayList<>();
        // System.out.println("当前数据库中的所有集合(除了索引)是：");
        for (String name : mongoDatabase.listCollectionNames()) {
            if (!name.equals("system.indexes")) {
                // System.out.println(name);
                CollectionNames.add(name);
            }
        }
        return CollectionNames;
    }
    /*
    * 查询当前集合内所有数据
    * 1. 获取迭代器FindIterable<Document>
    * 2. 获取游标MongoCursor<Document>
    * 3. 通过游标遍历检索出的文档集合
    * */
    public int getTriples(MongoDatabase mongoDatabase, String collectionName, String owlIRI, OntModel ontModel) {
        System.out.println(collectionName); // #
        int sum = 0;
        List<String> tableNames = new ArrayList<>();    // 用于记录当前表所链接到的其他表的表名
        boolean getAllNeedTables = false;  // 为false代表还需要继续通过objectId找表名
       String typeMappingDef = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        MongoCollection<Document> documents = mongoDatabase.getCollection(collectionName);
        FindIterable<Document> findIterable = documents.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            int index = 0;  // 用于跟踪tableNames下标
            Document document = mongoCursor.next();     //一行数据
            ObjectId id = document.getObjectId("_id");  //id
            // 当前实体的IRI
            String objectIRI = owlIRI + collectionName + "/" + id.toString();
            // 1. 当前实体对应owl中哪个概念类(这里默认类名和表名相同)
            String className = owlIRI + collectionName;
            addTriple(ontModel, objectIRI, typeMappingDef, className, 0);
            sum++;
            // 2. 对象属性 & 数据属性
            for (Map.Entry<String, Object> stringObjectEntry : document.entrySet()) {
                String key = stringObjectEntry.getKey();
                String predicateValue = "";
                String objectValue = "";
                if (!key.equals("_id")) {
                    // 判断属性类型
                    String typeName = stringObjectEntry.getValue().getClass().getName();
                    int len = typeName.lastIndexOf(".");
                    String type = typeName.substring(len + 1);
                    if (type.equals("ObjectId") || type.equals("ArrayList")) {     //对象属性(多个对象)
                        // System.out.println("对象属性    " + key);
                        predicateValue = owlIRI + key;
                        // 获取链接到的表的表名
                        String tableName = "";
                        Object obj = stringObjectEntry.getValue();
                        // 若是多个，提取第一个提取一个objectId
                        if (type.equals("ArrayList")) {
                            List<ObjectId> IDlist = (ArrayList<ObjectId>)obj;
                            obj = IDlist.get(0);
                        }
                        if (getAllNeedTables) { //直接从tableNames里获取
                            tableName = tableNames.get(index);
                            index++;
                        } else {
                            tableName = queryInWhichCollection(mongoDatabase, obj); //查找获取
                            tableNames.add(tableName);
                        }
                        System.out.println(tableName);
                        //String tabelName = key.substring(0, key.length() - 2);

                        if (type.equals("ObjectId")) {  // 单个
                            objectValue = owlIRI + tableName + "/" + obj.toString();
                            addTriple(ontModel, objectIRI, predicateValue, objectValue, 0);
                        } else {    // 多个
                            Object objs = stringObjectEntry.getValue();
                            List<ObjectId> IDlist = (ArrayList<ObjectId>)objs;
                            for (ObjectId nowId : IDlist) {
                                objectValue = owlIRI + tableName + "/" + nowId.toString();
                                addTriple(ontModel, objectIRI, predicateValue, objectValue, 0);
                            }
                        }
                    } else {    // 数据属性
                        // System.out.println("数据属性    " + key);
                        predicateValue = owlIRI + key;
                        objectValue = stringObjectEntry.getValue().toString();
                        addTriple(ontModel, objectIRI, predicateValue, objectValue, 1);
                    }
                    sum++;

                }
            }
            // 经过一轮，需要的表名都已经全部按序录入tableNames里了
            getAllNeedTables = true;
        }
        return sum;
    }

    /*
    * 将3元组添加入此model
    * @param propertyType 属性类型（对象属性0 OR 数据属性1）
    * */
    public void addTriple(OntModel ontModel, String subject, String predicate, String object, int propertyType) {
        //存在当前三元组则不添加；
        if (propertyType == 0) {    // 对象属性
            Selector selector = new SimpleSelector(
                    (subject != null) ? ontModel.createResource(subject) : null,
                    (predicate != null) ? ontModel.createProperty(predicate) : null,
                    (object != null) ? ontModel.createResource(object) : null
            );
            StmtIterator it = ontModel.listStatements(selector);
            if (!it.hasNext()) {
                Statement stmt = ontModel.createStatement(
                        ontModel.createResource(subject),
                        ontModel.createProperty(predicate),
                        ontModel.createResource(object));
                ontModel.add(stmt);
            }
        } else {    // 数据属性
            Selector selector = new SimpleSelector(
                    (subject != null) ? ontModel.createResource(subject) : null,
                    (predicate != null) ? ontModel.createDatatypeProperty(predicate) : null,
                    object
            );
            StmtIterator it = ontModel.listStatements(selector);
            if (!it.hasNext()) {
                Statement stmt = ontModel.createStatement(
                        ontModel.createResource(subject),
                        ontModel.createProperty(predicate),
                        object);
                ontModel.add(stmt);
            }
        }
            // console
            // System.out.println(subject + " " + predicate + " " + object);
    }
    /*
    * 查询此ID在哪张表中
    * */
    public String queryInWhichCollection(MongoDatabase mongoDatabase, Object id) {
        String result = "";
        DBObject filter = new BasicDBObject("_id", id);
        // 获取数据库里的所有collection名
        List<String> collectionNames = getAllCollectionNames(mongoDatabase);
        // 遍历每一个collection
        for (String collection : collectionNames) {
            MongoCollection<Document> documents = mongoDatabase.getCollection(collection);
            FindIterable<Document> iter = documents.find((Bson) filter);
            MongoCursor<Document> cursor = iter.iterator();
            if (cursor.hasNext()) { // 存在于当前这个表
                result = collection;
                break;
            }
        }
        return result;
    }

}
