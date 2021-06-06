package de.kwerber.persistlib.creator;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.exception.QueryExecuteException;

public interface TableCreator {

	<T, Id> void createTable(PersistenceContext<T, Id> context) throws QueryExecuteException;

}
