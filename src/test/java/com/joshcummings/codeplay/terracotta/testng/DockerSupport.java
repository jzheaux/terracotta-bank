package com.joshcummings.codeplay.terracotta.testng;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class DockerSupport {
	private static final String CONTAINER_NAME = "test-terracotta-bank";
	
	private DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
		    .withDockerHost(System.getenv("DOCKER_HOST"))
		    .withDockerTlsVerify(true)
		    .withDockerCertPath(System.getenv("DOCKER_CERT_PATH"))
		    .withApiVersion("1.23")
		    .withRegistryUrl("https://index.docker.io/v1/")
		    .build();
	
	private DockerClient docker = DockerClientBuilder.getInstance(config).build();

	public void startContainer() throws Exception {
		CreateContainerCmd cmd = docker.createContainerCmd("terracotta-bank");
		cmd.withPortBindings(PortBinding.parse("8080:8080"))
			.withNetworkMode("host")
			.withName(CONTAINER_NAME);
		cmd.exec();
		docker.startContainerCmd(CONTAINER_NAME).exec();
	}

	public void stopContainer() throws Exception {
		docker.stopContainerCmd(CONTAINER_NAME).exec();
		docker.removeContainerCmd(CONTAINER_NAME).exec();
	}
	
	public void startClamav() throws Exception {
		CreateContainerCmd cmd = docker.createContainerCmd("mkodockx/docker-clamav");
		cmd.withPortBindings(PortBinding.parse("3310:3310"))
			.withNetworkMode("host")
			.withName("test-clamav");
		cmd.exec();
		docker.startContainerCmd("test-clamav").exec();
	}
	
	public void stopClamav() throws Exception {
		docker.stopContainerCmd("test-clamav").exec();
		docker.removeContainerCmd("test-clamav").exec();
	}
	
	public void startElasticsearch() throws Exception {
		CreateContainerCmd cmd = docker.createContainerCmd("docker.elastic.co/elasticsearch/elasticsearch:5.3.1");
		cmd.withPortBindings(PortBinding.parse("9200:9200"), PortBinding.parse("9300:9300"))
			.withNetworkMode("host")
			.withEnv("http.host=0.0.0.0", "script.engine.groovy.inline=true", "cluster.name=terracotta-search")
			.withName("test-elasticsearch");
		cmd.exec();
		docker.startContainerCmd("test-elasticsearch").exec();
	}
	
	public void stopElasticsearch() throws Exception {
		docker.stopContainerCmd("test-elasticsearch").exec();
		docker.removeContainerCmd("test-elasticsearch").exec();
	}
}
