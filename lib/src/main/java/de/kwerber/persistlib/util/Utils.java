package de.kwerber.persistlib.util;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.SerializeException;
import de.kwerber.persistlib.type.TypeMapper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utils {

	public static String generateParameters(int count) {
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < count; i++) {
			b.append('?');

			if (i < count - 1) {
				b.append(", ");
			}
		}

		return b.toString();
	}

	public static <T> Object[] collectQueryParams(PersistenceContext<T, ?> ctx, List<PersistentAttribute> attributes,
	                                              T instance) throws SerializeException {
		Check.notNull(ctx);
		Check.notNull(attributes);
		Check.notNull(instance);

		Map<String, Object> valueMap = ctx.getHandler().getAttributeValues(instance);
		TypeMapper typeMapper = ctx.getTypeMapper();

		Object[] args = new Object[attributes.size()];

		for (int i = 0; i < args.length; i++) {
			PersistentAttribute attribute = attributes.get(i);

			Object value = valueMap.get(attribute.getId());
			if (value == null) { continue; }

			if (!typeMapper.applies(value.getClass())) {
				throw new SerializeException("cannot serialize type: " + value.getClass());
			}

			args[i] = typeMapper.serialize(value);
		}

		return args;
	}

	public static Object[] append(Object[] array, Object element) {
		Object[] newArray = new Object[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length] = element;
		return newArray;
	}

	public static String camelCaseToSnakeCase(String string) {
		String regex = "([a-z])([A-Z]+)";
		String replacement = "$1_$2";

		return string.replaceAll(regex, replacement).toLowerCase(Locale.ROOT);
	}

}
