package com.mendeley.api.model;

import java.util.ArrayList;
import java.util.List;

public class Profile {

	public String location;
	public String id;
	public String displayName;
	public String userType;
	public String url;
	public String email;
	public String firstName;
	public String lastName;
	public String researchInterests;
	public String academicStatus;
	public Boolean verified;
	public String createdAt;
	
	public Discipline discipline;
	public Photo photo;
	
	public List<Education> education;
	public ArrayList<Employment> employment;
	
	public Profile() {
		education = new ArrayList<Education>();
		employment = new ArrayList<Employment>();
	}
	
	@Override
	public String toString() {
		
		String educationString = "";
		String employmentString = "";
		
		for (Education e : education) {
			educationString = "\n" + e;
		}
		
		for (Employment e : employment) {
			employmentString = "\n" + e;
		}
		
		return "location: " + location + 
				", id: " + id + 
				", displayName: " + displayName + 
				", userType: " + userType + 
				", url: " + url + 
				", email: " + email + 
				", firstName: " + firstName + 
				", lastName: " + lastName + 
				", researchInterests: " + researchInterests + 
				", academicStatus: " + academicStatus + 
				", verified: " + verified + 
				", createdAt: " + createdAt + 
				", discipline: " + discipline + 
				", photo: " + photo +
				", employment: " + employmentString +
				", education: " + educationString;			
	}
}
