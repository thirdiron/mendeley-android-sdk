package com.mendeley.api.model;

/**
 * Model class representing education json object.
 *
 */
public class Education {
	
	public Integer id;
	public String lastModified;
	public String created;
	public String degree;
	public String institution;
	public String startDate;
	public String endDate;
	public String website;
	
	public Education() {}
	
	@Override
	public String toString() {
		return "id: " + id + 
				", lastModified: " + lastModified +
				", created: " + created +
				", degree: " + degree +
				", institution: " + institution +
				", startDate: " + startDate +
				", endDate: " + endDate +
				", website: " + website;
	}
}
