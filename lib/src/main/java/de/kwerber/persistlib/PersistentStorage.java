package de.kwerber.persistlib;

import de.kwerber.persistlib.exception.PersistenceQueryException;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.query.PersistenceSelect;
import de.kwerber.persistlib.util.Check;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class PersistentStorage<T, Id> {

	public static <T, Id> PersistentStorageBuilder<T, Id> builder(Class<T> entityClass, Class<Id> idType) {
		Check.notNull(entityClass);
		Check.notNull(idType);
		return new PersistentStorageBuilder<>(entityClass);
	}

	private final PersistenceContext<T, Id> ctx;

	PersistentStorage(PersistenceContext<T, Id> ctx) {
		Check.notNull(ctx);
		this.ctx = ctx;
	}

	public void update(T object) throws PersistenceQueryException {
		Check.notNull(object);

		ParameterizedQuery query = this.ctx.getTranslator().getUpdateQuery(this.ctx, object);

		this.ctx.getExecutor().execute(query);
	}

	public void insert(T object) throws PersistenceQueryException {
		Check.notNull(object);

		PersistentAttribute primaryAttribute = ctx.getHandler().getPrimaryAttribute();

		ParameterizedQuery query = this.ctx.getTranslator().getInsertQuery(this.ctx, object);

		if (primaryAttribute.isAutoIncremented()) {
			// need to grab auto generated key
			Object serializedKey = this.ctx.getExecutor().executeAndReturnKey(query);
			Object key = this.ctx.getTypeMapper().deserialize(this.ctx.getHandler().getPrimaryAttribute(), serializedKey);

			this.ctx.getHandler().setPrimaryValue(object, (Id) key);
		}
		else {
			// key has to be set already since it is not auto generated
			this.ctx.getExecutor().execute(query);
		}
	}

	public void delete(T object) {
		Check.notNull(object);
		this.deleteById(this.ctx.getHandler().getPrimaryValue(object));
	}

	public void deleteById(Id id) {
		Check.notNull(id);
		ParameterizedQuery query = this.ctx.getTranslator().getDeleteQuery(this.ctx, id);
		this.ctx.getExecutor().execute(query);
	}

	public void deleteAll() {
		ParameterizedQuery query = this.ctx.getTranslator().getDeleteAllQuery(this.ctx);
		this.ctx.getExecutor().execute(query);
	}

	public boolean exists(T object) {
		Check.notNull(object);
		return this.existsWithId(this.ctx.getHandler().getPrimaryValue(object));
	}

	public boolean existsWithId(Id id) {
		Check.notNull(id);
		return findById(id).isPresent();
	}

	public int count() throws PersistenceQueryException {
		return count("2 > 1");
	}

	public int count(String where, Object... args) throws PersistenceQueryException {
		PersistenceSelect<T, Id> select = new PersistenceSelect<>(this.ctx).where(where, args);
		ParameterizedQuery query = this.ctx.getTranslator().getCountQuery(this.ctx, select);

		ResultSet result = this.ctx.getExecutor().executeAndReturnResult(query);

		try {
			result.next();
			return result.getInt("COUNT(*)");
		}
		catch (SQLException e) {
			throw new PersistenceQueryException(e);
		}
	}

	public PersistenceSelect<T, Id> find() {
		return new PersistenceSelect<T, Id>(this.ctx);
	}

	public PersistenceSelect<T, Id> find(String where, Object... args) {
		return find().where(where, args);
	}

	public Collection<T> findAll() throws PersistenceQueryException {
		return find().toList();
	}

	public Optional<T> findById(Id id) throws PersistenceQueryException {
		Check.notNull(id);

		String primaryAttrName = this.ctx.getHandler().getPrimaryAttribute().getId();

		return find("`" + primaryAttrName + "` = ?", id).firstMaybe();
	}

}
