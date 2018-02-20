package com.joshcummings.codeplay.terracotta;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by joshcummings on 2/17/18.
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Measurement(iterations = 30)
public class CheckPasswordBenchmark {
	@Param({
				"asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf",
				"asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdg",
				"asdfasdfasdfasdfasdfasdfgggggggggggggggggggggggg",
				//"asdfasdfasdfgggg", "asdfasdfasdggggg", "asdfasdfasgggggg", "asdfasdfaggggggg",
				//"asdfasdfgggggggg", "asdfasdggggggggg", "asdfasgggggggggg", "asdfaggggggggggg",
				//"asdfgggggggggggg", "asdggggggggggggg", "asgggggggggggggg", "aggggggggggggggg",
				"gggggggggggggggggggggggggggggggggggggggggggggggg"
	}) String guess;

	String password = "asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf";

	@Benchmark
	public boolean checkPassword() {
		return guess.equals(password);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(CheckPasswordBenchmark.class.getCanonicalName())
			.forks(1)
			.build();

		new Runner(opt).run();
	}
}
