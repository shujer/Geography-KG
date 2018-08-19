package RDFDemo;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TDBPortal {
    final static Logger logger = LoggerFactory.getLogger(TDBPortal.class);
    // Dataset最后要记得close
    Dataset ds = null;
    /*
    * 创建TDB
    * */
    public TDBPortal(String tdbPath, boolean useAssemblerFile) {
        if (!useAssemblerFile) {
            Location location = Location.create(tdbPath);
            ds = TDBFactory.createDataset(location);
        } else {
            ds = TDBFactory.assembleDataset(tdbPath);
        }
    }
    public  TDBPortal(String tdbPath) {
        Location location = Location.create(tdbPath);
        ds = TDBFactory.createDataset(location);
    }
    /*
    * 往model中添加内容
    * @param modelURI 本体的URI
    * @param sourcePath 本体文件的路径
    * @param override 是否覆盖
    * */
    public int loadModel(String modelURI, String sourcePath, Boolean override) {
        Model model = null;
        ds.begin(ReadWrite.WRITE);
        try {
            if (ds.containsNamedModel(modelURI)) {
                if (override) {   //覆盖
                    removeModel(modelURI);  //只移除地址，未移除实际数据
                    loadModel(modelURI, sourcePath, false);
                }
            } else {
                model = ds.getNamedModel(modelURI); //没有则创建一个
                model.begin();
                RDFDataMgr.read(model, sourcePath);
                model.commit();
            }
            ds.commit();
            logger.info("本体模型数据已经导入");
            return 1;
        } catch (Exception e) {
            return 0;
        } finally {
            if (model != null) {
                model.close();
            }
            ds.end();
        }
    }
    /*
    * 导入本体 OntModel，不支持事务
    * @param modelURI 本体的URI
    * @param sourcePath 本体文件的路径
    * @param override 是否覆盖
    * */
    public int loadOntModel(String modelURI, String sourcePath, Boolean override) {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        ds.begin(ReadWrite.WRITE);
        try {
            if (ds.containsNamedModel(modelURI)) {
                if (override) {   //覆盖
                    removeModel(modelURI);  //只移除地址，未移除实际数据
                    loadOntModel(modelURI, sourcePath, false);
                }
            } else {
                FileManager.get().readModel(model, sourcePath);
                //model.read(sourcePath);  //导入本体文件
                ds.addNamedModel(modelURI, model);
            }
            ds.commit();
            System.out.println(modelURI + "已导入");
            logger.info(modelURI + "已导入");
            return 1;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            logger.error(e.getLocalizedMessage());
            return 0;
        } finally {
            ds.end();
        }
    }

    public Model getDefaultModel() {
        ds.begin(ReadWrite.READ);
        Model model;
        try
        {
            model = ds.getDefaultModel();
            ds.commit();
        } finally {
            ds.end();
        }
        return model;
    }
    /*
    * 获取指定模型
    * */
    public Model getModel(String modelURI) {
        ds.begin(ReadWrite.READ);
        Model model;
        try
        {
            model = ds.getNamedModel(modelURI);
        } finally {
            ds.end();
        }
        return model;
    }

    public void loadDefaultModel(String sourcePath) {
        Model model = null;
        ds.begin(ReadWrite.WRITE);
        try {
            model = ds.getDefaultModel();
            model.begin();
            if (!StringUtils.isBlank(sourcePath))
                RDFDataMgr.read(model, sourcePath);
                model.commit();
                ds.commit();
            }
        finally {
            if (model != null)
                model.close();
            ds.end();
        }
    }
    public void removeModel(String modelUri) {
        if (!ds.isInTransaction())
            ds.begin(ReadWrite.WRITE);
        try {
            ds.removeNamedModel(modelUri);
            ds.commit();
            System.out.println(modelUri + " 已被移除");
            logger.info(modelUri + " 已被移除");
        } finally {
            ds.end();
        }
    }
    /*
     * 列出所有模型的uri
     */
    public List<String> listModels() {
        ds.begin(ReadWrite.READ);
        List<String> uriList = new ArrayList<>();
        try {
            Iterator<String> names = ds.listNames();// DefaultModel没有name
            String name = null;
            while (names.hasNext()) {
                name = names.next();
                uriList.add(name);
            }
        } finally {
            ds.end();
        }
        return uriList;
    }
    /*
    * 关闭TDB
    * */
    public void close() {
        ds.close();
    }

}
