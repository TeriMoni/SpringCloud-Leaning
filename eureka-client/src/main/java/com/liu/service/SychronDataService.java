package com.liu.service;

import com.liu.mapper.AppInfoMapper;
import com.liu.model.AppInfo;
import com.liu.repository.AppInfoRepository;
import com.liu.repository.UserRepository;
import com.liu.utils.DateUtil;
import com.liu.utils.JsonUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class SychronDataService {

    @Autowired
    AppInfoMapper appInfoMapper;

    private static final Logger logger = LoggerFactory.getLogger(SychronDataService.class);

    private static final Integer PAGENUM = 1000;

    private static List<String> financeIndustryList = new ArrayList<String>();//金融行业list

    static{
        financeIndustryList.add("理财工具");
        financeIndustryList.add("证券");
        financeIndustryList.add("第三方支付");
        financeIndustryList.add("保险");
        financeIndustryList.add("银行");
    }

    public void sychron(String startTime,String endTime,String industry) throws ParseException {
        //循环遍历各个行业
        for (String indus:financeIndustryList) {
            List<String> dateList = DateUtil.getDatesBetweenTwoDate(startTime, endTime);//获取2个时间字符串之间的所有字符串集合
            CloudSolrClient applibClient = connect("127.0.0.1:2181,127.0.0.1:2181");
            applibClient.setDefaultCollection("test_core");
            CloudSolrClient prophetClient = connect("127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181/solr");
            prophetClient.setDefaultCollection("test");
            for (String date : dateList) {
                Integer pageNow = 0;
                while(true){
                    logger.info("当前日期-->{},当前行业-->{}",date,indus);
                    QueryResponse queryResponse = executeQuery(applibClient,getQueryMap(indus,date), pageNow);
                    if(queryResponse == null){
                        break;
                    }
                    SolrDocumentList documentList = queryResponse.getResults();
                    if(documentList.size() == 0){
                        break;
                    }
                    Collection<SolrInputDocument> data = getData(documentList);
                    try {
                        prophetClient.add(data);
                        prophetClient.commit();
                    } catch (SolrServerException | IOException e) {
                        logger.error("solr插入文档出错,当前日期-->{},当前位置-->{}",date,pageNow);
                    }
                    pageNow += PAGENUM;
                }
            }
        }
    }


    private Collection<SolrInputDocument> getData(SolrDocumentList documentList) {
        Collection<SolrInputDocument> collection = new ArrayList<SolrInputDocument>();
        for (SolrDocument solrDocument : documentList) {
            SolrInputDocument inputDocument = new SolrInputDocument();
            for (Map.Entry<String, Object> entry : solrDocument) {
                if(entry.getKey().equals("md5")){
                    inputDocument.addField("apkMD5", entry.getValue());
                }else{
                    inputDocument.addField(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
                }
            }
            collection.add(inputDocument);
        }
        return collection;
    }


    private QueryResponse executeQuery(CloudSolrClient client, Map<String, String[]> map,Integer start ) {
        SolrQuery query = new SolrQuery();
        QueryResponse response = null;
        try {
            for (Map.Entry<String, String[]> entry : map.entrySet()) {
                query.set(entry.getKey(), entry.getValue());
            }
            query.setFields("md5,channel, provinceName, cityName, industry, legalCopy, legalCopyTime,adScore,high,medium,low,isProtect,icon,label,storageTime,creatTime,adTime,cmTime,download,location,protectProducer");
            query.setStart(start);
            query.setRows(PAGENUM);
            response = client.query(query);
        } catch (Exception e) {
            logger.info("综合查询solr查询报错", e);
        }
        return response;
    }

    /**
     * 组装solr查询请求参数
     * @param industry 行业
     * @param date 开始时间
     * @return
     */
    public Map<String, String[]> getQueryMap(String industry, String date){
        Map<String, String[]> queryMap = new HashMap<String, String[]>();
        String today = date+ " 23:59:59";
        String yesterday = date+ " 00:00:00";
        queryMap.put("q", new String[]{String.format("createTime:[\"%s\" TO \"%s\"] AND industry:%s",yesterday,today,industry)});
        return queryMap;
    }


    public Map<String, String[]> getQueryString(){
        Map<String, String[]> queryMap = new HashMap<String, String[]>();
        queryMap.put("q", new String[]{"*:*"});
        return queryMap;
    }

    public CloudSolrClient connect(String zkHostString) {
        CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHostString).build();
        return client;
    }

    public void permission() {
        CloudSolrClient applatestClient= connect("192.168.113.123:2181,192.168.113.124:2181");
        applatestClient.setDefaultCollection("applatest_core");
        Integer pageNow = 0;
        while(true){
            logger.info("当前索引位置-->{}",pageNow);
            QueryResponse queryResponse = query(applatestClient,getQueryString(), pageNow);
            if(queryResponse == null){
                break;
            }
            SolrDocumentList documentList = queryResponse.getResults();
            if(documentList.size() == 0){
                break;
            }
            List<AppInfo> data = getAppInfo(documentList);
            appInfoMapper.batchUpdataData(data);
            pageNow += PAGENUM;
        }
    }

    private  List<AppInfo> getAppInfo(SolrDocumentList documentList) {
        List<AppInfo> result = new ArrayList<AppInfo>();
        for (SolrDocument solrDocument : documentList) {
            AppInfo appInfo = new AppInfo();
            List<String> persmission = (List<String>) solrDocument.getFieldValue("permission");
            appInfo.setAppName(solrDocument.getFieldValue("name") == null ? "" : (String) solrDocument.getFieldValue("name"));
            appInfo.setPermission(persmission == null ? "" : JsonUtil.getJsonString4JavaList(persmission));
            result.add(appInfo);
        }
        return  result;
    }

    private QueryResponse query(CloudSolrClient client, Map<String, String[]> map,Integer start ) {
        SolrQuery query = new SolrQuery();
        QueryResponse response = null;
        try {
            for (Map.Entry<String, String[]> entry : map.entrySet()) {
                query.set(entry.getKey(), entry.getValue());
            }
            query.setFields("md5,permission,name");
            query.setStart(start);
            query.setRows(PAGENUM);
            response = client.query(query);
        } catch (Exception e) {
            logger.info("综合查询solr查询报错", e);
        }
        return response;
    }
}
