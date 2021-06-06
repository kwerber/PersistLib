package de.kwerber.persistlib.type.types;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.type.TypeMapperUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DecimalMapper implements TypeMapper {

	@Override
	public boolean applies(Class<?> type) {
		return type.equals(float.class) || type.equals(Float.class)
			|| type.equals(double.class) || type.equals(Double.class);
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
		if (type.equals(float.class) || type.equals(Float.class)) {
			sql = "FLOAT";
		}
		else if (type.equals(double.class) || type.equals(Double.class)) {
			sql += "DOUBLE";
		}

		// PRECISION
		if (column != null && column.scale() > 0) {
			sql += "(" + column.scale();

			if (column.precision() > 0) {
				sql += "," + column.precision();
			}

			sql += ")";
		}

		// NULLABLE
		sql += TypeMapperUtils.handleNullable(attribute);

		// KEY
		sql += TypeMapperUtils.handleKey(attribute);

		return sql;
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
		Class<?> type = attribute.getFieldType();

		if (type.equals(float.class) || type.equals(Float.class)) {
			return resultSet.getFloat(attribute.getId());
		}
		else if (type.equals(double.class) || type.equals(Double.class)) {
			return resultSet.getDouble(attribute.getId());
		}
		else {
			throw new IllegalArgumentException("attribute has invalid type: " + type);
		}
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, Object object) {
		Class<?> type = attribute.getFieldType();

		if (type.equals(float.class) || type.equals(Float.class)) {
			return Float.parseFloat(String.valueOf(object));
		}
		else if (type.equals(double.class) || type.equals(Double.class)) {
			return Double.parseDouble(String.valueOf(object));
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
