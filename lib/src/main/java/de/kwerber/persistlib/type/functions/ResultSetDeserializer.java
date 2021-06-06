package de.kwerber.persistlib.type.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetDeserializer {

	Object deserialize(String colName, ResultSet resultSet) throws SQLException;

}
