package de.kwerber.persistlib.benchmarks.insert;

import de.kwerber.persistlib.Person;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.io.IOException;
import java.sql.PreparedStatement;

public class InsertBench {

	public static void main(String[] args) throws IOException { org.openjdk.jmh.Main.main(args); }

	@Benchmark
	@BenchmarkMode(Mode.All)
	public void testPersistence(InsertBenchState state) {
		state.storage.insert(state.personToInsert);
	}

	@Benchmark
	@BenchmarkMode(Mode.All)
	public void testJDBC(InsertBenchState state) throws Exception {
		String sql = "INSERT INTO `" + InsertBenchState.TABLE_NAME + "` " +
			"(`id`, `name`, `age`, `alive`) " +
			"VALUES (?, ?, ?, ?);";

		Person person = state.personToInsert;

		try (PreparedStatement statement = state.connection.prepareStatement(sql)) {
			statement.setString(1, person.getId().toString());
			statement.setString(2, person.getName());
			statement.setInt(3, person.getAge());
			statement.setBoolean(4, person.isAlive());

			statement.executeUpdate();
		}
	}

}
