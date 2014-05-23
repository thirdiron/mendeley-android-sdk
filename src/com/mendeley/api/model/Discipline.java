package com.mendeley.api.model;

public class Discipline {

	public String name;
	
	public Discipline() {}
	
	public Discipline(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "name: " + name;
	}
}
