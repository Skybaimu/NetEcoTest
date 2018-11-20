package com.yzsj.neteco.common;

import com.yzsj.neteco.util.InitHttpClient;
import com.yzsj.neteco.util.ParseResponse;
import com.yzsj.neteco.util.RedisManager;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Component
public class InitPmdata {

    @Autowired(required=false)
    Config config;
    @Autowired(required=false)
    RedisManager redisManager;
    @Autowired(required=false)
    InitHttpClient initHttpClient;
    @Autowired(required=false)
    ParseResponse parseResponse;
    @Autowired(required=false)
    OpenId openIdObj;

    private String openId  = "";
    private HttpClient httpClient;


    /**
     * 启动时自动初始化一个openId和httpClient
     * */
//    @PostConstruct
    public  void initOpenIdAndHttpClient(){
        openId = openIdObj.getOpenId( config.getIp(),config.getPort(),config.getUrl());
        httpClient = initHttpClient.createSSLClientDefault(config.getPort());
    }


//    @Scheduled(initialDelay = 5000,fixedDelay = 5000)
    public void publishData(){
        Map<String ,String > retMap = new HashMap<>();
        for (Map.Entry<String, String> entry : retMap.entrySet()) {
            String publishValue = "{\"id\":" + entry.getKey() + ",\"f\":0,\"p\":2,\"t\":0,\"v\":" + entry.getValue() + "}";
            redisManager.publish("chan:from_all_to_uckernal:value", publishValue);
        }

    }

    public Map<String ,String >  getPmdata(){


        return null;
    }











}
