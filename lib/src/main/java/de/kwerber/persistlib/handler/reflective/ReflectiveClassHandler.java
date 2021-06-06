package de.kwerber.persistlib.handler.reflective;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.PersistenceReflectionException;
import de.kwerber.persistlib.handler.ClassHandler;
import de.kwerber.persistlib.util.Check;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectiveClassHandler<T, IdType> implements ClassHandler<T, IdType> {

	private final Class<T> clazz;
	private final List<ReflectiveAttribute> attributeList = new ArrayList<>();
	private final ReflectiveAttribute primaryAttribute;

	public ReflectiveClassHandler(Class<T> clazz) {
		Check.notNull(clazz);

		this.clazz = clazz;

		ReflectiveAttribute foundPrimary = null;

		for (Field field : this.clazz.getDeclaredFields()) {
			if (Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(Transient.class)) {
				continue;
			}

			field.setAccessible(true);

			ReflectiveAttribute attribute = new ReflectiveAttribute(field);
			this.attributeList.add(attribute);

			if (field.isAnnotationPresent(Id.class)) {
				foundPrimary = attribute;
			}
		}

		this.primaryAttribute = foundPrimary;
	}

	@Override
	public List<PersistentAttribute> getAttributes() { return Collections.unmodifiableList(this.attributeList); }

	@Override
	public Map<String, Object> getAttributeValues(T instance) {
		Check.notNull(instance);

		return this.attributeList.stream()
			.collect(Collectors.toMap(PersistentAttribute::getId, a -> a.getValue(instance)));
	}

	@Override
	public PersistentAttribute getPrimaryAttribute() { return this.primaryAttribute; }

	@Override
	public IdType getPrimaryValue(T instance) {
		Check.notNull(instance);

		return (IdType) this.primaryAttribute.getValue(instance);
	}

	@Override
	public void setPrimaryValue(T instance, IdType id) {
		Check.notNull(instance);
		this.primaryAttribute.setValue(instance, id);
	}

	@Override
	public T createInstance(Map<String, Object> attributeValues) {
		Check.notNull(attributeValues);

		try {
			// Construct instance
			Object instance = null;

			outer: for (Constructor<?> constr : this.clazz.getDeclaredConstructors()) {
				constr.setAccessible(true);

				if (constr.getParameterCount() == 0) {
					// Always prefer empty constructor
					instance = constr.newInstance();

					// Set values
					for (ReflectiveAttribute attribute : this.attributeList) {
						Object value = attributeValues.get(attribute.getId());
						attribute.setValue(instance, value);
					}

					break outer;
				}
				else if (constr.getParameterCount() == this.attributeList.size()) {
					// Check if arguments match all attributes
					Object[] paramValues = new Object[constr.getParameterCount()];

					for (int i = 0; i < constr.getParameters().length; i++) {
						Parameter param = constr.getParameters()[i];

						if (!param.getType().equals(this.attributeList.get(i).getFieldType())) {
							// constructor does not match!
							continue outer;
						}

						ReflectiveAttribute attribute = this.attributeList.get(i);

						// Found attribute => remember value
						if (!attributeValues.containsKey(attribute.getId())) {
							// No value for attribute present (maybe nullable?)
							if (attribute.isNullable()) {
								paramValues[i] = null;
							}
							else {
								// missing value for this constructor
								continue outer;
							}
						}
						else {
							paramValues[i] = attributeValues.get(attribute.getId());
						}
					}

					// Constructor seems to be suitable => use it!
					instance = constr.newInstance(paramValues);

					break outer;
				}
			}

			return (T) instance;
		}
		catch (Exception e) {
			throw new PersistenceReflectionException(e);
		}
	}

}
