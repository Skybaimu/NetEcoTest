package com.yzsj.neteco.common.alarm;

import com.yzsj.neteco.common.Config;
import com.yzsj.neteco.util.InitHttpClient;
import com.yzsj.neteco.util.ParseResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmManager {
    @Autowired
    Config config;

    @Autowired
    InitHttpClient initHttpClient;

    @Autowired
    ParseResponse parseResponse;

    public String getAlarm(String openid, List<BasicNameValuePair> parameters,String ip,int port, String alarmURL){
        List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
        headers.add(new BasicNameValuePair("openid", openid));
        headers.add(new BasicNameValuePair("pageSize", "10"));

        Map<String ,String> retMap = null;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient = initHttpClient.createSSLClientDefault(port);

            String url = "https://" + ip + ":" + port + alarmURL;

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
            HttpGet httpGet = new HttpGet(url);
            if (null != headers) {
                for (BasicNameValuePair header : headers) {
                    httpGet.setHeader(header.getName(), header.getValue());
                }
            }
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String ret = EntityUtils.toString(entity);
            if (null == ret || ret.isEmpty()) {
                return "";
            }
           retMap = parseResponse.getParseResponse(ret);
            if (retMap.get("code").equals("0")) {
                return retMap.get("data");
            } else {
                System.out.println("报警数据获取失败 " + retMap.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.getConnectionManager().shutdown();
        }

        return "";
    }


}
