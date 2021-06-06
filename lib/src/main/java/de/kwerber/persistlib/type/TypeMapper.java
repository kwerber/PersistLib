package de.kwerber.persistlib.type;

import de.kwerber.persistlib.PersistentAttribute;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeMapper {

	boolean applies(Class<?> type);

	String getSQLDefinition(PersistentAttribute attribute);

	Object deserialize(PersistentAttribute attribute, ResultSet resultSet) throws SQLException;

	Object deserialize(PersistentAttribute attribute, Object object);

	Object serialize(Object value);

}
