package test;

import mongoDBtoTDB.MongoDBAPI;
import com.mongodb.client.MongoDatabase;
import org.junit.Test;
import org.bson.types.ObjectId;

public class FindTableNameTest {

    @Test
    /*
    * 测试通过ObjectId查找所处表名的queryInWhichCollection()
    * */

    public void tableNameTest() {
        String address = "ds113693.mlab.com";
        int PORT = 13693;
        String DBName = "geokg";
        String user = "sysu";
        String password = "sysu2018";
        String owlIRI = "http://www.geokg.com/";
        String modelName = "http://www.Graph.com/geokgData"; // 命名图 -> 最好用绝对路径
        String owlPath = "myData\\geokg.owl";
        String tdbPath = "DataBase\\geokg_TDB";
        MongoDBAPI mongoDBAPI = new MongoDBAPI();
        MongoDatabase myDB = mongoDBAPI.getMongoClientByCredential(address, PORT, DBName, user, password);
        String testId = "5bb8a7c33fe3e52c9cc28fcb";
        String table = mongoDBAPI.queryInWhichCollection(myDB, new ObjectId(testId));
        System.out.println(table);

    }
}
