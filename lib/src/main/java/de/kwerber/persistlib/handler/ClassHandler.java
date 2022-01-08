package de.kwerber.persistlib.handler;

import de.kwerber.persistlib.PersistentAttribute;

import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClassHandler<T, Id> {

	List<PersistentAttribute> getAttributes();

	Map<String, Object> getAttributeValues(T instance);

	PersistentAttribute getPrimaryAttribute();

	Id getPrimaryValue(T instance);

	void setPrimaryValue(T instance, Id id);

	T createInstance(Map<String, Object> attributeValues);

	Optional<Table> getTableData();

}
