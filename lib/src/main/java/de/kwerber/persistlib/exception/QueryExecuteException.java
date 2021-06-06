package de.kwerber.persistlib.exception;

public class QueryExecuteException extends PersistenceQueryException {

	public QueryExecuteException(Throwable cause) {
		super(cause);
	}

	public QueryExecuteException(String message) {
		super(message);
	}

}
