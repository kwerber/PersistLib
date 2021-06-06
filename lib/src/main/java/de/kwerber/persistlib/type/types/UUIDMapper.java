package de.kwerber.persistlib.type.types;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.type.TypeMapperUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDMapper implements TypeMapper {

	@Override
	public boolean applies(Class<?> type) {
		return type.equals(UUID.class);
	}

	@Override
	public String getSQLDefinition(PersistentAttribute attribute) {
		Column column = attribute.getColumnData().orElse(null);
		Id id = attribute.getIdData().orElse(null);

		if (column != null && !column.columnDefinition().isEmpty()) {
			return column.columnDefinition();
		}

		String sql = "CHAR(36)";

		// NULLABLE
		sql += TypeMapperUtils.handleNullable(attribute);

		// KEY
		sql += TypeMapperUtils.handleKey(attribute);

		return sql;
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
		return UUID.fromString(resultSet.getString(attribute.getId()));
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, Object object) {
		return UUID.fromString(String.valueOf(object));
	}

	@Override
	public Object serialize(Object value) {
		return String.valueOf(value);
	}

}
