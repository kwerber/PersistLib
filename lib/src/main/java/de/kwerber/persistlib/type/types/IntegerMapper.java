package de.kwerber.persistlib.type.types;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.type.TypeMapperUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerMapper implements TypeMapper {

	@Override
	public boolean applies(Class<?> type) {
		return type.equals(byte.class) || type.equals(Byte.class)
			|| type.equals(short.class) || type.equals(Short.class)
			|| type.equals(int.class) || type.equals(Integer.class)
			|| type.equals(long.class) || type.equals(Long.class);
	}

	@Override
	public String getSQLDefinition(PersistentAttribute attribute) {
		Column column = attribute.getColumnData().orElse(null);
		Id id = attribute.getIdData().orElse(null);
		Class<?> type = attribute.getFieldType();
		String sql = "";

		if (column != null && !column.columnDefinition().isEmpty()) {
			return column.columnDefinition();
		}

		// TYPE
		if (type.equals(byte.class) || type.equals(Byte.class)) {
			sql = " TINYINT UNSIGNED";
		}
		else if (type.equals(short.class) || type.equals(Short.class)) {
			sql += " SMALLINT";
		}
		else if (type.equals(int.class) || type.equals(Integer.class)) {
			sql += " INTEGER";
		}
		else if (type.equals(long.class) || type.equals(Long.class)) {
			sql += " BIGINT";
		}

		// NULLABLE
		sql += TypeMapperUtils.handleNullable(attribute);

		// AUTO INCREMENT
		sql += TypeMapperUtils.handleAutoIncrement(attribute);

		// KEY
		sql += TypeMapperUtils.handleKey(attribute);

		return sql;
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
		Class<?> type = attribute.getFieldType();

		if (type.equals(byte.class) || type.equals(Byte.class)) {
			return resultSet.getByte(attribute.getId());
		}
		else if (type.equals(short.class) || type.equals(Short.class)) {
			return resultSet.getShort(attribute.getId());
		}
		else if (type.equals(int.class) || type.equals(Integer.class)) {
			return resultSet.getInt(attribute.getId());
		}
		else if (type.equals(long.class) || type.equals(Long.class)) {
			return resultSet.getLong(attribute.getId());
		}
		else {
			throw new IllegalArgumentException("attribute has invalid type: " + type);
		}
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, Object object) {
		Class<?> type = attribute.getFieldType();

		if (type.equals(byte.class) || type.equals(Byte.class)) {
			return Byte.parseByte(String.valueOf(object));
		}
		else if (type.equals(short.class) || type.equals(Short.class)) {
			return Short.parseShort(String.valueOf(object));
		}
		else if (type.equals(int.class) || type.equals(Integer.class)) {
			return Integer.parseInt(String.valueOf(object));
		}
		else if (type.equals(long.class) || type.equals(Long.class)) {
			return Long.parseLong(String.valueOf(object));
		}
		else {
			throw new IllegalArgumentException("attribute has invalid type: " + type);
		}
	}

	@Override
	public Object serialize(Object value) {
		return value;
	}

}
