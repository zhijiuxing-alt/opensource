package com.oceanbase.opensource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
@Controller
public class YamlToJson {

    @Value("${OBYmlPath}")
    private String OBYmlPath;

    @Value("${OBTimePath}")
    private String OBTimePath;

    @Value("${BaseUrl}")
    private String BaseUrl;

    private static JsonParser p = new JsonParser();

    @Autowired
    FileModifyTime fileModifyTime;

    @RequestMapping("metaJson")
    @ResponseBody
    public FNode metaJson() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        java.util.Map<String, Object> obj = (java.util.Map<String, Object>) yaml.load(getYml(OBYmlPath));
        String res = new Gson().toJson(obj);
        JsonElement e = p.parse(res);
        FNode fNode = new FNode();
        fNode.setTitle("OceanBase数据库文档");
        fNode.setDescription("OceanBase数据库文档");
        createMetaJson(e,fNode);
        return fNode;
    }

    @RequestMapping("mainJson")
    @ResponseBody
    public MainJson mainJson() throws FileNotFoundException {

        MainJson mainJson = new MainJson();
        mainJson.setTarget("cean_base_open_cn");
        mainJson.setLanguage("zh-CN");
        createMainJson(mainJson);
        return mainJson;

    }

    private void createMetaJson(JsonElement e, FNode fNode)
    {
        if (e.isJsonNull())
        {
            return;
        }

        if (e.isJsonArray())
        {
            JsonArray ja = e.getAsJsonArray();
            if (null != ja)
            {
                for (JsonElement ae : ja)
                {
                    Set<java.util.Map.Entry<String, JsonElement>> es = ae.getAsJsonObject().entrySet();
                    for (java.util.Map.Entry<String, JsonElement> en : es)
                    {
                        if (en.getValue().isJsonArray())
                        {
                            FNode newNode = new FNode();
                            newNode.setTitle(en.getKey());
                            newNode.setDescription(en.getKey());
                            fNode.getDocList().add(newNode);
                            createMetaJson(en.getValue(),newNode);
                        }else {
                            SNode sNode = new SNode();
                            sNode.setTitle(en.getKey());
                            sNode.setSlug(en.getValue().toString().replace(".md","").replace("\"",""));
                            sNode.setKey(BaseUrl + sNode.getSlug());
                            sNode.setModifyTime(fileModifyTime.getOBFileModifyTime(sNode.getSlug()));
                            fNode.getDocList().add(sNode);
                        }
                    }
                }
            }
            return;
        }

        if (e.isJsonObject())
        {
            Set<java.util.Map.Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();
            for (java.util.Map.Entry<String, JsonElement> en : es)
            {
                createMetaJson(en.getValue(),fNode);
            }
        }
    }

    private String getYml(String OBYmlPath) {
        boolean flag = false;
        File file = new File(OBYmlPath);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr = null;
            String res = "";
            while ((tempStr = reader.readLine()) != null) {
                if(tempStr.contains("nav:"))
                {
                    flag = true;
                }
                if (flag == true)
                {
                    res+=tempStr+"\n";
                }
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    private void createMainJson(MainJson mainJson)
    {
        List<Product> productList = new ArrayList<>();
        mainJson.setProductList(productList);
        Product product = new Product();
        productList.add(product);
        product.setTitle("OceanBase数据库文档");
        Yaml yaml = new Yaml();
        java.util.Map<String, Object> obj = (java.util.Map<String, Object>) yaml.load(getYml(OBYmlPath));
        String res = new Gson().toJson(obj);
        JsonElement e = p.parse(res);
        product.setMapList(getMapList(e));
    }

    private List<Map> getMapList(JsonElement e)
    {
        FNode fNode = new FNode();
        fNode.setTitle("OceanBase数据库文档");
        fNode.setDescription("OceanBase数据库文档");
        createMetaJson(e,fNode);
        List<Map> mapList = new ArrayList<>();

        if (e.isJsonNull())
        {
            return null;
        }

        if (e.isJsonObject())
        {
            Set<java.util.Map.Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();
            //获得到nav
            for (java.util.Map.Entry<String, JsonElement> en : es)
            {
                e = en.getValue();
                if (e.isJsonArray())
                {
                    JsonArray firstJs = e.getAsJsonArray();
                    if (null != firstJs)
                    {
                        //遍历nav
                        for (JsonElement first : firstJs)
                        {
                            Set<java.util.Map.Entry<String, JsonElement>> firstNodes = first.getAsJsonObject().entrySet();
                            for (java.util.Map.Entry<String, JsonElement> firstNode : firstNodes)
                            {
                                //获得到nav下面每一级 即每个具体的 map
                                Map map = new Map();
                                map.setTitle(firstNode.getKey());
                                map.setProduct("OceanBase开源部");
                                map.setDescription(firstNode.getKey());
                                List<Version> versionList = new ArrayList<>();
                                map.setVersionList(versionList);
                                Version version = new Version();
                                versionList.add(version);
                                List<SNode> docList = new ArrayList<>();
                                version.setDocList(docList);
                                version.setTitle(firstNode.getKey());
                                version.setVersion("0.0.1");
                                mapList.add(map);
                                e=firstNode.getValue();
                                if (e.isJsonArray()){
                                    JsonArray secondja = e.getAsJsonArray();
                                    if (null != secondja)
                                    {
                                        // 获得到每个map中具体的目录
                                        for (JsonElement second : secondja)
                                        {
                                            Set<java.util.Map.Entry<String, JsonElement>> secondNodes = second.getAsJsonObject().entrySet();
                                            for (java.util.Map.Entry<String, JsonElement> secondNode : secondNodes)
                                            {
                                                SNode sNode = new SNode();
                                                sNode.setTitle(secondNode.getKey());
                                                sNode.setSlug(getPath(fNode,secondNode.getKey()));
                                                sNode.setKey(BaseUrl+sNode.getSlug());
                                                sNode.setModifyTime(fileModifyTime.getOBFileModifyTime(sNode.getSlug()));
                                                docList.add(sNode);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return mapList;
    }

    /**
     * 获得左侧导航栏对应路径
     * @param fNode
     * @param key
     * @return
     */
    private String getPath(FNode fNode,String key)
    {
        if (fNode.getDocList().size() > 0) {
            List<Object> objs = fNode.getDocList();
            Queue nodes = new LinkedList<>();
            nodes.addAll(objs);

            while (nodes.size() > 0) {
                Object node = nodes.poll();
                if (null != node) {

                    if (node instanceof FNode) {
                        if (((FNode) node).getTitle().equals(key))
                        {
                            return getFNodePath((FNode) node,key);
                        }
                        nodes.addAll(((FNode) node).getDocList());
                    }else if (node instanceof SNode)
                    {
                        if (((SNode) node).getTitle().equals(key))
                        {
                            return ((SNode) node).getSlug();
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getFNodePath(FNode fNode,String key)
    {
        if (fNode.getDocList().size() > 0) {
            List<Object> objs = fNode.getDocList();
            Queue nodes = new LinkedList<>();
            nodes.addAll(objs);

            while (nodes.size() > 0) {
                Object node = nodes.poll();

                if (node instanceof FNode) {
                    nodes.addAll(((FNode) node).getDocList());
                }

                if (node instanceof SNode)
                {
                    return ((SNode) node).getSlug();
                }
            }
        }
        return null;
    }

    private class MainJson{

        private String target;

        private String language;

        private List<Product> productList;

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public List<Product> getProductList() {
            return productList;
        }

        public void setProductList(List<Product> productList) {
            this.productList = productList;
        }

        @Override
        public String toString() {
            return "main{" +
                    "target='" + target + '\'' +
                    ", language='" + language + '\'' +
                    ", productList=" + productList +
                    '}';
        }
    }

    private class Product{

        private String title;

        private List<com.oceanbase.opensource.YamlToJson.Map> mapList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<com.oceanbase.opensource.YamlToJson.Map> getMapList() {
            return mapList;
        }

        public void setMapList(List<com.oceanbase.opensource.YamlToJson.Map> mapList) {
            this.mapList = mapList;
        }

        @Override
        public String toString() {
            return "product{" +
                    "title='" + title + '\'' +
                    ", mapList=" + mapList +
                    '}';
        }
    }

    private class Map{

        private String title;

        private String product;

        private String description;

        private List<Version> versionList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Version> getVersionList() {
            return versionList;
        }

        public void setVersionList(List<Version> versionList) {
            this.versionList = versionList;
        }

        @Override
        public String toString() {
            return "Map{" +
                    "title='" + title + '\'' +
                    ", product='" + product + '\'' +
                    ", description='" + description + '\'' +
                    ", versionList=" + versionList +
                    '}';
        }
    }

    private class Version{

        private String id;

        private String version;

        private String tag;

        private String title;

        private List<SNode> docList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SNode> getDocList() {
            return docList;
        }

        public void setDocList(List<SNode> docList) {
            this.docList = docList;
        }

        @Override
        public String toString() {
            return "version{" +
                    "id='" + id + '\'' +
                    ", version='" + version + '\'' +
                    ", tag='" + tag + '\'' +
                    ", title='" + title + '\'' +
                    ", docList=" + docList +
                    '}';
        }
    }

    private class FNode{

        private String title;

        private String description;

        private List<Object> docList=new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Object> getDocList() {
            return docList;
        }

        public void setDocList(List<Object> docList) {
            this.docList = docList;
        }

        @Override
        public String toString() {
            return "FNode{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", docList=" + docList +
                    '}';
        }
    }

    private class SNode{

        private String key;

        private String title;

        private String slug;

        private Date modifyTime;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        private void jsonTree(JsonElement e,FNode fNode)
        {
            if (e.isJsonNull())
            {
                return;
            }

            if (e.isJsonArray())
            {
                JsonArray ja = e.getAsJsonArray();
                if (null != ja)
                {
                    for (JsonElement ae : ja)
                    {
                        Set<java.util.Map.Entry<String, JsonElement>> es = ae.getAsJsonObject().entrySet();
                        for (java.util.Map.Entry<String, JsonElement> en : es)
                        {
                            if (en.getValue().isJsonArray())
                            {
                                FNode newNode = new FNode();
                                newNode.setTitle(en.getKey());
                                newNode.setDescription(en.getKey());
                                fNode.getDocList().add(newNode);
                                jsonTree(en.getValue(),newNode);
                            }else {
                                SNode sNode = new SNode();
                                sNode.setTitle(en.getKey());
                                sNode.setSlug(en.getValue().toString().replace(".md",""));
                                sNode.setKey(BaseUrl + sNode.getSlug());
                                fNode.getDocList().add(sNode);
                            }
                        }
                    }
                }
                return;
            }

            if (e.isJsonObject())
            {
                Set<java.util.Map.Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();
                for (java.util.Map.Entry<String, JsonElement> en : es)
                {
                    jsonTree(en.getValue(),fNode);
                }
            }
        }

        private String getYml(String OBYmlPath) {
            boolean flag = false;
            File file = new File(OBYmlPath);
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer();
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempStr = null;
                String res = "";
                while ((tempStr = reader.readLine()) != null) {
                    if(tempStr.contains("nav:"))
                    {
                        flag = true;
                    }
                    if (flag == true)
                    {
                        res+=tempStr+"\n";
                    }
                }
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return null;
        }

        public Date getModifyTime() { return modifyTime; }

        public void setModifyTime(Date modifyTime) {this.modifyTime = modifyTime;}

        @Override
        public String toString() {
            return "SNode{" +
                    "key='" + key + '\'' +
                    ", title='" + title + '\'' +
                    ", slug='" + slug + '\'' +
                    ", modifyTime=" + modifyTime +
                    '}';
        }
    }

}
