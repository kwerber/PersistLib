package de.kwerber.persistlib.type;

import de.kwerber.persistlib.PersistentAttribute;

import javax.persistence.Column;
import javax.persistence.Id;

public class TypeMapperUtils {

	public static String handleNullable(PersistentAttribute attribute) {
		if (attribute.isNullable()) {
			return " NULL";
		}
		else {
			return " NOT NULL";
		}
	}

	public static String handleKey(PersistentAttribute attribute) {
		Column column = attribute.getColumnData().orElse(null);
		Id id = attribute.getIdData().orElse(null);

		if (column != null && column.unique() && id == null) {
			return " UNIQUE KEY";
		}
		else if (id != null) {
			return " PRIMARY KEY";
		}
		else {
			return "";
		}
	}

	public static String handleAutoIncrement(PersistentAttribute attribute) {
		return attribute.isAutoIncremented() ? " AUTO_INCREMENT" : "";
	}


}
