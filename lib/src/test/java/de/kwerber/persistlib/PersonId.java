package de.kwerber.persistlib;

import java.util.Objects;
import java.util.UUID;

public class PersonId {

	private final UUID id;

	public PersonId(String idAsString) {
		this.id = UUID.fromString(idAsString);
	}

	public PersonId() {
		this.id = UUID.randomUUID();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PersonId)) return false;
		PersonId personId = (PersonId) o;
		return id.equals(personId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return id.toString();
	}

}
