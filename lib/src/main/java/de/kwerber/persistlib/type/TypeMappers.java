package de.kwerber.persistlib.type;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.type.functions.ResultSetDeserializer;
import de.kwerber.persistlib.type.functions.ValueDeserializer;
import de.kwerber.persistlib.type.functions.ValueSerializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class TypeMappers {

	public static <T> TypeMapper of(
		Class<T> type,
		String sqlType,
		ResultSetDeserializer resultSetDeserializer,
		ValueDeserializer valueDeserializer,
		ValueSerializer<T> serializer
	) {
		return new TypeMapper() {
			@Override
			public boolean applies(Class<?> typeToCheck) {
				return type.equals(typeToCheck);
			}

			@Override
			public String getSQLDefinition(PersistentAttribute attribute) {
				return sqlType
					+ TypeMapperUtils.handleNullable(attribute)
					+ TypeMapperUtils.handleAutoIncrement(attribute)
					+ TypeMapperUtils.handleKey(attribute);
			}

			@Override
			public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
				return resultSetDeserializer.deserialize(attribute.getId(), resultSet);
			}

			@Override
			public Object deserialize(PersistentAttribute attribute, Object object) {
				return valueDeserializer.deserialize(object);
			}

			@Override
			public Object serialize(Object value) {
				return serializer.serialize((T) value);
			}
		};
	}

	public static <T> TypeMapper toAndFromString(Class<T> type, int maxLength, Function<String, T> fromString) {
		return toAndFromString(
			type,
			maxLength,
			fromString,
			String::valueOf
		);
	}

	public static <T> TypeMapper toAndFromString(Class<T> type, int maxLength, Function<String, T> fromString, Function<T, String> toString) {
		return of(
			type,
			"VARCHAR(" + maxLength + ")",
			(colName, resultSet) -> fromString.apply(resultSet.getString(colName)),
			val -> fromString.apply(String.valueOf(val)),
			toString::apply
		);
	}

	public static <T> TypeMapper toAndFromInt(Class<T> type, Function<Integer, T> fromInt, Function<T, Integer> toInt) {
		return of(
			type,
			"INTEGER",
			(colName, resultSet) -> fromInt.apply(resultSet.getInt(colName)),
			val -> fromInt.apply(Integer.parseInt(String.valueOf(val))),
			toInt::apply
		);
	}


}
