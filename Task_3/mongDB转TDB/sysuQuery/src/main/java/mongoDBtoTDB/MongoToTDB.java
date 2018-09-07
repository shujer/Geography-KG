package mongoDBtoTDB;

import com.mongodb.client.MongoDatabase;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDBFactory;

import java.util.List;

public class MongoToTDB {
    public MongoToTDB() {
    }

    /*
    * 将owl文件内的数据读入
    * @param .owl文件的路径
    * */
    public void importOWL(OntModel ontModel, String owlPath) {
        ontModel.read(owlPath);
    }

    /*
    * 将mongoDB内的数据读入model
    * @param mongoDB服务器地址
    * @param 端口
    * @param 数据库名
    * @param 用户名
    * @param 密码
    * @param 本体IRI
    * */
    public void importMongoDBDate(String address, int PORT, String DBName, String user, String psw, String owlIRI, OntModel ontModel) {
        // 连接mongoDB服务
        MongoDBAPI mongoDBAPI = new MongoDBAPI();
        MongoDatabase myDB = mongoDBAPI.getMongoClientByCredential(address, PORT, DBName, user, psw);
        // 获取数据库里的所有collection名
        List<String> collectionNames = mongoDBAPI.getAllCollectionNames(myDB);
        // 遍历每一个collection
        int sum = 0;
        for (String collection : collectionNames) {
            int resum = mongoDBAPI.getTriples(myDB, collection, owlIRI, ontModel);
            sum = sum + resum;
        }
        System.out.println("添加了" + sum + "条triple");
    }
    /*
    * 将当前模型存入TDB
    * @param TDB数据库的路径
    * @param 对此model的命名
    * */
    public void saveModelToTDB(String tdbPath, OntModel ontModel, String modelName) {
        // 连接TDB
        Dataset ds = TDBFactory.createDataset(tdbPath);
        ds.begin(ReadWrite.WRITE);
        // 将此model加入TDB
        //ontModel.commit();
        ds.addNamedModel(modelName, ontModel);
        ds.commit();
        //ontModel.close();
        ds.end();
        ds.close();
    }
}
