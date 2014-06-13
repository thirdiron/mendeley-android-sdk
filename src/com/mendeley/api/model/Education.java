package com.mendeley.api.model;

/**
 * Model class representing education json object.
 */
public class Education {
	
	public final Integer id;
	public final String lastModified;
	public final String created;
	public final String degree;
	public final String institution;
	public final String startDate;
	public final String endDate;
	public final String website;
	
	private Education(
			Integer id,
			String lastModified,
			String created,
			String degree,
			String institution,
			String startDate,
			String endDate,
			String website) {
		this.id = id;
		this.lastModified = lastModified;
		this.created = created;
		this.degree = degree;
		this.institution = institution;
		this.startDate = startDate;
		this.endDate = endDate;
		this.website = website;
	}
	
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
	
	public static class Builder {
		private Integer id;
		private String lastModified;
		private String created;
		private String degree;
		private String institution;
		private String startDate;
		private String endDate;
		private String website;
		
		public Builder() {}
		
		public Builder(Builder from) {
			this.id = from.id;
			this.lastModified = from.lastModified;
			this.created = from.created;
			this.degree = from.degree;
			this.institution = from.institution;
			this.startDate = from.startDate;
			this.endDate = from.endDate;
			this.website = from.website;
		}
		
		public Builder setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Builder setLastModified(String lastModified) {
			this.lastModified = lastModified;
			return this;
		}
		
		public Builder setCreated(String created) {
			this.created = created;
			return this;
		}
		
		public Builder setDegree(String degree) {
			this.degree = degree;
			return this;
		}
		
		public Builder setInstitution(String institution) {
			this.institution = institution;
			return this;
		}
		
		public Builder setStartDate(String startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public Builder setEndDate(String endDate) {
			this.endDate = endDate;
			return this;
		}
		
		public Builder setWebsite(String website) {
			this.website = website;
			return this;
		}
		
		public Education build() {
			return new Education(
					id,
					lastModified,
					created,
					degree,
					institution,
					startDate,
					endDate,
					website);
		}
	}
}
