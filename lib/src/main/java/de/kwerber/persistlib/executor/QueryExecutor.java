package de.kwerber.persistlib.executor;

import de.kwerber.persistlib.exception.QueryExecuteException;
import de.kwerber.persistlib.query.ParameterizedQuery;

import java.sql.ResultSet;

public interface QueryExecutor {

	void execute(ParameterizedQuery query) throws QueryExecuteException;

	Object executeAndReturnKey(ParameterizedQuery query) throws QueryExecuteException;

	ResultSet executeAndReturnResult(ParameterizedQuery query) throws QueryExecuteException;

}
