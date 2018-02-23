package com.joshcummings.codeplay.terracotta.app;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.elasticsearch.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.Plugin;
import org.springframework.util.SocketUtils;

@WebListener
public class ElasticsearchConfigurer implements ServletContextListener {
	private ElasticsearchNode node;
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Settings.Builder settingsBuilder = Settings.settingsBuilder().
				put("http.enabled", String.valueOf(true)).
				put("http.port", 9200).
				put("transport.tcp.port", 9300).
				put("path.home", "target");

		String clusterName = "terracotta-search";

		Settings settings = new NodeBuilder().settings(settingsBuilder).clusterName(clusterName).local(true).getSettings().build();

        Version version = Version.CURRENT;

        node = new ElasticsearchNode(settings, version, Collections.emptyList());
        node.start();
        try {
        	node.client().admin().indices().prepareCreate("messages").get();
        } catch ( Exception e ) {
        	// okay
        }
    }
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			node.client().admin().indices().prepareDelete("messages").get();
		} catch ( Exception e ) {
			// okay 
		}
    	node.close();
    }
    
	/**
	 * Specific InternalNode class used to specify plugins and version.
	 */
	public static class ElasticsearchNode extends Node {
		private final Version version;
		private final int port;
		
		ElasticsearchNode(Settings settings, Version version, Collection<Class<? extends Plugin>> classpathPlugins) {
			super(InternalSettingsPreparer.prepareEnvironment(settings, null), version, classpathPlugins);
			this.port = Integer.parseInt(settings.get("http.port"));
			this.version = version;
		}

		public Version getVersion() {
			return this.version;
		}
		
		public Integer getPort() {
			return this.port;
		}
	}
}
