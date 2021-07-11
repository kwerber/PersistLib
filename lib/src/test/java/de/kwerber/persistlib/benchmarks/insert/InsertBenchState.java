package de.kwerber.persistlib.benchmarks.insert;

import de.kwerber.persistlib.PersistentStorage;
import de.kwerber.persistlib.Person;
import de.kwerber.persistlib.PersonId;
import de.kwerber.persistlib.exception.QueryExecuteException;
import de.kwerber.persistlib.executor.QueryExecutor;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.type.CommonMapper;
import de.kwerber.persistlib.type.TypeMappers;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.ResultSet;
import java.util.List;

@State(Scope.Benchmark)
public class InsertBenchState {

	// 1 Benchmark contains?: Forks
	// 1 Fork = Trial contains: Warmups & Iterations
	// 1 Warmup Phase contains: Warmup Iterations
	// 1 Invocation contains: Operations

	// Number of Forks configured by: @Fork
	// Number of warmup iterations configured by: @Warmup
	// Number of iterations configured by: @Measurement
	// Number of operations per iteration configured by: @OperationsPerInvocation

	record NoOpExecutor(Blackhole blackhole) implements QueryExecutor {
		@Override
		public void execute(ParameterizedQuery query) throws QueryExecuteException {
			this.blackhole.consume(query);
		}

		@Override
		public Object executeAndReturnKey(ParameterizedQuery query) throws QueryExecuteException {
			this.blackhole.consume(query);
			return null;
		}

		@Override
		public ResultSet executeAndReturnResult(ParameterizedQuery query) throws QueryExecuteException {
			this.blackhole.consume(query);
			return null;
		}
	}

	public final String tableName = "person_bench_table";
	public Person personToInsert;
	public PersistentStorage<Person, PersonId> storage;

	@Setup(Level.Trial)
	public void setUp(Blackhole blackhole) {
		NoOpExecutor executor = new NoOpExecutor(blackhole);

		this.storage = PersistentStorage.builder(Person.class, PersonId.class)
			.tableName(tableName)
			.executor(executor)
			.typeMappers(List.of(
				TypeMappers.toAndFromString(PersonId.class, 36, PersonId::new),
				new CommonMapper()
			))
			.build();

		this.personToInsert = new Person(
			"Hans",
			10,
			true
		);
	}

}
