package com.mendeley.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mendeley.api.model.Education.Builder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class representing document json object.
 *
 */
public class Document implements Parcelable {
		
	public final String lastModified;
	public final String groupId;
	public final String profileId;
	public final Boolean read;
	public final Boolean starred;
	public final Boolean authored;
	public final Boolean confirmed;
	public final Boolean hidden;
	public final String id;
	public final String type;
	public final Integer month;
	public final Integer year;
	public final Integer day;	
	public final String source;
	public final String title;
	public final String revision;
	public final String created;
	public final Map<String, String> identifiers;
	public final String abstractString;
	public final ArrayList<Person> authors;
	public final String pages;
	public final String volume;
	public final String issue;
	public final String website;
	public final String publisher;
	public final String city;
	public final String edition;
	public final String institution;
	public final String series;
	public final String chapter;
	public final ArrayList<Person> editors;
	
	final static String ET_EL = "et. al";
		
	private Document(
			String lastModified,
			String groupId,
			String profileId,
			Boolean read,
			Boolean starred,
			Boolean authored,
			Boolean confirmed,
			Boolean hidden,
			String id,
			String type,
			Integer month,
			Integer year,
			Integer day,	
			String source,
			String title,
			String revision,
			String created,
			Map<String, String> identifiers,
			String abstractString,
			ArrayList<Person> authors,
			String pages,
			String volume,
			String issue,
			String website,
			String publisher,
			String city,
			String edition,
			String institution,
			String series,
			String chapter,
			ArrayList<Person> editors) {
		this.lastModified = lastModified;
		this.groupId = groupId;
		this.profileId = profileId;
		this.read = read;
		this.starred = starred;
		this.authored = authored;
		this.confirmed = confirmed;
		this.hidden = hidden;
		this.id = id;
		this.type = type;
		this.month = month;
		this.year = year;
		this.day = day;
		this.source = source;
		this.title = title;
		this.revision = revision;
		this.created = created;
		this.identifiers = identifiers==null?new HashMap<String, String>():identifiers;;
		this.abstractString = abstractString;
		this.authors = authors==null?new ArrayList<Person>():authors;
		this.pages = pages;
		this.volume = volume;
		this.issue = issue;
		this.website = website;
		this.publisher = publisher;
		this.city = city;
		this.edition = edition;
		this.institution = institution;
		this.series = series;
		this.chapter = chapter;
		this.editors = editors==null?new ArrayList<Person>():editors;
	}
	
	public static class Builder {
		private String lastModified;
		private String groupId;
		private String profileId;
		private Boolean read;
		private Boolean starred;
		private Boolean authored;
		private Boolean confirmed;
		private Boolean hidden;
		private String id;
		private String type;
		private Integer month;
		private Integer year;
		private Integer day;	
		private String source;
		private String title;
		private String revision;
		private String created;
		private Map<String, String> identifiers;
		private String abstractString;
		private ArrayList<Person> authors;
		private String pages;
		private String volume;
		private String issue;
		private String website;
		private String publisher;
		private String city;
		private String edition;
		private String institution;
		private String series;
		private String chapter;
		private ArrayList<Person> editors;
		
		public Builder() {}
		
		public Builder(Builder from) {
			this.lastModified = from.lastModified;
			this.groupId = from.groupId;
			this.profileId = from.profileId;
			this.read = from.read;
			this.starred = from.starred;
			this.authored = from.authored;
			this.confirmed = from.confirmed;
			this.hidden = from.hidden;
			this.id = from.id;
			this.type = from.type;
			this.month = from.month;
			this.year = from.year;
			this.day = from.day;
			this.source = from.source;
			this.title = from.title;
			this.revision = from.revision;
			this.created = from.created;
			this.identifiers = from.identifiers==null?new HashMap<String, String>():from.identifiers;
			this.abstractString = from.abstractString;
			this.authors = from.authors==null?new ArrayList<Person>():from.authors;
			this.pages = from.pages;
			this.volume = from.volume;
			this.issue = from.issue;
			this.website = from.website;
			this.publisher = from.publisher;
			this.city = from.city;
			this.edition = from.edition;
			this.institution = from.institution;
			this.series = from.series;
			this.chapter = from.chapter;
			this.editors =  from.editors==null?new ArrayList<Person>():from.editors;
		}
		
		public Builder setLastModified(String lastModified) {
			this.lastModified = lastModified;
			return this;
		}
		
		public Builder setGroupId(String groupId) {
			this.groupId = groupId;
			return this;
		}
		
		public Builder setProfileId(String profileId) {
			this.profileId = profileId;
			return this;
		}
		
		public Builder setRead(Boolean read) {
			this.read = read;
			return this;
		}
		
		public Builder setStarred(Boolean starred) {
			this.starred = starred;
			return this;
		}
		
		public Builder setAuthored(Boolean authored) {
			this.authored = authored;
			return this;
		}
		
		public Builder setConfirmed(Boolean confirmed) {
			this.confirmed = confirmed;
			return this;
		}
		
		public Builder setHidden(Boolean hidden) {
			this.hidden = hidden;
			return this;
		}
		
		public Builder setId(String id) {
			this.id = id;
			return this;
		}
		
		public Builder setType(String type) {
			this.type = type;
			return this;
		}
		
		public Builder setMonth(Integer month) {
			this.month = month;
			return this;
		}
		
		public Builder setYear(Integer year) {
			this.year = year;
			return this;
		}
		
		public Builder setDay(Integer day) {
			this.day = day;
			return this;
		}
		
		public Builder setSource(String source) {
			this.source = source;
			return this;
		}
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		public Builder setRevision(String revision) {
			this.revision = revision;
			return this;
		}

		public Builder setIdentifiers(HashMap<String, String> identifiers) {
			this.identifiers = identifiers;
			return this;
		}
		
		public Builder setAbstractString(String abstractString) {
			this.abstractString = abstractString;
			return this;
		}

		public Builder setAuthors(ArrayList<Person> authors) {
			this.authors = authors;
			return this;
		}
		
		public Builder setPages(String pages) {
			this.pages = pages;
			return this;
		}
		
		public Builder setVolume(String volume) {
			this.volume = volume;
			return this;
		}
		
		public Builder setIssue(String issue) {
			this.issue = issue;
			return this;
		}
		
		public Builder setWebsite(String website) {
			this.website = website;
			return this;
		}
		
		public Builder setPublisher(String publisher) {
			this.publisher = publisher;
			return this;
		}
		
		public Builder setCity(String city) {
			this.city = city;
			return this;
		}
		
		public Builder setEdition(String edition) {
			this.edition = edition;
			return this;
		}
		
		public Builder setCreated(String created) {
			this.created = created;
			return this;
		}
		
		public Builder setInstitution(String institution) {
			this.institution = institution;
			return this;
		}
		
		public Builder setSeries(String series) {
			this.series = series;
			return this;
		}
		
		public Builder setChapter(String chapter) {
			this.chapter = chapter;
			return this;
		}

		public Builder setEditors(ArrayList<Person> editors) {
			this.editors = editors;
			return this;
		}

		
		public Document build() {
			return new Document(
					lastModified,
					groupId,
					profileId,
					read,
					starred,
					authored,
					confirmed,
					hidden,
					id,
					type,
					month,
					year,
					day,	
					source,
					title,
					revision,
					created,
					identifiers,
					abstractString,
					authors,
					pages,
					volume,
					issue,
					website,
					publisher,
					city,
					edition,
					institution,
					series,
					chapter,
					editors);
		}
	}
	
	public String getAuthorsString() {
		String authorsString = "";
		for (int i = 0; i < authors.size() && i < 2; i++) {
			if (i > 0) {
				authorsString +=", ";				
			}
			authorsString += authors.get(i).surname;
			if (authors.get(i).forename.length() > 0) {
				authorsString += " " + authors.get(i).forename.substring(0, 1);
			}
		}
		
		if (authors.size() > 2) {
			authorsString += " " + ET_EL;
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
				", created: " + created +
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
	    
		dest.writeString(created);
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
		
		created = in.readString();
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
	} 
}
