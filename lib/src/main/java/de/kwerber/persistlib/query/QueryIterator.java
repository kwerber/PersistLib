package de.kwerber.persistlib.query;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.ResultRetrieveException;
import de.kwerber.persistlib.exception.SerializeException;
import de.kwerber.persistlib.util.Check;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class QueryIterator<T, Id> implements Iterator<T> {

	private final ResultSet result;
	private final PersistenceContext<T, Id> context;
	private boolean hasNext;

	public QueryIterator(ResultSet result, PersistenceContext<T, Id> ctx) throws ResultRetrieveException {
		Check.notNull(result);
		Check.notNull(ctx);

		this.result = result;
		this.context = ctx;

		try {
			this.hasNext = result.next();
		}
		catch (SQLException e) {
			throw new ResultRetrieveException(e);
		}
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() throws ResultRetrieveException {
		Map<String, Object> valueMap = new HashMap<>();

		try {
			for (PersistentAttribute attribute : this.context.getHandler().getAttributes()) {
				if (!this.context.getTypeMapper().applies(attribute.getFieldType())) {
					throw new SerializeException("cannot deserialize attribute: " + attribute);
				}

				Object value = this.context.getTypeMapper().deserialize(attribute, result);
				valueMap.put(attribute.getId(), value);
			}

			T instance = this.context.getHandler().createInstance(valueMap);

			this.hasNext = this.result.next();

			return instance;
		}
		catch (SQLException e) {
			this.hasNext = false;
			throw new ResultRetrieveException(e);
		}
	}

}
