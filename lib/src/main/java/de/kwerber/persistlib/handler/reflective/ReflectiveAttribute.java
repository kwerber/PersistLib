package de.kwerber.persistlib.handler.reflective;

import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.PersistenceReflectionException;
import de.kwerber.persistlib.util.Check;
import de.kwerber.persistlib.util.Utils;
import de.kwerber.persistlib.util.annotation.AutoIncrement;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Optional;

public class ReflectiveAttribute extends PersistentAttribute {

	private static String id(Field field) {
		String id = field.getName().toLowerCase(Locale.ROOT);

		id = Utils.camelCaseToSnakeCase(id);

		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);

			if (!column.name().isEmpty()) {
				id = column.name();
			}
		}

		return id;
	}

	private final Field field;

	public ReflectiveAttribute(Field field) {
		super(id(field), field.getName(), field.getType(),
			Optional.ofNullable(field.getAnnotation(Column.class)),
			Optional.ofNullable(field.getAnnotation(Id.class)),
			Optional.ofNullable(field.getAnnotation(GeneratedValue.class)),
			field.isAnnotationPresent(AutoIncrement.class)
		);

		Check.notNull(field);

		this.field = field;
	}

	void setValue(Object instance, Object value) {
		Check.notNull(instance);

		try {
			this.field.set(instance, value);
		}
		catch (IllegalAccessException e) {
			throw new PersistenceReflectionException(e);
		}
	}

	Object getValue(Object instance) {
		Check.notNull(instance);

		try {
			return this.field.get(instance);
		}
		catch (IllegalAccessException e) {
			throw new PersistenceReflectionException(e);
		}
	}

}
