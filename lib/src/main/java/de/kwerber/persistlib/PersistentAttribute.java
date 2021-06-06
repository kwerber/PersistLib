package de.kwerber.persistlib;

import de.kwerber.persistlib.util.Check;
import de.kwerber.persistlib.util.Utils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Optional;

public class PersistentAttribute {

	private final String id;
	private final String fieldName;
	private final Class<?> fieldType;
	private final Optional<Column> columnData;
	private final Optional<Id> idData;
	private final Optional<GeneratedValue> generatedValueData;
	private final boolean autoIncremented;

	public PersistentAttribute(String id, String fieldName, Class<?> fieldType,
	                           Optional<Column> columnData, Optional<Id> idData,
	                           Optional<GeneratedValue> generatedValueData,
	                           boolean autoIncremented) {

		Check.notNullNotEmpty(id);
		Check.notNullNotEmpty(fieldName);
		Check.notNull(fieldType);
		Check.notNull(columnData);
		Check.notNull(idData);
		Check.notNull(generatedValueData);

		this.id = id;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.columnData = columnData;
		this.idData = idData;
		this.generatedValueData = generatedValueData;
		this.autoIncremented = autoIncremented;
	}

	public String getId() {
		return id;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public Optional<Column> getColumnData() {
		return columnData;
	}

	public Optional<Id> getIdData() {
		return idData;
	}

	public Optional<GeneratedValue> getGeneratedValueData() {
		return generatedValueData;
	}

	public boolean isAutoIncremented() { return autoIncremented; }

	public boolean isNullable() {
		return false;
	} // TODO

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PersistentAttribute)) return false;
		PersistentAttribute that = (PersistentAttribute) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

}
