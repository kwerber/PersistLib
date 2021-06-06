package de.kwerber.persistlib;

import ch.vorburger.exec.ManagedProcessException;
import de.kwerber.persistlib.executor.ConnectionProvider;
import de.kwerber.persistlib.executor.jdbc.JdbcExecutor;
import de.kwerber.persistlib.type.CommonMapper;
import de.kwerber.persistlib.type.TypeMappers;
import de.kwerber.persistlib.util.EmbeddedDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Test {

	public static void main(String[] args) throws Exception {
		//EmbeddedDB db = new EmbeddedDB(3306);
		String url = "jdbc:mariadb://localhost:3306/testdb?user=root&password=123";
		Connection connection = DriverManager.getConnection(url);
		ConnectionProvider provider = () -> connection;

		Random rand = new Random();

		PersistentStorage<Person, PersonId> storage = PersistentStorage.builder(Person.class, PersonId.class)
			.tableName("new_person_vtwo")
			.executor(new JdbcExecutor(provider, true))
			.createTableIfNotExists(true)
			.typeMappers(List.of(
				TypeMappers.toAndFromString(PersonId.class, 36, PersonId::new),
				new CommonMapper()
			))
			.build();


		// --- insert() ------------------------------------------------------------------------------------------------
		newLine();

		Person person = new Person("Ralf " + rand.nextInt(10), rand.nextInt(100), rand.nextBoolean());

		long start = System.currentTimeMillis();

		storage.insert(person);

		System.out.println((System.currentTimeMillis() - start) + "ms");
		System.out.println("Inserted: " + person);

		// --- find() --------------------------------------------------------------------------------------------------
		newLine();

		for (Person p : storage.find("age > ?", 3)) {
			System.out.println(" -> " + p);
		}

		// --- Update and findById() -----------------------------------------------------------------------------------
		newLine();

		person = storage.findById(person.getId()).orElseThrow();
		person.setAge(rand.nextInt(100));
		person.setName("Ralf" + rand.nextInt(10));
		storage.update(person);

		// --- count() -------------------------------------------------------------------------------------------------
		newLine();

		int count = storage.count("age > ?", 30);

		System.out.println("Count: " + count);

		// --- exists() ------------------------------------------------------------------------------------------------
		newLine();

		storage.exists(person);

		// --- delete() ------------------------------------------------------------------------------------------------
		newLine();

		storage.delete(person);

		//db.shutdown();
	}

	static void newLine() { System.out.println(); }

}
