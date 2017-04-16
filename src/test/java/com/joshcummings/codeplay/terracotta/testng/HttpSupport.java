package com.joshcummings.codeplay.terracotta.testng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.util.IOUtils;

public class HttpSupport {
	protected CloseableHttpClient httpclient = HttpClients.createDefault();
	protected HttpHost proxy = new HttpHost("localhost", 8081, "http");
	protected RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	

	public CloseableHttpResponse post(String path, BasicNameValuePair... body) throws IOException {
		try ( CloseableHttpResponse csrf = getForEntity("/csrf.jsp") ) {
			String token = new String(IOUtils.toByteArray(csrf.getEntity().getContent()));
			
			HttpPost post = new HttpPost("http://" + TestConstants.host + path);
			post.setConfig(config);
			List<NameValuePair> nvps = new ArrayList<>(Arrays.asList(body));
			nvps.add(new BasicNameValuePair("csrfToken", token));
			post.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(post);
			return response;			
		}
	}
	
	public CloseableHttpResponse getForEntity(String path) throws IOException {
		HttpGet get = new HttpGet("http://" + TestConstants.host + path);
		get.setConfig(config);
		CloseableHttpResponse response = httpclient.execute(get);
		return response;
	}
}
