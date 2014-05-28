package com.mendeley.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing document json object.
 *
 */
public class Document {
		
	public String lastModified;
	public String groupId;
	public String profileId;
	public Boolean read;
	public Boolean starred;
	public Boolean authored;
	public Boolean confirmed;
	public Boolean hidden;
	public String id;
	public String type;
	public Integer month;
	public Integer year;
	public Integer day;	
	public String source;
	public String title;
	public String revision;
	public Map<String, String> identifiers;
	public String abstractString;
	public ArrayList<Person> authors;
	public String added;
	public String pages;
	public String volume;
	public String issue;
	public String website;
	public String publisher;
	public String city;
	public String edition;
	public String institution;
	public String series;
	public String chapter;
	public ArrayList<Person> editors;
	
	public boolean downloaded;
		
	public Document() {
		authors = new ArrayList<Person>();
		editors = new ArrayList<Person>();
		identifiers = new HashMap<String, String>();
	}
	
	public Document(String id, String title, ArrayList<Person> authors, int year) {
		this.id = id;
		this.title = title;
		this.authors = authors;
		this.year = year;
	}
	
	public String getAuthorsString() {
		String authorsString = "";
		for (int i = 0; i < authors.size(); i++) {
			if (i > 0) {
				authorsString +=", ";				
			}
			authorsString += authors.get(i).surname + " " + authors.get(i).forename;
		}
		
		return authorsString;
	}
	
	public String getYearString() {
		return "("+year+")";
	}
	
	@Override
	public boolean equals(Object object) {
		
		Document other;
		
		try {
			other = (Document) object;
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
	
	@Override
	public String toString() {
		
		return "lastModified: " + lastModified +
				", groupId: " + groupId +
				", profileId: " + profileId +
				", read: " + read +
				", starred: " + starred +
				", authored: " + authored +
				", confirmed: " + confirmed +
				", hidden: " + hidden +
				", id: " + id +
				", type: " + type +
				", month: " + month +
				", year: " + year +
				", day: " + day +
				", source: " + source +
				", title: " + title +
				", revision: " + revision +
				", added: " + added +
				", pages: " + pages +
				", volume: " + volume +
				", issue: " + issue +
				", website: " + website +
				", publisher: " + publisher +
				", city: " + city +
				", edition: " + edition +
				", institution: " + institution +
				", series: " + series +
				", chapter: " + chapter +
				", abstract: " + abstractString +
				", authors: " + authors.size() +
				", editors: " + editors.size() +
				", identifiers: " + identifiers.size();
	}
}
