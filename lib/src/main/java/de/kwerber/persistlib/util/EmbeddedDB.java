package de.kwerber.persistlib.util;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import de.kwerber.persistlib.executor.ConnectionProvider;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class EmbeddedDB implements ConnectionProvider {

	public static void main(String[] args) throws ManagedProcessException {
		new EmbeddedDB(3306, "testdb");
		System.out.println("db launched");
	}

	final DBConfigurationBuilder builder;
	final DB db;
	final String dbName;
	final int port;

	public EmbeddedDB(int port, String dbName) throws ManagedProcessException {
		Path root = Path.of("data/db/");

		this.port = port;
		this.dbName = dbName;

		stopRunningDB();

		this.builder = DBConfigurationBuilder.newBuilder();
		builder.setPort(port); // OR, default: setPort(0); => autom. detect free port
		builder.setBaseDir(root.resolve("base").toAbsolutePath().toString()); // just an example
		builder.setDataDir(root.resolve("data").toAbsolutePath().toString()); // just an example
		builder.setLibDir(root.resolve("lib").toAbsolutePath().toString()); // just an example
		builder.setDeletingTemporaryBaseAndDataDirsOnShutdown(true);

		this.db = DB.newEmbeddedDB(builder.build());

		this.db.start();

		this.db.createDB(dbName);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				this.db.stop();
			}
			catch (ManagedProcessException e) {
				e.printStackTrace();
			}
		}, "DBShutdownHook"));
	}

	public static void stopRunningDB() {
		ProcessHandle.allProcesses()
			.filter(p -> p.info().toString().contains("mysqld.exe"))
			//.filter(p -> p.parent().isPresent() && p.parent().get().info().toString().contains("java.exe"))
			.forEach(p -> {
				p.destroy();

				try { p.onExit().get(); }
				catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
			});
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.builder.getURL(this.dbName), "root", "");
	}

	public void shutdown() {
		try {
			this.db.stop();
		}
		catch (ManagedProcessException e) {
			throw new RuntimeException(e);
		}
	}

}
