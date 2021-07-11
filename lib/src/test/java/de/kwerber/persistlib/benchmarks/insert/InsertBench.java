package de.kwerber.persistlib.benchmarks.insert;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

public class InsertBench {

	public static void main(String[] args) throws IOException { org.openjdk.jmh.Main.main(args); }

	@Benchmark
	@BenchmarkMode(Mode.All)
	public void testPersistLib(InsertBenchState state) {
		state.storage.insert(state.personToInsert);
	}

	@Benchmark
	@BenchmarkMode(Mode.All)
	public void testJDBC(InsertBenchState state, Blackhole blackhole) {
		String sql = "INSERT INTO `" + state.tableName + "` " +
			"(`id`, `name`, `age`, `alive`) " +
			"VALUES (?, ?, ?, ?);";

		Object[] params = new Object[4];
		params[0] = state.personToInsert.getId().toString();
		params[1] = state.personToInsert.getName();
		params[2] = state.personToInsert.getAge();
		params[3] = state.personToInsert.isAlive();

		// Imagine doing the database insert here
		blackhole.consume(params);
		blackhole.consume(sql);
	}

}
