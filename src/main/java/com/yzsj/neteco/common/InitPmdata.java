package com.yzsj.neteco.common;

import com.alibaba.fastjson.JSONArray;
import com.yzsj.neteco.util.InitHttpClient;
import com.yzsj.neteco.util.ParseResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
    Config config;
    @Autowired(required = false)
    RedisManager redisManager;
    @Autowired(required = false)
    InitHttpClient initHttpClient;
    @Autowired(required = false)
    ParseResponse parseResponse;
    @Autowired(required = false)
    OpenId openIdObj;

    private String openId = "";
    private HttpClient httpClient;
    String url = "";
    List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

    /**
     * 启动时自动初始化一个openId和httpClient
     */
//    @PostConstruct
    public void initOpenIdAndHttpClient() {
        openId = openIdObj.getOpenId(config.getIp(), config.getPort(), config.getUrl());
        httpClient = initHttpClient.createSSLClientDefault(config.getPort());
        url = "https://" + config.getIp() + ":" + config.getPort() + config.getUrl();
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("userid", config.getUserid()));
        parameters.add(new BasicNameValuePair("value", config.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", config.getIpAddress()));
        try {
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
        }
        List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
        headers.add(new BasicNameValuePair("openid", openId));
        headers.add(new BasicNameValuePair("pageSize", "4000"));

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
        HttpGet httpGet = new HttpGet(url);
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
            retMap = parseResponse.getParseResponse(ret);
            if (retMap.get("code").equals("0")) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }




}


