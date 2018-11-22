package com.yzsj.neteco;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzsj.neteco.common.Config;
import com.yzsj.neteco.common.OpenId;
import com.yzsj.neteco.common.alarm.AlarmManager;
import com.yzsj.neteco.util.InitHttpClient;
import com.yzsj.neteco.util.POIUtil;
import com.yzsj.neteco.util.ParseResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonbTester;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class NetecoApplicationTests {
    @Autowired(required=false)
    Config config;

    @Autowired(required = false)
    ParseResponse parseResponse;


    String openId="";
    @Test
    public void contextLoads() {
        String url = config.getUrl();

//        String url1 = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=18314444444";
        int port = config.getPort();
        String ip =config.getIp();

        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("userid", config.getUserid()));
        parameters.add(new BasicNameValuePair("value", config.getPassword()));
        parameters.add(new BasicNameValuePair("ipaddress", config.getIpAddress()));
        Map<String, String> params = new HashMap<>();
        if(openId ==""){
//            OpenId opId = new OpenId();
//            openId = opId.getOpenId(ip,port,url);
//            openId = getOpenId(ip,port,url,parameters);
            openId = getOpenId1(ip,port,url);
            System.out.println(openId);
        }

        String result = (String) getAlarm(openId,parameters,ip,port,url);
//        String result1 = sendHttpsRequestByPost(url1,params);
//        String result = (String) AlarmManager
        System.out.println(result);

    }
    public static final  String getOpenId(String ip ,int port,String url1,List<BasicNameValuePair> parameters){
         //set the URL
        String userName = "";
        String pwd = "";
        String openidURL = "/rest/openapi/sm/session";
//set parameters
       /* List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("userid", userName));
        parameters.add(new BasicNameValuePair("value", pwd));
        parameters.add(new BasicNameValuePair("ipaddress", "10.10.10.10"));*/
//create a connection manager
        X509TrustManager tm = new X509TrustManager()
        {
            public void checkClientTrusted(X509Certificate[] xcs, String string) {    }
            public void checkServerTrusted(X509Certificate[] xcs, String string) {    }
            public X509Certificate[] getAcceptedIssuers() {     return null;    }
        };
//create a SSL connection
        Map<String, String> retMap = null;
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", port, socketFactory));
//create a HttpClient to connect to the target host
            HttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));
//set the URL, the ip is NetEco server's IP, port is 32102
            String url = "https://" + ip + ":" + port + openidURL;
//set the method
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
//send the request
            HttpResponse response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
//            System.out.println(EntityUtils.toString(entity));
            if(entity != null){
                retMap = parseResponse(EntityUtils.toString(entity));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (retMap.get("code").equals("0"))
        {
            return retMap.get("data");
        }
        return "";

    }

public String getOpenId1(String ip ,int port,String url1){
    //set the URL
    String openidURL = "/rest/openapi/sm/session";
//set parameters
    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
    parameters.add(new BasicNameValuePair("userid", config.getUserid()));
    parameters.add(new BasicNameValuePair("value", config.getPassword()));
    parameters.add(new BasicNameValuePair("ipaddress", config.getIpAddress()));
//create a connection manager
    X509TrustManager tm = new X509TrustManager()
    {
        public void checkClientTrusted(X509Certificate[] xcs, String string) {    }
        public void checkServerTrusted(X509Certificate[] xcs, String string) {    }
        public X509Certificate[] getAcceptedIssuers() {     return null;    }
    };
//create a SSL connection
    Map<String, String> retMap = null;
    try {
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[] { tm }, null);
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("https", port, socketFactory));

        HttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));

        String url = "https://" + ip + ":" + port + openidURL;

        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        httpPut.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

        HttpResponse response = httpClient.execute(httpPut);
        HttpEntity entity = response.getEntity();
//        System.out.println(EntityUtils.toString(entity));
        if(entity != null){
            retMap = parseResponse(EntityUtils.toString(entity));
        }
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (KeyManagementException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    if (retMap.get("code").equals("0"))
    {
        return retMap.get("data");
    }
    return "";

}

    public static Object getAlarm(String openid,List<BasicNameValuePair> parameters,String ip ,int port,String url1) {
        //set the URL
        String queryNeURL = "/rest/openapi/alarm";
//set headers
        List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
//openid is get from interface "/rest/openapi/sm/session"
        System.out.println(openid);
        headers.add(new BasicNameValuePair("openid", openid));
//pageSize is HTTP header parameter
        headers.add(new BasicNameValuePair("pageSize", "100"));
//create a connection manager
        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
//create a SSL connection
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
            sslcontext.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", port, socketFactory));
//create a HttpClient to connect to the target host
            HttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));
//set the URL, the ip is NetEco server's IP, port is 32102
            String url = "https://" + ip + ":" + port + queryNeURL;
//set parameters
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
//set headers
            if (null != headers) {
                for (BasicNameValuePair header : headers) {
                    httpGet.setHeader(header.getName(), header.getValue());
                }
            }
//send the request
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String ret = EntityUtils.toString(entity);
            if (null == ret || ret.isEmpty()) {
                return "";
            }
//get the result
            Map<String, String> retMap = parseResponse(ret);
            if (retMap.get("code").equals("0")) {
                return retMap.get("data");
            } else {
                System.out.println("error!!! " + retMap.toString());
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static final String sendHttpsRequestByPost(String url, Map<String, String> params) {
        String responseContent = null;
        HttpClient httpClient = new DefaultHttpClient();
        //创建TrustManager
        X509TrustManager xtm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        //这个好像是HOST验证
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

            public void verify(String arg0, SSLSocket arg1) throws IOException {}
            public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
            public void verify(String arg0, X509Certificate arg1) throws SSLException {}
        };

        try {
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
            SSLContext ctx = SSLContext.getInstance("TLS");
            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[] { xtm }, null);
            //创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier(hostnameVerifier);
            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity(); // 获取响应实体
            if (entity != null) {
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }




    public static Map<String, String> parseResponse(String input)
    {
        Map<String, String> retMap = new HashMap<String, String>();

        JSONObject jObject = JSONObject.parseObject(input);
        if (null == jObject)
        {
            return retMap;
        }
        if (null != jObject.get("code"))
        {
            String i = jObject.get("code").toString();
            retMap.put("code", i);
        }
        if (null != jObject.get("data"))
        {
            String data = jObject.get("data").toString();
            retMap.put("data", data.toString());
        }
        if (null != jObject.get("description"))
        {
            String des = jObject.get("description").toString();
            retMap.put("description", des.toString());
        }
        if (null != jObject.get("currentPage"))
        {
            String currentPage = jObject.get("currentPage").toString();
            retMap.put("currentPage", currentPage.toString());
        }
        if (null != jObject.get("totalPage"))
        {
            String totalPage = jObject.get("totalPage").toString();
            retMap.put("totalPage", totalPage.toString());
        }
        if (null != jObject.get("pageSize"))
        {
            String pageSize = jObject.get("pageSize").toString();
            retMap.put("pageSize", pageSize.toString());
        }
        return retMap;
    }


    @Test
    public void testMethod(){
        String url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=18314444444";
        HttpClient httpClient =new InitHttpClient().createSSLClientDefault(443);
        HttpGet httpGet = new HttpGet(url);
        HttpPut httpPut = new HttpPut(url);
        try {
            String retMap = "";
            System.out.println(new Date());
           for (int i = 0;i<100 ; i++){
               HttpResponse response = httpClient.execute(httpPut);

               HttpEntity entity = response.getEntity();
               retMap += EntityUtils.toString(entity);
           }
            System.out.println(new Date());
            System.out.println(retMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0 ;i < 10 ;i++){
//          String retult =   sendHttpsRequestByPost1(url1,parameters);
//          System.out.println(retult);
        }

    }
    public static final String sendHttpsRequestByPost1(String url, Map<String, String> params) {
        String responseContent = null;
        HttpClient httpClient = new DefaultHttpClient();
        //创建TrustManager
        X509TrustManager xtm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        //这个好像是HOST验证
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

            public void verify(String arg0, SSLSocket arg1) throws IOException {}
            public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
            public void verify(String arg0, X509Certificate arg1) throws SSLException {}
        };

        try {
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
            SSLContext ctx = SSLContext.getInstance("TLS");
            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[] { xtm }, null);
            //创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier(hostnameVerifier);
            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity(); // 获取响应实体
            if (entity != null) {
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }

    @Test
    public  void getJson(){
        String ret = "{\"code\": 0,\"data\": [{\"resultTime\": 1541844764,\"measObject\": null,\"period\": 0,\"neTypeID\": 41000,\"dn\": \"NE=33554442.41000-21506\",\"parentDn\": \"NE=33554439\"}, {\"resultTime\": 1541844764,\"measObject\": null,\"period\": 0,\"neTypeID\": 41000,\"dn\": \"NE=33554442.41000-21506\",\"parentDn\": \"NE=33554439\"}],\"description\": \"Operation success.\",\"pageSize\": 20,\"totalPage\": 12,\"currentPage\": 1}";
        System.out.println(ret);
        Map<String,Object> parse = (Map<String,Object>)JSON.parse(ret);
        Map<String,String> retMap = new ParseResponse().getParseResponse(ret);
        List<Map<String,Object>> data = (List<Map<String,Object>>)parse.get("data");
        List<Map<String,Object>> data1= (List<Map<String,Object>>)JSONArray.parse(retMap.get("data"));
        System.out.println(data);
        String neId = "41000";
        HashMap<String, Map<String, Object>> stringMapHashMap = new HashMap<>();
        for(Map<String,Object> d : data){
            stringMapHashMap.put(String.valueOf(d.get("neTypeID")),d);
        }
        Map<String, Object> stringObjectMap = stringMapHashMap.get(neId);
        System.out.println(stringObjectMap.get("resultTime"));
       /*Map<String,String> retMap = new ParseResponse().getParseResponse(ret);
        if (retMap.get("code").equals("0")) {
            //调用方法将结果集转化为推送数据
            String result =  retMap.get("data");
            List<Map<String,String>>  list= JSONArray.parseObject(result);
            System.out.println(object);
        }*/
    }

    @Test
    public  void fileTest() throws IOException {
        POIUtil poiUtil = new POIUtil();
        String realPath = POIUtil.class.getClassLoader().getResource("").getPath();
        String path = "F:\\baimu\\工作文件\\华为微模块测试\\TestData-lyj\\TestData-lyj\\excelTest.xlsx";
        List<String[] > list = POIUtil.readExcel(path);
        String path1 = new File("").getAbsolutePath();

        System.out.println(path1);
        Map<String ,String> map = new HashMap<>();
        for (String[] strings: list) {
            map.put(strings[0],strings[1]);
            for (String str: strings) {
                System.out.println(str);
            }
        }
        System.out.println(map.get("8941"));
//        System.out.println(list);

    }


}
