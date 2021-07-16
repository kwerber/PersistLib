package de.kwerber.persistlib;

import javax.persistence.Id;
import java.util.Objects;

public class Person {

	@Id
	private final PersonId id;
	private String name;
	private int age;
	private boolean alive;

	private Person(PersonId id, String name, int age, boolean alive) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.alive = alive;
	}

	public Person(String name, int age, boolean alive) {
		this.id = new PersonId();
		this.name = name;
		this.age = age;
		this.alive = alive;
	}

	public PersonId getId() { return id; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Person)) return false;
		Person person = (Person) o;
		return Objects.equals(getId(), person.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return "Person{" +
			"id=" + id +
			", name='" + name + '\'' +
			", age=" + age +
			", alive=" + alive +
			'}';
	}

}