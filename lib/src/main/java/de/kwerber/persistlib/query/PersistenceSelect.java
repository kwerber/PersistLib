package de.kwerber.persistlib.query;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.exception.PersistenceQueryException;
import de.kwerber.persistlib.util.Check;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PersistenceSelect<T, Id> implements Iterable<T> {

	private final PersistenceContext<T, Id> ctx;

	private WhereClause whereClause;
	private Optional<Integer> limit = Optional.empty();
	private Optional<OrderByClause> orderBy = Optional.empty();

	public PersistenceSelect(PersistenceContext<T, Id> ctx) {
		Check.notNull(ctx);
		this.ctx = ctx;
	}

	public ParameterizedQuery toParameterizedQuery() {
		return this.ctx.getTranslator().getSelectQuery(this.ctx, this);
	}

	public WhereClause getWhereClause() {
		return this.whereClause;
	}

	public Optional<Integer> getLimit() {
		return this.limit;
	}

	public Optional<OrderByClause> getOrderByClause() { return this.orderBy; }

	public PersistenceSelect<T, Id> where(String clause, Object... args) {
		this.whereClause = new WhereClause(clause, args);
		return this;
	}

	public PersistenceSelect<T, Id> limit(int limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("limit must not be negative: " + limit);
		}

		this.limit = Optional.of(limit);
		return this;
	}

	public PersistenceSelect<T, Id> orderAscBy(String column) {
		Check.notNullNotEmpty(column);
		this.orderBy = Optional.of(new OrderByClause(OrderByClause.Order.ASC, column));
		return this;
	}

	public PersistenceSelect<T, Id> orderDescBy(String column) {
		Check.notNullNotEmpty(column);
		this.orderBy = Optional.of(new OrderByClause(OrderByClause.Order.DESC, column));
		return this;
	}

	public T first() throws PersistenceQueryException {
		return firstMaybe().orElseThrow();
	}

	public Optional<T> firstMaybe() throws PersistenceQueryException {
		Iterator<T> iterator = this.iterator();
		return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
	}

	public List<T> toList() throws PersistenceQueryException {
		ArrayList<T> list = new ArrayList<>();
		Iterator<T> iterator = this.iterator();
		iterator.forEachRemaining(list::add);
		return list;
	}

	public Set<T> toSet() throws PersistenceQueryException {
		HashSet<T> set = new HashSet<>();
		Iterator<T> iterator = this.iterator();
		iterator.forEachRemaining(set::add);
		return set;
	}

	public Stream<T> toStream() throws PersistenceQueryException {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
			false);
	}

	@Override
	public Iterator<T> iterator() throws PersistenceQueryException {
		ResultSet result = this.ctx.getExecutor().executeAndReturnResult(this.toParameterizedQuery());
		return new QueryIterator<T, Id>(result, this.ctx);
	}

}
