package de.kwerber.persistlib.benchmarks.insert;

import de.kwerber.persistlib.PersistentStorage;
import de.kwerber.persistlib.Person;
import de.kwerber.persistlib.PersonId;
import de.kwerber.persistlib.executor.jdbc.JdbcExecutor;
import de.kwerber.persistlib.type.CommonMapper;
import de.kwerber.persistlib.type.TypeMappers;
import de.kwerber.persistlib.util.EmbeddedDB;
import org.openjdk.jmh.annotations.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

@State(Scope.Benchmark)
public class InsertBenchState {

	public static final int DB_PORT = 3306;
	public static final String DB_NAME = "testdb";
	public static final String TABLE_NAME = "bench_person";

	public Person personToInsert;
	public EmbeddedDB db;
	public Connection connection;
	public PersistentStorage<Person, PersonId> storage;

	@Setup(Level.Invocation)
	public void setUpInvocation() {
		Random random = new Random();

		this.personToInsert = new Person(
			"Hans".repeat(random.nextInt(10) + 1),
			random.nextInt(100),
			random.nextBoolean()
		);
	}

	@Setup(Level.Trial)
	public void setUpTrial() throws Exception {
		this.db = new EmbeddedDB(DB_PORT, DB_NAME);

		this.connection = this.db.getConnection();

		this.storage = PersistentStorage.builder(Person.class, PersonId.class)
			.tableName(TABLE_NAME)
			.executor(new JdbcExecutor(db, false))
			.createTableIfNotExists(true)
			.typeMappers(List.of(
				TypeMappers.toAndFromString(PersonId.class, 36, PersonId::new),
				new CommonMapper()
			))
			.build();

		try (Statement statement = this.connection.createStatement()){
			statement.executeUpdate("DELETE FROM " + TABLE_NAME);
		}
	}

	@TearDown(Level.Trial)
	public void tearDownTrial() {
		try { this.connection.close(); } catch (SQLException ignore) { }
		try { this.db.shutdown(); } catch (Exception ignore) { }
	}

}
