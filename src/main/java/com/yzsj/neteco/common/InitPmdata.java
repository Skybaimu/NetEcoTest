package com.yzsj.neteco.common;

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
//        openId = getOpenId(configUtil.getIp(), configUtil.getPort(), "/rest/openapi/sm/session");
        openId = "7080fab6a6d4f9f05e366b9462c5fe7c477bb3a81aef708e";
        url = "https://" + configUtil.getIp() + ":" + configUtil.getPort() ;
        parameters.add(new BasicNameValuePair("userid", configUtil.getUserid()));
        parameters.add(new BasicNameValuePair("value", configUtil.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", configUtil.getIpAddress()));
        /*try {
            if (null != parameters) {
                url += "?";
                boolean init = false;
                for (BasicNameValuePair e : parameters) {
                    if (!init) {
                        url += URLEncoder.encode(e.getName(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                        init = true;
                    } else {
                        url += "&" + URLEncoder.encode(e.getName(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
//        List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
        headers.add(new BasicNameValuePair("openid", openId));
        headers.add(new BasicNameValuePair("pageSize", "4000"));
        Map<String, String> retMap = getPmdata();
        Map<String, String> retMap1 = getPmdata();
//        queryHttpsTest();
        System.out.println(retMap);
        System.out.println(retMap1);
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


    public   String getOpenId( String ip , int port, String openidURL){
//        ConfigUtil configUtil = new ConfigUtil();
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
                getHttpsResult(configUtil.getPort(),url + "/rest/openapi/neteco/instancenode",headers,parameters,"get");
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
        System.out.println("--------------------" + instanceData);
    }



}


