package de.kwerber.persistlib.query;

import de.kwerber.persistlib.util.Check;

import java.util.Arrays;

public class ParameterizedQuery {

	private static final Object[] NO_PARAMS = new Object[0];

	private final String sql;
	private final Object[] parameters;

	public ParameterizedQuery(String sql) {
		this(sql, NO_PARAMS);
	}

	public ParameterizedQuery(String sql, Object[] parameters) {
		Check.notNullNotEmpty(sql);
		Check.notNull(parameters);

		this.sql = sql;
		this.parameters = parameters;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public boolean hasParameters() {
		return parameters.length > 0;
	}

	@Override
	public String toString() {
		return "ParameterizedQuery{" +
			"sql='" + sql + '\'' +
			", parameters=" + Arrays.toString(parameters) +
			'}';
	}

}
