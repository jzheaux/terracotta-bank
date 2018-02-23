package com.joshcummings.codeplay.terracotta.defense.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.joshcummings.codeplay.terracotta.app.model.Message;
import com.joshcummings.codeplay.terracotta.app.service.MessageService;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

public class JestClientElasticsearchMessageService implements MessageService {
	private JestClient client;
		
	public JestClientElasticsearchMessageService() {
		HttpClientConfig.Builder builder = new HttpClientConfig.Builder(Arrays.asList("http://localhost:9200"));
		builder.defaultCredentials("elastic", "changeme");
		JestClientFactory f = new JestClientFactory();
		f.setHttpClientConfig(builder.build());
		client = f.getObject();
	}
	
	@Override
	public Set<Message> findAll(String criteria) {
		SearchSourceBuilder query = 
				new SearchSourceBuilder().query(
						QueryBuilders.boolQuery()
							.must(QueryBuilders.termQuery("message", criteria)));
		
		try {
			JestResult result = client.execute(new Search.Builder(query.toString())
							.addIndex("messages")
							.build());
				
			List<Message> m = result.getSourceAsObjectList(Message.class);
			
			return new HashSet<>(m);
		} catch ( IOException e ) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void addMessage(Message message) {
		try {
			client.execute( 
				new Index.Builder(message)
					.index("messages")
					.type("message")
					.id(message.getId())
					.build());
		} catch ( IOException e ) {
			throw new IllegalArgumentException(e);
		}
	}
}
