package de.kwerber.persistlib.exception;

public class PersistenceQueryException extends RuntimeException {

	public PersistenceQueryException(Throwable cause) {
		super(cause);
	}

	public PersistenceQueryException(String message) {
		super(message);
	}

}
