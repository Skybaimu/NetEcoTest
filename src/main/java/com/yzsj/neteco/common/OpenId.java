package com.yzsj.neteco.common;

import com.yzsj.neteco.util.InitHttpClient;
import com.yzsj.neteco.util.ParseResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenId {
    @Autowired
    Config config;

    @Autowired
    InitHttpClient initHttpClient;

    @Autowired
    ParseResponse parseResponse;

    public  String getOpenId( String ip , int port, String openidURL){

        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("userid", config.getUserid()));
        parameters.add(new BasicNameValuePair("value", config.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", config.getIpAddress()));

        Map<String ,String> retMap = null;
        HttpClient httpClient = new DefaultHttpClient();
        try {
           httpClient = initHttpClient.createSSLClientDefault(port);

            String url = "https://" + ip + ":" + port + openidURL;
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
            HttpResponse response = httpClient.execute(httpPut);

            HttpEntity entity = response.getEntity();
            if(entity != null){
                retMap = parseResponse.getParseResponse(EntityUtils.toString(entity));
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
}
