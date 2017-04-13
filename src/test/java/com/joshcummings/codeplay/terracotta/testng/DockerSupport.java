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
}
