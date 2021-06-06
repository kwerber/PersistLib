package de.kwerber.persistlib.creator;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.QueryExecuteException;
import de.kwerber.persistlib.executor.QueryExecutor;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.util.Check;

public class MariadbCreator implements TableCreator {

	@Override
	public <T, Id> void createTable(PersistenceContext<T, Id> ctx) throws QueryExecuteException {
		Check.notNull(ctx);

		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS `").append(ctx.getTableName()).append("` (").append('\n');

		for (int i = 0; i < ctx.getHandler().getAttributes().size(); i++) {
			PersistentAttribute attribute = ctx.getHandler().getAttributes().get(i);

			b.append('`').append(attribute.getId()).append("` ");
			b.append(ctx.getTypeMapper().getSQLDefinition(attribute));

			if (i < ctx.getHandler().getAttributes().size() - 1) {
				b.append(", ");
			}

			b.append('\n');
		}

		b.append(");");

		ParameterizedQuery query = new ParameterizedQuery(b.toString());

		QueryExecutor executor = ctx.getExecutor();
		executor.execute(query);
	}

}
