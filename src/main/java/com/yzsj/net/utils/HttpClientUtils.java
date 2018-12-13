package com.yzsj.net.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("unchecked")
public class HttpClientUtils {
	public static class CloseableHttpClientHolder {
		public static CloseableHttpClient INSTANCE;
		static {
			HttpRequestRetryHandler rh = new HttpRequestRetryHandler() {
				@Override
				public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
					if (executionCount >= 3) {
						return false;
					}
					if ((exception instanceof InterruptedIOException)) {
						return false;
					}
					if ((exception instanceof UnknownHostException)) {
						return false;
					}
					if ((exception instanceof ConnectTimeoutException)) {
						return false;
					}
					if ((exception instanceof SSLException)) {
						return false;
					}

					HttpClientContext clientContext = HttpClientContext.adapt(context);
					HttpRequest request = clientContext.getRequest();
					boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
					if (idempotent) {
						return true;
					}
					return false;
				}
			};

			try {
				SSLContext sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}

					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}
				}}, null);

				Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE
						))
						.build();

				PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
				cm.setMaxTotal(200);
				cm.setDefaultMaxPerRoute(50);
				cm.setValidateAfterInactivity(1000);
				SocketConfig sc = SocketConfig.custom().setTcpNoDelay(true).setSoReuseAddress(true).setSoTimeout(50000).setSoLinger(1500).setSoKeepAlive(true).build();
				RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(50000).setConnectTimeout(50000).setSocketTimeout(50000).build();
				INSTANCE = HttpClients.custom().setRetryHandler(rh).setConnectionManager(cm).setDefaultSocketConfig(sc).setDefaultRequestConfig(rc).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36").build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final ResponseHandler<Object> RESPONSEHANDLER = new ResponseHandler<Object>() {
		public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if ((status >= 200) && (status < 300)) {
				HttpEntity entity = response.getEntity();
				entity.getContentType();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
			throw new ClientProtocolException("Unexpected HTTP Status Code: " + status);
		}
	};

	private static final <T> T ajax(String url, String type, List<NameValuePair> data, List<Header> headers) throws Exception {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		T response = null;
		if ("POST".equalsIgnoreCase(type)) {
			URIBuilder builder = new URIBuilder(url);
			HttpPost httppost = new HttpPost(builder.build());
			if (headers != null && headers.size() > 0) {
				httppost.setHeaders(headers.toArray(new Header[headers.size()]));
			}
			if (data != null) {
				httppost.setEntity(new UrlEncodedFormEntity(data));
			}
			System.out.println("Executing request " + httppost.getRequestLine());
			response = (T) CloseableHttpClientHolder.INSTANCE.execute(httppost, RESPONSEHANDLER);
		}else if("PUT".equalsIgnoreCase(type)) {
			URIBuilder builder = new URIBuilder(url);
			HttpPut httpput = new HttpPut(builder.build());
			if (headers != null && headers.size() > 0) {
				httpput.setHeaders(headers.toArray(new Header[headers.size()]));
			}
			if (data != null) {
				httpput.setEntity(new UrlEncodedFormEntity(data));
			}
			System.out.println("Executing request " + httpput.getRequestLine());
			response = (T) CloseableHttpClientHolder.INSTANCE.execute(httpput, RESPONSEHANDLER);
		}else {
			URIBuilder builder = new URIBuilder(url);
			if (data != null) {
				builder.setParameters(data);
			}
			HttpGet httpget = new HttpGet(builder.build());
			if (headers != null && headers.size() > 0) {
				httpget.setHeaders(headers.toArray(new Header[headers.size()]));
			}
			System.out.println("Executing request " + httpget.getRequestLine());
			response = (T) CloseableHttpClientHolder.INSTANCE.execute(httpget, RESPONSEHANDLER);
		}
		return response;
	}

	public static final <T> T get(String url) throws Exception {
		return ajax(url, "GET", null, null);
	}
	
	public static final <T> T get(String url, List<NameValuePair> data) throws Exception {
		return ajax(url, "GET", data, null);
	}
	public static final <T> T put(String url, List<NameValuePair> data) throws Exception {
		return ajax(url, "PUT", data, null);
	}


	public static final <T> T get(String url, List<NameValuePair> data, List<Header> headers) throws Exception {
		return ajax(url, "GET", data, headers);
	}
	
	public static final <T> T post(String url, List<NameValuePair> data) throws Exception {
		return ajax(url, "POST", data, null);
	}

	public static void main(String[] args) throws Exception {
		String data = get("https://www.baidu.com");
		System.out.println(data);
	}
}