package de.kwerber.persistlib.sql;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.query.PersistenceSelect;

public interface SQLTranslator<T, Id> {

	ParameterizedQuery getInsertQuery(PersistenceContext<T, Id> ctx, T instance);

	ParameterizedQuery getUpdateQuery(PersistenceContext<T, Id> ctx, T instance);

	ParameterizedQuery getDeleteQuery(PersistenceContext<T, Id> ctx, Id id);

	ParameterizedQuery getCountQuery(PersistenceContext<T, Id> ctx, PersistenceSelect<T, Id> query);

	ParameterizedQuery getSelectQuery(PersistenceContext<T, Id> ctx, PersistenceSelect<T, Id> query);

	ParameterizedQuery getDeleteAllQuery(PersistenceContext<T, Id> ctx);

}
