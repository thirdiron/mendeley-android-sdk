package com.mendeley.api.model;

import com.mendeley.api.util.Nullable;
import com.mendeley.api.util.NullableList;
import com.mendeley.api.util.NullableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class representing document json object.
 *
 */
public class Document {
		
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
	public final NullableMap<String, String> identifiers;
	public final String abstractString;
	public final NullableList<Person> authors;
	public final String pages;
	public final String volume;
	public final String issue;
	public final String publisher;
	public final String city;
	public final String edition;
	public final String institution;
	public final String series;
	public final String chapter;
	public final NullableList<Person> editors;
    public final NullableList<String> tags;
    public final String accessed;
    public final Boolean fileAttached;
    public final NullableList<String> keywords;
    public final NullableList<String> websites;
    public final String clientData;
    public final String uniqueId;
	
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
            List<Person> authors,
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
            List<Person> editors,
            List<String> tags,
            String accessed,
            Boolean fileAttached,
            List<String> keywords,
            List<String> websites,
            String clientData,
            String uniqueId) {
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
		this.identifiers = new NullableMap<String, String>(identifiers);
		this.abstractString = abstractString;
		this.authors = new NullableList<Person>(authors);
		this.pages = pages;
		this.volume = volume;
		this.issue = issue;
		this.publisher = publisher;
		this.city = city;
		this.edition = edition;
		this.institution = institution;
		this.series = series;
		this.chapter = chapter;
		this.editors = new NullableList<Person>(editors);
        this.tags = new NullableList<String>(tags);
        this.accessed = accessed;
        this.fileAttached = fileAttached;
        this.keywords = new NullableList<String>(keywords);
        this.websites = new NullableList<String>(websites);
        this.clientData = clientData;
        this.uniqueId = uniqueId;
	}

	public static class Builder {
        private String title;
        private String type;

		private String lastModified;
		private String groupId;
		private String profileId;
		private Boolean read;
		private Boolean starred;
		private Boolean authored;
		private Boolean confirmed;
		private Boolean hidden;
		private String id;
		private Integer month;
		private Integer year;
		private Integer day;	
		private String source;
		private String revision;
		private String created;
		private Map<String, String> identifiers;
		private String abstractString;
		private List<Person> authors;
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
		private List<Person> editors;
        private List<String> tags;
        private String accessed;
        private Boolean fileAttached;
        private List<String> keywords;
        private List<String> websites;
        private String clientData;
        private String uniqueId;
		
		public Builder(String title, String type) {
            this.title = title;
            this.type = type;
        }
		
		public Builder(Document from) {
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
			this.publisher = from.publisher;
			this.city = from.city;
			this.edition = from.edition;
			this.institution = from.institution;
			this.series = from.series;
			this.chapter = from.chapter;
			this.editors =  from.editors==null?new ArrayList<Person>():from.editors;
            this.tags = from.tags==null?new ArrayList<String>():from.tags;
            this.accessed = from.accessed;
            this.fileAttached = from.fileAttached;
            this.keywords = from.keywords==null?new ArrayList<String>():from.keywords;
            this.websites = from.websites==null?new ArrayList<String>():from.websites;
            this.clientData = from.clientData;
            this.uniqueId = from.uniqueId;
		}

        public Builder setTitle(String title) {
            this.title = title;
            return this;
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

		public Builder setAuthors(List<Person> authors) {
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

        public Builder setTags(ArrayList<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setAccessed(String accessed) {
            this.accessed = accessed;
            return this;
        }

        public Builder setFileAttached(Boolean fileAttached) {
            this.fileAttached = fileAttached;
            return this;
        }

        public Builder setKeywords(ArrayList<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder setWebsites(ArrayList<String> websites) {
            this.websites = websites;
            return this;
        }

        public Builder setClientData(String clientData) {
            this.clientData = clientData;
            return this;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
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
					editors,
                    tags,
                    accessed,
                    fileAttached,
                    keywords,
                    websites,
                    clientData,
                    uniqueId);
		}
	}

    public static Builder getBuilder(String title, String type) {
        return new Builder(title, type);
    }

    public String getAuthorsString() {
		String authorsString = "";
		for (int i = 0; i < authors.size() && i < 2; i++) {
			if (i > 0) {
				authorsString +=", ";				
			}
			authorsString += authors.get(i).lastName;
			if (authors.get(i).firstName.length() > 0) {
				authorsString += " " + authors.get(i).firstName.substring(0, 1);
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
}
