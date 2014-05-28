package com.mendeley.api.model;

/**
 * Model class representing person json object.
 *
 */
public class Person {

	public String forename;
	public String surname;
	
	public Person(String forename, String surname) {
		this.forename = forename;
		this.surname = surname;
	}

}
