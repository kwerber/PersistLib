package de.kwerber.persistlib.type.functions;

public interface ValueSerializer<T> {

	Object serialize(T value);

}
