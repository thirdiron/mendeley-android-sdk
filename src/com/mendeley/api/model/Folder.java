package com.mendeley.api.model;

public class Folder {

	public String name;
	public String parent;
	public String id;
	public String group;
	public String added;
	
	
	public Folder (String name) {
		this.name = name;
	}
	
	
	public Folder (String name, String parent, String id, String group, String added) {
		this.name = name;
		this.parent = parent;
		this.id = id;
		this.group = group;
		this.added = added;
	}
}
