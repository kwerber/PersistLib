package de.kwerber.persistlib;

import de.kwerber.persistlib.executor.ConnectionProvider;
import de.kwerber.persistlib.executor.jdbc.JdbcExecutor;
import de.kwerber.persistlib.type.CommonMapper;

import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RecordTest {

	@Table(indexes = @Index(name = "name_idx", columnList = "name"))
	record Person(@Id int id, String name, int age) {

	}

	public static void main(String[] args) throws SQLException {
		String url = "jdbc:mariadb://localhost:3306/test?user=root&password=123";
		Connection connection = DriverManager.getConnection(url);
		ConnectionProvider provider = () -> connection;

		var storage = PersistentStorage.builder(Person.class, Integer.class)
			.executor(new JdbcExecutor(provider, false, true))
			.tableName("record_table")
			.createTableIfNotExists(true)
			.typeMappers(List.of(
				new CommonMapper()
			))
			.build();

		//Person peter = new Person(10, "Peter", 30);
		//storage.insert(peter);

		Optional<Person> res = storage.findById(10);

		System.out.println(res);
	}

}
