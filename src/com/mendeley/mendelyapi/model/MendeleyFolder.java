package com.mendeley.mendelyapi.model;

public class MendeleyFolder {

	public String name;
	public String parent;
	public String id;
	public String group;
	public String added;
	
	
	public MendeleyFolder (String name) {
		this.name = name;
	}
	
	
	public MendeleyFolder (String name, String parent, String id, String group, String added) {
		this.name = name;
		this.parent = parent;
		this.id = id;
		this.group = group;
		this.added = added;
	}
}
