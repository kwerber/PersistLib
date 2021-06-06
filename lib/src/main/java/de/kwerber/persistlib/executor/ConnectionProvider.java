package de.kwerber.persistlib.executor;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {

	Connection getConnection() throws SQLException;

}
