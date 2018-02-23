package com.joshcummings.codeplay.terracotta.defense.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import com.joshcummings.codeplay.terracotta.app.service.MessageService;

import ucar.httpservices.HTTPBasicProvider;

public class GsonElasticsearchMessageService implements MessageService {
	private final RestClient client;
	private final Gson gson = new Gson();
	
	public GsonElasticsearchMessageService() {
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
		Map<String, Object> query = new LinkedHashMap<>();
		Map<String, Object> bool = new LinkedHashMap<>();
		Map<String, Object> must = new LinkedHashMap<>();
		Map<String, Object> match = new LinkedHashMap<>();
		Map<String, Object> message = new LinkedHashMap<>();
		
		query.put("query", bool);
		bool.put("bool", must);
		must.put("must", Arrays.asList(match));
		match.put("match", message);
		message.put("message", criteria);

		HttpEntity entity  = new NStringEntity(
				gson.toJson(query),
				ContentType.APPLICATION_JSON);
			
		try {
			Response response = client.performRequest(
					"GET",
					"/messages/_search",
			        Collections.singletonMap("pretty", "true"),
			        entity);
			
			Map<String, Object> details = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), Map.class);
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
				gson.toJson(message),
				ContentType.APPLICATION_JSON);

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
