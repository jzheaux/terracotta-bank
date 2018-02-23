package com.joshcummings.codeplay.terracotta.app.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;

import com.google.gson.Gson;
import com.joshcummings.codeplay.terracotta.app.model.Message;

import ucar.httpservices.HTTPBasicProvider;

/**
 * A service for CRUDing messages via Elasticsearch
 * 
 * @author Josh
 *
 */
public class ElasticsearchMessageService implements MessageService {
	private final RestClient client;
	
	public ElasticsearchMessageService() {
		client = RestClient.builder(
		        new HttpHost("localhost", 9200, "http"))
				.setRequestConfigCallback(new RequestConfigCallback() {

					@Override
					public Builder customizeRequestConfig(Builder arg0) {
						arg0.setAuthenticationEnabled(true);
						return arg0;
					}
					
				})
				.setHttpClientConfigCallback(new HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder arg0) {
						arg0.setDefaultCredentialsProvider(new HTTPBasicProvider("elastic", "changeme"));
						return arg0;
					}
				})
				.build();
	}


	
	@Override
	public Set<Message> findAll(String criteria) {		
		String query = "{" +
			"\"query\": {" + 
				"\"bool\" : {" + 
					"\"must\" : [" + 
						"{" +
							"\"match\" : {" +
							"\"message\" : \"%s\"" +
							"}" +
						"}" +
                	"]" +
            	"}" +
        	"}" +
    	"}";
		
		HttpEntity entity = new NStringEntity(
				String.format(query, criteria),
				ContentType.APPLICATION_JSON);
		
		try {
			Response response = client.performRequest(
					"GET",
					"/messages/_search",
			        Collections.singletonMap("pretty", "true"),
			        entity);
			Map<String, Object> details = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), Map.class);
			List list = (List)((Map)details.get("hits")).get("hits");
			Set<Message> messages = new HashSet<>();
			for ( Object e : list ) {
				Map<String, Object> properties = (Map)((Map)e).get("_source");
				Message m = new Message(String.valueOf(properties.get("id")), 
						String.valueOf(properties.get("name")),
						String.valueOf(properties.get("email")),
						String.valueOf(properties.get("subject")),
						String.valueOf(properties.get("message")));
				messages.add(m);
			}
			return messages;
		} catch ( IOException e ) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void addMessage(Message message) {
		HttpEntity entity = new NStringEntity(
		        "{\n" +
		        "    \"id\" : \"" + message.getId() + "\",\n" +
		        "    \"name\" : \"" + message.getName() + "\",\n" +
		        "    \"email\" : \"" + message.getEmail() + "\",\n" +
		        "    \"subject\" : \"" + message.getSubject() + "\",\n" +
		        "    \"message\" : \"" + message.getMessage() + "\"\n" +
		        "}", ContentType.APPLICATION_JSON);

		try { 
			client.performRequest(
			        "POST",
			        "/messages/message/" + message.getId(),
			        Collections.<String, String>emptyMap(),
			        entity);
		} catch ( IOException e ) {
			throw new IllegalArgumentException(e);
		}
	}
}
