package RDFDemo;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

public class createTDB {
    public static void main(String[] args) {
        // 创建一个TDB数据集
        String directory = "MyDataBases/Dataset1";
        Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.READ);
        Model model = dataset.getDefaultModel();

    }


}
