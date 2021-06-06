package de.kwerber.persistlib;

import de.kwerber.persistlib.executor.QueryExecutor;
import de.kwerber.persistlib.handler.ClassHandler;
import de.kwerber.persistlib.sql.SQLTranslator;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.util.Check;

public class PersistenceContext<T, Id> {

	private final Class<T> clazz;
	private final String tableName;
	private final QueryExecutor executor;
	private final ClassHandler<T, Id> handler;
	private final SQLTranslator<T, Id> translator;
	private final TypeMapper typeMapper;

	public PersistenceContext(Class<T> clazz, String tableName, QueryExecutor executor,
	                          ClassHandler<T, Id> handler, SQLTranslator<T, Id> translator,
	                          TypeMapper typeMapper) {
		Check.notNull(clazz);
		Check.notNullNotEmpty(tableName);
		Check.notNull(executor);
		Check.notNull(handler);
		Check.notNull(translator);
		Check.notNull(typeMapper);

		this.clazz = clazz;
		this.tableName = tableName;
		this.executor = executor;
		this.handler = handler;
		this.translator = translator;
		this.typeMapper = typeMapper;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public QueryExecutor getExecutor() {
		return executor;
	}

	public ClassHandler<T, Id> getHandler() {
		return handler;
	}

	public SQLTranslator<T, Id> getTranslator() {
		return translator;
	}

	public TypeMapper getTypeMapper() {
		return typeMapper;
	}

}
