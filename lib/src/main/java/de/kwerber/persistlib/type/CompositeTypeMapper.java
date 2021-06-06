package de.kwerber.persistlib.type;

import de.kwerber.persistlib.PersistentAttribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CompositeTypeMapper implements TypeMapper {

	private final List<TypeMapper> mappers;

	public CompositeTypeMapper(List<TypeMapper> mappers) {
		this.mappers = mappers;
	}

	TypeMapper findMapper(Class<?> type) {
		for (TypeMapper mapper : mappers) {
			if (mapper.applies(type)) {
				return mapper;
			}
		}

		throw new IllegalArgumentException("no mapper applies: " + type);
	}

	@Override
	public boolean applies(Class<?> type) {
		return mappers.stream().anyMatch(mapper -> mapper.applies(type));
	}

	@Override
	public String getSQLDefinition(PersistentAttribute attribute) {
		return findMapper(attribute.getFieldType()).getSQLDefinition(attribute);
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
		return findMapper(attribute.getFieldType()).deserialize(attribute, resultSet);
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, Object object) {
		return findMapper(attribute.getFieldType()).deserialize(attribute, object);
	}

	@Override
	public Object serialize(Object value) {
		return findMapper(value.getClass()).serialize(value);
	}

}
