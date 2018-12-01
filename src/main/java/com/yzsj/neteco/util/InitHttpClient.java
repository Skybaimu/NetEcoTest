package com.yzsj.neteco.util;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 *
 * 获取httpClient
 * @author baimu
 * @data 2018-11-15
 *
 */
public class InitHttpClient {



    public  HttpClient createSSLClientDefault(int port) {
        //创建truestManager
        X509TrustManager trustManager = new X509TrustManager()
        {
            public void checkClientTrusted(X509Certificate[] xcs, String string) {    }
            public void checkServerTrusted(X509Certificate[] xcs, String string) {    }
            public X509Certificate[] getAcceptedIssuers() {     return null;    }
        };
        try {
            //获取一个安全套接字实例实现
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            //初始化该上下文
            sslcontext.init(null, new TrustManager[] { trustManager }, null);
            //忽略证书验证
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //注册协议并设置默认端口号
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", port, socketFactory));

            HttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));
            //设置数据连接超时时间
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
            //设置数据传输超时时间
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);
            return httpClient;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return new DefaultHttpClient();
    }
}