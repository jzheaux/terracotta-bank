package com.joshcummings.codeplay.terracotta;

import com.joshcummings.codeplay.terracotta.testng.HttpSupport;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by joshcummings on 2/17/18.
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 20)
@Warmup(iterations = 25)
public class RegistrationBenchmark {
	static HttpSupport http = new HttpSupport();

	@Param({ "admin", "admidpifns" }) String username;

	@Benchmark
	public HttpEntity attemptLogin() throws IOException {
		try ( CloseableHttpResponse response =
			http.post("/login", new BasicNameValuePair("username", username), new BasicNameValuePair("password", "password")); ) {
			return response.getEntity();
		}
	}

	@Benchmark
	public HttpEntity baseline() throws IOException {
		try ( CloseableHttpResponse response =
				  http.getForEntity("/index.jsp"); ) {
			return response.getEntity();
		}
	}

	@Test
	public void runBenchmark() throws RunnerException {
		List<Double> differences = new ArrayList<>();

			Options opt = new OptionsBuilder()
				.include(RegistrationBenchmark.class.getCanonicalName())
				.jvmArgs("-Xmx512M")
				.forks(1)
				.build();

			Collection<RunResult> results = new Runner(opt).run();

			Map<String, Double> scoresByBenchmarkParam = new HashMap<>();

			for ( RunResult result : results ) {
				scoresByBenchmarkParam.put(result.getParams().getBenchmark() + ":" + result.getParams().getParam("username"), result.getPrimaryResult().getScore());
			}

			double scoreForExistingUsername =
				scoresByBenchmarkParam
					.get(RegistrationBenchmark.class.getCanonicalName() + ".attemptLogin:admin") -
				scoresByBenchmarkParam
					.get(RegistrationBenchmark.class.getCanonicalName() + ".baseline:admin");

			double scoreForFakeUsername =
				scoresByBenchmarkParam
					.get(RegistrationBenchmark.class.getCanonicalName() + ".attemptLogin:admidpifns") -
					scoresByBenchmarkParam
						.get(RegistrationBenchmark.class.getCanonicalName() + ".baseline:admin");

			System.out.println("normalized score for fake: " + scoreForFakeUsername);
			System.out.println("normalized score for real: " + scoreForExistingUsername);
			differences.add(Math.abs(1 - scoreForFakeUsername / scoreForExistingUsername) * 100);


		System.out.println(differences);
		//Assert.assertTrue(Math.abs(1 - scoreForFakeUsername / scoreForExistingUsername) < .2);
	}
}
