package com.joshcummings.codeplay.terracotta.app.service;

import org.junit.Before;
import org.junit.Test;

import com.joshcummings.codeplay.terracotta.app.model.Message;
import com.joshcummings.codeplay.terracotta.app.service.ElasticsearchMessageService;

public class ElasticsearchMessageServiceTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		ElasticsearchMessageService service = new ElasticsearchMessageService();
		Message message = new Message("1", "asdf", "asdf", "sadf", "asdf");
		service.addMessage(message);
		service.findAll("asdf");
	}

}
