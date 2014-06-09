package com.mendeley.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class representing document json object.
 *
 */
public class Document implements Parcelable {
		
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
	
	public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {
	    public Document createFromParcel(Parcel in) {
	    	return new Document(in);
	    }

	    public Document[] newArray(int size) {
	    	return new Document[size];
	    }
	};
	
	@Override
	public int describeContents() {
		return 0;
	} 
	  
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(lastModified);
	    dest.writeString(groupId);
	    dest.writeString(profileId);
	    dest.writeByte((byte) (read == null ? -1 : (read ? 1 : 0))); 
	    dest.writeByte((byte) (starred == null ? -1 : (starred ? 1 : 0))); 
	    dest.writeByte((byte) (authored == null ? -1 : (authored ? 1 : 0))); 
	    dest.writeByte((byte) (confirmed == null ? -1 : (confirmed ? 1 : 0))); 
	    dest.writeByte((byte) (hidden == null ? -1 : (hidden ? 1 : 0))); 
	    dest.writeString(id);
	    dest.writeString(type);
	    dest.writeInt(month == null ? -1 : month);
	    dest.writeInt(year == null ? -1 : year);
	    dest.writeInt(day == null ? -1 : day);
	    dest.writeString(source);
	    dest.writeString(title);
	    dest.writeString(revision);
	    
	    dest.writeInt(identifiers.size());
	    for(String key : identifiers.keySet()){
	    	dest.writeString(key);
	    	dest.writeString(identifiers.get(key));
	    }
	    
		dest.writeString(abstractString);
		
		dest.writeInt(authors.size());
	    for(Person person : authors){
	    	dest.writeString(person.forename);
	    	dest.writeString(person.surname);
	    }
	    
		dest.writeString(added);
		dest.writeString(pages);
		dest.writeString(volume);
		dest.writeString(issue);
		dest.writeString(website);
		dest.writeString(publisher);
		dest.writeString(city);
		dest.writeString(edition);
		dest.writeString(institution);
		dest.writeString(series);
		dest.writeString(chapter);

		dest.writeInt(editors.size());
	    for(Person person : editors){
	    	dest.writeString(person.forename);
	    	dest.writeString(person.surname);
	    }
	    
	    dest.writeByte((byte) (downloaded ? 1 : 0));  
	}
	
	  
	private Document(Parcel in) {
		lastModified = in.readString();
		groupId = in.readString();
		profileId = in.readString();
		read = in.readByte() != 0; 
		starred =  (in.readByte() == -1 ? null : (in.readByte() != 0));
		authored = (in.readByte() == -1 ? null : (in.readByte() != 0));
		confirmed = (in.readByte() == -1 ? null : (in.readByte() != 0));
		hidden = (in.readByte() == -1 ? null : (in.readByte() != 0));
		id = in.readString();
		type = in.readString();
		month = (in.readInt() == -1 ?  null : in.readInt());
		year = (in.readInt() == -1 ?  null : in.readInt());
		day = (in.readInt() == -1 ?  null : in.readInt());
		source = in.readString();
		title = in.readString();
		revision = in.readString();
		
		identifiers = new HashMap<String, String>();
		int size = in.readInt();
		for(int i = 0; i < size; i++) {
		    String key = in.readString();
		    String value = in.readString();
		    identifiers.put(key,value);
	    }
		
		abstractString = in.readString();
		
		authors = new ArrayList<Person>();
		size = in.readInt();
		for(int i = 0; i < size; i++) {
		    String foreName = in.readString();
		    String surname = in.readString();
		    Person person = new Person(foreName, surname);
		    authors.add(person);
	    }
		
		added = in.readString();
		pages = in.readString();
		volume = in.readString();
		issue = in.readString();
		website = in.readString();
		city = in.readString();
		publisher = in.readString();
		edition = in.readString();
		institution = in.readString();
		series = in.readString();
		chapter = in.readString();
		
		editors = new ArrayList<Person>();
		size = in.readInt();
		for(int i = 0; i < size; i++) {
		    String foreName = in.readString();
		    String surname = in.readString();
		    Person person = new Person(foreName, surname);
		    editors.add(person);
	    }
		
		downloaded = in.readByte() != 0; 
	} 
}
