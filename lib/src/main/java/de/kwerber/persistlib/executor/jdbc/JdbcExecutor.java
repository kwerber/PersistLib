package de.kwerber.persistlib.executor.jdbc;

import de.kwerber.persistlib.exception.QueryExecuteException;
import de.kwerber.persistlib.executor.ConnectionProvider;
import de.kwerber.persistlib.executor.QueryExecutor;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.util.Check;

import java.sql.*;
import java.util.Arrays;

public class JdbcExecutor implements QueryExecutor {

	private final ConnectionProvider provider;
	private final boolean closeConnectionAfterUse;
	private final boolean debug;

	public JdbcExecutor(ConnectionProvider provider, boolean closeConnectionAfterUse) {
		this(provider, closeConnectionAfterUse, true);
	}

	public JdbcExecutor(ConnectionProvider provider, boolean closeConnectionAfterUse, boolean debug) {
		Check.notNull(provider);
		this.provider = provider;
		this.closeConnectionAfterUse = closeConnectionAfterUse;
		this.debug = debug;
	}

	@Override
	public void execute(ParameterizedQuery query) throws QueryExecuteException {
		Check.notNull(query);

		try {
			String sql = query.getSql();
			Object[] params = query.getParameters();

			if (debug) {
				System.out.println("EXECUTE: " + sql + " " + Arrays.toString(params));
			}

			Connection connection = provider.getConnection();

			try {
				if (query.hasParameters()) {
					try (PreparedStatement statement = connection.prepareStatement(sql)) {
						applyParams(statement, params);
						statement.executeUpdate();
					}
				}
				else {
					try (Statement statement = connection.createStatement()) {
						statement.executeUpdate(sql);
					}
				}
			}
			finally {
				if (closeConnectionAfterUse) { connection.close(); }
			}
		}
		catch (SQLException e) {
			throw new QueryExecuteException(e);
		}
	}

	@Override
	public Object executeAndReturnKey(ParameterizedQuery query) throws QueryExecuteException {
		Check.notNull(query);

		try {
			String sql = query.getSql();
			Object[] params = query.getParameters();

			if (debug) {  System.out.println("EXECUTE: " + sql + " " + Arrays.toString(params)); };

			Connection connection = provider.getConnection();

			try {
				try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
					applyParams(statement, params);

					statement.executeUpdate();

					ResultSet rs = statement.getGeneratedKeys();

					if (rs.next()) {
						return rs.getObject(1);
					}
					else {
						throw new QueryExecuteException("no primary key for: " + sql);
					}
				}
			}
			finally {
				if (closeConnectionAfterUse) { connection.close(); }
			}
		}
		catch (SQLException e) {
			throw new QueryExecuteException(e);
		}
	}

	@Override
	public ResultSet executeAndReturnResult(ParameterizedQuery query) throws QueryExecuteException {
		Check.notNull(query);

		try {
			String sql = query.getSql();
			Object[] params = query.getParameters();

			if (debug) {  System.out.println("QUERY: " + sql + " " + Arrays.toString(params)); };

			Connection connection = provider.getConnection();

			try {
				if (query.hasParameters()) {
					try (PreparedStatement statement = connection.prepareStatement(sql)) {
						applyParams(statement, params);

						return statement.executeQuery();
					}
				}
				else {
					try (Statement statement = connection.createStatement()) {
						return statement.executeQuery(sql);
					}
				}
			}
			finally {
				if (closeConnectionAfterUse) { connection.close(); }
			}
		}
		catch (SQLException e) {
			throw new QueryExecuteException(e);
		}
	}

	private void applyParams(PreparedStatement statement, Object... args) throws SQLException {
		for (int i = 0; i < args.length; i++) {
			statement.setObject(i + 1, args[i]);
		}
	}

}
