package de.kwerber.persistlib;

import de.kwerber.persistlib.creator.MariadbCreator;
import de.kwerber.persistlib.creator.TableCreator;
import de.kwerber.persistlib.exception.PersistenceQueryException;
import de.kwerber.persistlib.executor.ConnectionProvider;
import de.kwerber.persistlib.executor.QueryExecutor;
import de.kwerber.persistlib.executor.jdbc.JdbcExecutor;
import de.kwerber.persistlib.handler.ClassHandler;
import de.kwerber.persistlib.handler.reflective.ReflectiveClassHandler;
import de.kwerber.persistlib.sql.MariadbTranslator;
import de.kwerber.persistlib.sql.SQLTranslator;
import de.kwerber.persistlib.type.CommonMapper;
import de.kwerber.persistlib.type.CompositeTypeMapper;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.util.Check;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

public class PersistentStorageBuilder<T, Id> {

	private final Class<T> clazz;
	private String tableName;
	private QueryExecutor executor;
	private ClassHandler<T, Id> handler;
	private TableCreator creator;
	private boolean createTableIfNotExists = true;
	private TypeMapper typeMapper;
	private SQLTranslator<T, Id> translator;

	PersistentStorageBuilder(Class<T> clazz) {
		Check.notNull(clazz);
		this.clazz = clazz;
	}

	public PersistentStorageBuilder<T, Id> tableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public PersistentStorageBuilder<T, Id> connection(ConnectionProvider connectionProvider) {
		this.executor = new JdbcExecutor(connectionProvider);
		return this;
	}

	public PersistentStorageBuilder<T, Id> connection(Connection connection) {
		this.executor = new JdbcExecutor(() -> connection);
		return this;
	}

	public PersistentStorageBuilder<T, Id> executor(QueryExecutor executor) {
		this.executor = executor;
		return this;
	}

	public PersistentStorageBuilder<T, Id> handler(ClassHandler<T, Id> handler) {
		this.handler = handler;
		return this;
	}

	public PersistentStorageBuilder<T, Id> createTableIfNotExists(boolean createTableIfNotExists) {
		this.createTableIfNotExists = createTableIfNotExists;
		return this;
	}

	public PersistentStorageBuilder<T, Id> tableCreator(TableCreator creator) {
		this.creator = creator;
		return this;
	}

	public PersistentStorageBuilder<T, Id> translator(SQLTranslator<T, Id> translator) {
		this.translator = translator;
		return this;
	}

	public PersistentStorageBuilder<T, Id> typeMapper(TypeMapper typeMapper) {
		this.typeMapper = typeMapper;
		return this;
	}

	public PersistentStorageBuilder<T, Id> typeMappers(List<TypeMapper> mappers) {
		this.typeMapper = new CompositeTypeMapper(mappers);
		return this;
	}

	public PersistentStorage<T, Id> build() throws PersistenceQueryException {
		if (this.tableName == null) {
			this.tableName = clazz.getSimpleName().toLowerCase(Locale.ROOT);
		}

		if (this.handler == null) {
			this.handler = new ReflectiveClassHandler<>(this.clazz);
		}

		if (this.executor == null) {
			throw new IllegalStateException("no executor provided");
		}

		if (this.typeMapper == null) {
			this.typeMapper = new CommonMapper();
		}

		if (this.translator == null) {
			this.translator = new MariadbTranslator<>();
		}

		PersistenceContext<T, Id> context = new PersistenceContext<>(
			this.clazz,
			this.tableName,
			this.executor,
			this.handler,
			this.translator,
			this.typeMapper
		);

		if (this.createTableIfNotExists) {
			if (this.creator == null) {
				this.creator = new MariadbCreator();
			}


			this.creator.createTable(context);
		}

		return new PersistentStorage<>(context);
	}

}
