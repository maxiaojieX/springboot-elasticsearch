package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.Movie;
import com.example.demo.config.ElConfig;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by xiaojie.Ma on 2018/12/11.
 */
public class ElUtil {

    /**
     * 添加
     * @param object
     * @param database
     * @param tableName
     */
    public static void insertIntoEs(Object object,String database,String tableName) {
        Map<String,Object> map = JSON.parseObject(JSONObject.toJSONString(object),HashMap.class);
        try {

            IndexResponse indexResponse = ElConfig.geTransportClient().prepareIndex()
                    .setIndex(database)
                    .setType(tableName)
                    .setSource(map)
                    .execute()
                    .actionGet();
            System.out.println(indexResponse);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id查询
     * @param database
     * @param tableName
     * @param id
     * @return
     */
    public static Object queryById(String database,String tableName,String id) {
        GetResponse getResponse = null;
        try {
            getResponse = ElConfig.geTransportClient().prepareGet()
                    .setIndex(database)
                    .setType(tableName)
                    .setId(id)
                    .execute()
                    .actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("get="+getResponse.getSourceAsString());
        Object object = JSON.parseObject(getResponse.getSourceAsString());
        return object;
    }

    /**
     * 全匹配搜索
     * @param database
     * @param condition
     * @return
     */
    public static List<Object> queryByConditionMustEqual(String database, Map condition){

        QueryBuilder query;
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Iterator it = condition.keySet().iterator();
        while (it.hasNext()){
            String key = (String) it.next();
            boolQueryBuilder.must( QueryBuilders.matchQuery(key, condition.get(key)) );

        }
        query = boolQueryBuilder;

        SearchResponse searchResponse = null;
        try {
            searchResponse = ElConfig.geTransportClient().prepareSearch(database)
                    .setQuery(query)
                    .setFrom(0).setSize(10)
                    .execute()
                    .actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //SearchHits是SearchHit的复数形式，表示这个是一个列表
        List<Object> objects = new ArrayList<>();
        SearchHits shs = searchResponse.getHits();
        for(SearchHit hit : shs){
            objects.add(JSON.parseObject(hit.getSourceAsString()));
        }
        return objects;
    }

    /**
     * 全文搜索（模糊匹配）
     * @param database
     * @param content
     */
    public static List<Object> queryAll(String database,String content){
        QueryBuilder query = QueryBuilders.queryStringQuery(content);
        SearchResponse searchResponse = null;
        try {
            searchResponse = ElConfig.geTransportClient().prepareSearch(database)
                    .setQuery(query)
                    .setFrom(0).setSize(10)
                    .execute()
                    .actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //SearchHits是SearchHit的复数形式，表示这个是一个列表
        SearchHits shs = searchResponse.getHits();
        if(shs==null || shs.getHits().length<=0) {
            return null;
        }
        List<Object> objects = new ArrayList<>();
        for(SearchHit hit : shs){
            System.out.println(hit.getId());
            objects.add(JSON.parseObject(hit.getSourceAsString()));
        }
        return objects;
    }

    public static void delete(String datase,String table,String id){

        DeleteResponse delResponse = null;
        try {
            delResponse = ElConfig.geTransportClient().prepareDelete()
                    .setIndex(datase)
                    .setType(table)
                    .setId(id)
                    .execute()
                    .actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("del id found="+delResponse.getResult());
    }

    public static void update(String database,String table,String id,Object object){
        Map<String,Object> map = JSON.parseObject(JSONObject.toJSONString(object),HashMap.class);

        UpdateResponse updateResponse = null;
        try {
            updateResponse = ElConfig.geTransportClient().prepareUpdate()
                    .setIndex(database)
                    .setType(table)
                    .setDoc(map)
                    .setId(id)
                    .execute()
                    .actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("更新成功， isUpdated="+updateResponse.getResult());
    }

    public static void main(String[] args) {
        Movie movie = new Movie();
        movie.setId(12);
        movie.setName("海王6666");

//        AWecHUlJaxFNa7Zxd434
//                AWecJOr8axFNa7Zxd436
//        insertIntoEs(movie,"spider","movie_resp");
//        Object o = queryById("spider", "movie_resp", "11");
        Map map = new HashMap();
        map.put("name","海22王");
//        List<Object> objects = queryByConditionMustEqual("spider", map);
        List<Object> objects = queryAll("spider", "海");
        System.out.println(objects);
//        System.out.println(o);
//        delete("spider","movie_resp","AWecJOr8axFNa7Zxd436");
        update("spider","music_resp","AWecJOr8axFNa7Zxd436",movie);
    }

}
