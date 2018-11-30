package com.yzsj.neteco.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yzsj.neteco.util.InitHttpClient;
import com.yzsj.neteco.util.ParseResponse;
import com.yzsj.neteco.util.QueryHttpsResult;
import com.yzsj.neteco.util.RedisManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class InitPmdata {

    @Autowired(required = false)
    private ConfigUtil configUtil;
    @Autowired(required = false)
    private RedisManager redisManager;

    private String openId = "";
    private HttpClient httpClient;
    String url = "";
    List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

    /**
     * 启动时自动初始化一个openId和httpClient
     */
    @PostConstruct
    public void initOpenIdAndHttpClient() {
        httpClient =new InitHttpClient().createSSLClientDefault(configUtil.getPort());
        if(openId.equals("")){
            openId = getOpenId(configUtil.getIp(), configUtil.getPort(), "/rest/openapi/sm/session");
        }
        url = "https://" + configUtil.getIp() + ":" + configUtil.getPort() ;
       /* parameters.add(new BasicNameValuePair("userid", configUtil.getUserid()));
        parameters.add(new BasicNameValuePair("value", configUtil.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", configUtil.getIpAddress()));*/
        headers.add(new BasicNameValuePair("openid", openId));
//        headers.add(new BasicNameValuePair("pageSize", "3000"));
//        Map<String, String> retMap = getPmdata();

//        getKpiForObject();
        System.out.println("1");
    }


//    @Scheduled(initialDelay = 10000, fixedDelay = 5000)
    public void publishData() {
        Map<String, String> retMap = getPmdata();
        for (Map.Entry<String, String> entry : retMap.entrySet()) {
            String publishValue = "{\"id\":" + entry.getKey() + ",\"f\":0,\"p\":2,\"t\":0,\"v\":" + entry.getValue() + "}";
            redisManager.publish("chan:from_all_to_uckernal:value", publishValue);
        }

    }

    public Map<String, String> getPmdata() {

        List<BasicNameValuePair> KPIHeaders = headers;
        KPIHeaders.add(new BasicNameValuePair("params", "{\"pageIndex\":" + 1 + ",\"pageSize\":" + 4000 + "}"));
        String KPIData =  new QueryHttpsResult().
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmdata",KPIHeaders,null,"get");
        List<Map<String,Object>> kpiListAll = new ArrayList<>() ;
        int totalPage = (int)((Map<String,Object>) JSON.parse(KPIData)).get("totalPage");

        for (int i = 1;i <= totalPage;i++){
            headers.add(new BasicNameValuePair("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
            KPIData =  new QueryHttpsResult().
                    getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmdata",headers,null,"get");
            Map<String,String> KpiDataMap = new ParseResponse().getParseResponse(KPIData);
            kpiListAll.addAll ((List<Map<String,Object>>)JSONArray.parse(KpiDataMap.get("data")));

        }

        HttpGet httpGet = new HttpGet(url+configUtil.getUrl());
        if (null != headers) {
            for (BasicNameValuePair header : headers) {
                httpGet.setHeader(header.getName(), header.getValue());
            }
        }
        Map<String, String> retMap = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String ret = EntityUtils.toString(entity);
            if (null == ret || ret.isEmpty()) {
                return null;
            }
            retMap =new ParseResponse().getParseResponse(ret);
            return retMap;
            /*if (retMap.get("code").equals("0")) {
                //调用方法将结果集转化为推送数据
                String result =  retMap.get("data");
//                Map<String, String> map = transMap(result);
                List<Map<String,Object>> data= (List<Map<String,Object>>) JSONArray.parse(retMap.get("data"));
                String neId = "41000";
                HashMap<String, Map<String, Object>> stringMapHashMap = new HashMap<>();
                for(Map<String,Object> d : data){
                    stringMapHashMap.put(String.valueOf(d.get("neTypeID")),d);
                }
                Map<String, Object> stringObjectMap = stringMapHashMap.get(neId);
                System.out.println(stringObjectMap.get("resultTime"));

            } else {
                System.out.println("error!!! " + retMap.toString());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

/**
 * 根据ip 端口 url获取openId;
 * @param ip
 * @param port
 * @parm openidURL
 * @return
 * */
    public   String getOpenId( String ip , int port, String openidURL){
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("userid", configUtil.getUserid()));
        parameters.add(new BasicNameValuePair("value", configUtil.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", configUtil.getIpAddress()));

        Map<String ,String> retMap = null;
        try {

            String url = "https://" + ip + ":" + port + openidURL;
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
            HttpResponse response = httpClient.execute(httpPut);

            HttpEntity entity = response.getEntity();
            if(entity != null){
                retMap = new ParseResponse().getParseResponse(EntityUtils.toString(entity));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.getConnectionManager().shutdown();
        }
        if (retMap.get("code").equals("0"))
        {
            return retMap.get("data");
        }
        return "";
    }



    public void  queryHttpsTest(){

        String alarmData =  new QueryHttpsResult().
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmdata",headers,parameters,"get");
        Map<String ,String > retMap =new ParseResponse().getParseResponse(alarmData);
        List<Map<String,Object>> data= (List<Map<String,Object>>) JSONArray.parse(retMap.get("data"));
        String neId = "41000";
        HashMap<String, Map<String, Object>> stringMapHashMap = new HashMap<>();
        for(Map<String,Object> d : data){
            stringMapHashMap.put(String.valueOf(d.get("neTypeID")),d);
        }
        Map<String, Object> stringObjectMap = stringMapHashMap.get(neId);
        String instanceData = new QueryHttpsResult().
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/instancenode",headers,parameters,"get");
        System.out.println(alarmData+"--------------------" + instanceData);
    }

/**
 * 根据获取的对象DN来过去相应的指标
 * */
    public void  getKpiForObject(){
        //获取管理对象数据
        //设置管理对象头
        List<BasicNameValuePair> instanHeaders = headers;
        instanHeaders.add(new BasicNameValuePair("params", "{\"pageIndex\":" + 1 + ",\"pageSize\":" + 4000 + "}"));

        String instanceData = new QueryHttpsResult().
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/instancenode",instanHeaders,null,"get");
        List<Map<String,Object>> instListAll = new ArrayList<>() ;
        int instTotalPage = (int)((Map<String,Object>) JSON.parse(instanceData)).get("totalPage");

        for (int i = 1;i <= instTotalPage;i++){
            List<BasicNameValuePair> instanHeaders1 = headers;
            instanHeaders1.add(new BasicNameValuePair("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
            instanceData =  new QueryHttpsResult().
                    getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/instancenode",instanHeaders1,null,"get");
            Map<String,String> instDataMap = new ParseResponse().getParseResponse(instanceData);
            instListAll.addAll ((List<Map<String,Object>>)JSONArray.parse(instDataMap.get("data")));
        }
        Map<String ,Map<String ,Object>> instanceDataMap = new HashMap<>();
        for (Map<String ,Object> map : instListAll ) {
            instanceDataMap.put(map.get("dn").toString(),map);
        }


        //获取指标管理数据
        //设置管理对象头
        List<BasicNameValuePair> pmcounterHeaders = headers;
        pmcounterHeaders.add(new BasicNameValuePair("params", "{\"pageIndex\":" + 1 + ",\"pageSize\":" + 4000 + "}"));

        String pmcounteData = new QueryHttpsResult().
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmcounter",pmcounterHeaders,null,"get");
        List<Map<String,Object>> pmcounteListAll = new ArrayList<>() ;
        int pmcounteTotalPage = (int)((Map<String,Object>) JSON.parse(pmcounteData)).get("totalPage");

        for (int i = 1;i <= pmcounteTotalPage;i++){
            List<BasicNameValuePair> pmcounterHeaders1 = headers;
            pmcounterHeaders.add(new BasicNameValuePair("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
            pmcounteData =  new QueryHttpsResult().
                    getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmcounter",pmcounterHeaders1,null,"get");
            Map<String,String> instDataMap = new ParseResponse().getParseResponse(pmcounteData);
            pmcounteListAll.addAll ((List<Map<String,Object>>)JSONArray.parse(instDataMap.get("data")));
        }
        Map<String ,Map<String ,Object>> pmcounteDataMap = new HashMap<>();
        for (Map<String ,Object> map : pmcounteListAll ) {
            pmcounteDataMap.put(map.get("counterId").toString(),map);
        }



        //获取指标数据
        List<BasicNameValuePair> KPIHeaders = headers;
        KPIHeaders.add(new BasicNameValuePair("params", "{\"pageIndex\":" + 1 + ",\"pageSize\":" + 4000 + "}"));
        String KPIData =  new QueryHttpsResult().
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmdata",KPIHeaders,null,"get");
        List<Map<String,Object>> kpiListAll = new ArrayList<>() ;
        int totalPage = (int)((Map<String,Object>) JSON.parse(KPIData)).get("totalPage");

        for (int i = 1;i <= totalPage;i++){
            headers.add(new BasicNameValuePair("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
            KPIData =  new QueryHttpsResult().
                    getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/pmdata",headers,null,"get");
            Map<String,String> KpiDataMap = new ParseResponse().getParseResponse(KPIData);
            kpiListAll.addAll ((List<Map<String,Object>>)JSONArray.parse(KpiDataMap.get("data")));

        }

        //遍历指标数据，创建最终map集合
        File file = new File("d:/资产指标数据.txt");
        String result = "";
        HashMap<String, Map<String, Object>> resultHashMap = new HashMap<>();
        for (Map<String,Object> map: kpiListAll) {
                result += map.get("dn") +" " +/*(instanceDataMap.get(map.get((instanceDataMap.get(map.get("dn"))).get("parentId")))).get("name")*/(instanceDataMap.get(map.get("dn"))).get("parentName")+ (instanceDataMap.get(map.get("dn"))).get("name")+"  " + map.get("counterId")+ "  " + (pmcounteDataMap.get( map.get("counterId"))).get("name") +"\r\n";

        }
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file,result );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}


