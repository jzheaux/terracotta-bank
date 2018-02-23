package com.joshcummings.codeplay.terracotta.testng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

public class HttpSupport {
	protected CloseableHttpClient httpclient = HttpClients.createDefault();
	protected HttpHost proxy = new HttpHost("localhost", 8081, "http");
	protected RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	protected Pattern COOKIE = Pattern.compile("JSESSIONID=([A-Za-z09]+)");
	
	protected ThreadLocal<String> sessionId = new ThreadLocal<String>();
	
	public void login(String username, String password) throws IOException {
		try ( CloseableHttpResponse response = post("/login", new BasicNameValuePair("username", username),
							new BasicNameValuePair("password", password)); ) {
			Header[] headers = response.getHeaders("Set-Cookie");
			for ( Header h : headers ) {
				Matcher m = COOKIE.matcher(h.getValue());
				if ( m.find() ) {
					sessionId.set(m.group(1));
					return;
				}
			}
		}
	}
	
	public void logout() {
		sessionId.remove();
	}
	
	public CloseableHttpResponse post(String path, BasicNameValuePair... body) throws IOException {
		try ( CloseableHttpResponse csrf = getForEntity("/csrf.jsp") ) {
			String token = new String(IOUtils.toByteArray(csrf.getEntity().getContent()));
			
			HttpPost post = new HttpPost("http://" + TestConstants.host + path);
			addSessionIdIfPresent(post);
			post.setConfig(config);
			List<NameValuePair> nvps = new ArrayList<>(Arrays.asList(body));
			nvps.add(new BasicNameValuePair("csrfToken", token));
			post.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(post);
			return response;			
		}
	}
	
	public CloseableHttpResponse getForEntity(String path, BasicNameValuePair... params) throws IOException {
		HttpGet get = new HttpGet("http://" + TestConstants.host + path);
		addSessionIdIfPresent(get);
		get.setConfig(config);
		CloseableHttpResponse response = httpclient.execute(get);
		return response;
	}
	
	public String getFully(String path) throws IOException {
		try ( CloseableHttpResponse response = getForEntity(path); ) {
			Integer contentLength = Integer.parseInt(response.getFirstHeader("Content-Length").getValue());
			return new String(IOUtils.readFully(response.getEntity().getContent(), contentLength));
		}
	}
	
	public String postFully(String path, BasicNameValuePair... body) throws IOException {
		try ( CloseableHttpResponse response = post(path, body); ) {
			InputStream is = response.getEntity().getContent();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int read = -1;
			while ( ( read = is.read(b) ) != -1 ) {
				baos.write(b, 0, read);
			}
			return new String(baos.toByteArray());
		}
	}
	
	/*public String doThenReadResponse(Supplier<CloseableHttpResponse> s) {
		
	}*/
	protected void addSessionIdIfPresent(HttpRequestBase base) {
		if ( sessionId.get() != null ) {
			base.addHeader("Cookie", "JSESSIONID=" + sessionId.get());
		}
	}
}
