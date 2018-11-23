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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 获取openId
 * @author baimu
 * @date 2018-11-21
 *
 * */
@Component
public class OpenId {
    @Autowired(required = false)
    private ConfigUtil configUtil;

    /**
     * @param ip port openidURL
     * */
    public   String getOpenId( String ip , int port, String openidURL){
//        ConfigUtil configUtil = new ConfigUtil();
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("userid", configUtil.getUserid()));
        parameters.add(new BasicNameValuePair("value", configUtil.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", configUtil.getIpAddress()));

        Map<String ,String> retMap = null;
        HttpClient httpClient = new DefaultHttpClient();
        try {
           httpClient =new InitHttpClient().createSSLClientDefault(port);

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
}
