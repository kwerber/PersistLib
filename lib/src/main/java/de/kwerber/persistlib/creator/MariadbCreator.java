package de.kwerber.persistlib.creator;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.QueryExecuteException;
import de.kwerber.persistlib.executor.QueryExecutor;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.util.Check;

import javax.persistence.Index;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

public class MariadbCreator implements TableCreator {

	@Override
	public <T, Id> void createTable(PersistenceContext<T, Id> ctx) throws QueryExecuteException {
		Check.notNull(ctx);

		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS `").append(ctx.getTableName()).append("` (").append('\n');

		// Iterate over all attributes
		List<String> createDefinitions = new ArrayList<>();

		for (int i = 0; i < ctx.getHandler().getAttributes().size(); i++) {
			PersistentAttribute attribute = ctx.getHandler().getAttributes().get(i);
			String definition = "";

			definition += '`' + attribute.getId() + "` ";
			definition += ctx.getTypeMapper().getSQLDefinition(attribute);

			createDefinitions.add(definition);
		}

		// Maybe provide some additional information (indices etc.)
		if (ctx.getHandler().getTableData().isPresent()) {
			Table table = ctx.getHandler().getTableData().get();

			for (Index index : table.indexes()) {
				String definition = "";

				if (index.unique()) {
					definition += "UNIQUE INDEX";
				}
				else {
					definition += "INDEX";
				}

				definition += " `" + index.name() + "` ";

				definition += "(" + index.columnList() + ")";

				createDefinitions.add(definition);
			}
		}

		b.append(String.join(",\n", createDefinitions));
		b.append("\n);");

		ParameterizedQuery query = new ParameterizedQuery(b.toString());

		QueryExecutor executor = ctx.getExecutor();
		executor.execute(query);
	}

}
