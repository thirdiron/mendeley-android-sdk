package com.mendeley.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Document {
	
	
	public String lastModified;
	public String groupId;
	public String profileId;
	public boolean read;
	public boolean starred;
	public boolean authored;
	public boolean confirmed;
	public boolean hidden;
	public String id;
	public String type;
	public int month;
	public int year;
	public int day;	
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
	
	public ArrayList<DocumentFile> files;
	public boolean downloaded;
		
	public Document() {
		authors = new ArrayList<Person>();
		editors = new ArrayList<Person>();
		files = new ArrayList<DocumentFile>();
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
