package de.kwerber.persistlib.query;

import de.kwerber.persistlib.util.Check;

public class OrderByClause {

	enum Order { ASC, DESC }

	private final Order order;
	private final String column;

	public OrderByClause(Order order, String column) {
		Check.notNull(order);
		Check.notNullNotEmpty(column);
		this.order = order;
		this.column = column;
	}

	public Order getOrder() {
		return order;
	}

	public String getColumn() {
		return column;
	}

}
