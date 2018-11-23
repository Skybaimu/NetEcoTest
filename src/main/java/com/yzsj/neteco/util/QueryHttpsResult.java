package com.yzsj.neteco.util;

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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 获取https查询结果
 * */

public class QueryHttpsResult {
//    @Autowired(required = false)
//    InitHttpClient initHttpClient;

    /**
     * @param port
     * @param url
     * @param headers
     * @param  parameters
     * @param method
     *
     * @return  String
     * */

    public String getHttpsResult(int port,String url,List<BasicNameValuePair> headers, List<BasicNameValuePair> parameters,String method){

        Map<String ,String> retMap = null;
        HttpClient httpClient = new InitHttpClient().createSSLClientDefault(port);
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
            HttpResponse response= null;
            if("get".equals(method)) {
                HttpGet httpGet = new HttpGet(url);
                if (null != headers) {
                    for (BasicNameValuePair header : headers) {
                        httpGet.setHeader(header.getName(), header.getValue());
                    }
                }
                 response = httpClient.execute(httpGet);
            }else if("put".equals(method)) {
                HttpPut httpPut = new HttpPut(url);
                if (null != parameters) {
                    httpPut.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
                }
                response = httpClient.execute(httpPut);
            }else {
                //其他方法未定
                return "";
            }

            HttpEntity entity = response.getEntity();
            String ret = EntityUtils.toString(entity);
            if (null == ret || ret.isEmpty()) {
                return "";
            }
            return ret;

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.getConnectionManager().shutdown();
        }

        return "";
    }

}
