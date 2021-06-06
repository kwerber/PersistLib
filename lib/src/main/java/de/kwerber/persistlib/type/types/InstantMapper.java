package de.kwerber.persistlib.type.types;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.type.TypeMapperUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class InstantMapper implements TypeMapper {

	@Override
	public boolean applies(Class<?> type) {
		return type.equals(Instant.class);
	}

	@Override
	public String getSQLDefinition(PersistentAttribute attribute) {
		Column column = attribute.getColumnData().orElse(null);
		Id id = attribute.getIdData().orElse(null);

		if (column != null && !column.columnDefinition().isEmpty()) {
			return column.columnDefinition();
		}

		String sql = "TIMESTAMP";

		// NULLABLE
		sql += TypeMapperUtils.handleNullable(attribute);

		// KEY
		sql += TypeMapperUtils.handleKey(attribute);

		return sql;
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
		return resultSet.getTimestamp(attribute.getId()).toInstant();
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, Object object) {
		return ((Timestamp) object).toInstant();
	}

	@Override
	public Object serialize(Object value) {
		return Timestamp.from((Instant) value);
	}

}
