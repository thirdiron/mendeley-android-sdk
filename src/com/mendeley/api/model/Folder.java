package com.mendeley.api.model;

public class Folder {

	public String name;
	public String parent;
	public String id;
	public String groupId;
	public String added;
	
	public Folder (String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "name: " + name + 
			   ", parent: " + parent + 
			   ", id: " + id + 
			   ", groupId: " + groupId + 
			   ", added: " + added;
	}
	
	@Override
	public boolean equals(Object object) {
		
		Folder other;
		
		try {
			other = (Folder) object;
		}
		catch (ClassCastException e) {
			return false;
		}
		
		if (other == null) {
			return false;
		} else {
			return other.id.equals(this.id);
		}
	}
}
