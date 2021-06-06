package de.kwerber.persistlib.query;

import de.kwerber.persistlib.util.Check;

public class WhereClause {

	private final String clause;
	private final Object[] args;

	public WhereClause(String clause, Object[] args) {
		Check.notNullNotEmpty(clause);
		Check.notNull(args);
		this.clause = clause;
		this.args = args;
	}

	public String getClause() {
		return clause;
	}

	public Object[] getArgs() {
		return args;
	}

}
