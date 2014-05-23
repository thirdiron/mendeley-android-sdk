package com.mendeley.api.network.components;

/**
 * Superclass of RequestParameters objects
 * 
 * @author Elad
 *
 */
public class MendeleyRequest {

	/**
	 * Available document views 
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
	 * Available order types;
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
	 * Available sort types;
	 */
	public static enum Sort {
		/**
		 * modified.
		 */
		MODIFIED("modified"),
		/**
		 * added.
		 */
		ADDED("added"),
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
