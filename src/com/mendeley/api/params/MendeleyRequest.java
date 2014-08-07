package com.mendeley.api.params;

/**
 * Superclass of RequestParameters objects
 *
 */
public class MendeleyRequest {
	/**
	 * Available document views. The view specifies which fields are returned for document objects.
	 */
	public static enum View {
		/**
		 * bib
		 */
		BIB("bib"),
		/**
		 * client
		 */
		CLIENT("client"),
		/**
		 * all
		 */
		ALL("all");
		
		private final String value;		
		View(String value) {
			this.value = value;
		}		
		public String getValue() {
			return value;
		}		
		@Override 
		public String toString() {
			return value;
		}
	}
	
	/**
	 * Available sort orders.
	 */
	public static enum Order {
		/**
		 * Ascending order.
		 */
		ASC("asc"),
		/**
		 * Descending order.
		 */
		DESC("desc");
		
		private final String value;		
		Order(String value) {
			this.value = value;
		}		
		public String getValue() {
			return value;
		}		
		@Override 
		public String toString() {
			return value;
		}
	}
	
	/**
	 * Available fields to sort lists by.
	 */
	public static enum Sort {
		/**
		 * modified.
		 */
		MODIFIED("last_modified"),
		/**
		 * added.
		 */
		ADDED("created"),
		/**
		 * title.
		 */
		TITLE("title");
		
		private final String value;		
		Sort(String value) {
			this.value = value;
		}		
		public String getValue() {
			return value;
		}		
		@Override 
		public String toString() {
			return value;
		}
	}
}
