package de.kwerber.persistlib.type;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.type.types.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CommonMapper implements TypeMapper {

	private final CompositeTypeMapper compositeMapper;

	public CommonMapper() {
		this.compositeMapper = new CompositeTypeMapper(List.of(
			new StringMapper(),
			new IntegerMapper(),
			new DecimalMapper(),
			new BoolMapper(),
			new UUIDMapper(),
			new InstantMapper()
		));
	}

	@Override
	public boolean applies(Class<?> type) {
		return compositeMapper.applies(type);
	}

	@Override
	public String getSQLDefinition(PersistentAttribute attribute) {
		return compositeMapper.getSQLDefinition(attribute);
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException {
		return compositeMapper.deserialize(attribute, resultSet);
	}

	@Override
	public Object deserialize(PersistentAttribute attribute, Object object) {
		return compositeMapper.deserialize(attribute, object);
	}

	@Override
	public Object serialize(Object value) {
		return compositeMapper.serialize(value);
	}

}
