package com.mendeley.api.model;

import java.util.ArrayList;
import java.util.List;

public class Employment {

	public List<String> classes;
	public Integer id;
	public String lastModified;
	public String position;
	public String created;
	public String institution;
	public String startDate;
	public String endDate;
	public String website;
	public Boolean isMainEmployment;
	
	public Employment() {
		classes = new ArrayList<String>();
	}
	
	@Override
	public String toString() {
		
		String classesString = "";
		for (String s : classes) {
			classesString += "\n" + s;
		}
		
		return "id: " + id + 
				", lastModified: " + lastModified +
				", position: " + position +
				", created: " + created +
				", institution: " + institution +
				", startDate: " + startDate +
				", endDate: " + endDate +
				", website: " + website + 
				", mainEmployment: " + isMainEmployment + 
				", classes: " + classesString;
	}
}
