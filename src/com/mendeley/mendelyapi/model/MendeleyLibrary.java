package com.mendeley.mendelyapi.model;

import java.util.ArrayList;

public class MendeleyLibrary {

	public static final int MAX_ITEMS_PER_PAGE = 2;
	
	public ArrayList<String> documentIds;
	public int totalResults;
	public int totalPages;
	public int currentPage;
	public int itemsPerPage;
	
	public MendeleyLibrary() {
		currentPage = 0;
		itemsPerPage = MAX_ITEMS_PER_PAGE;
		documentIds = new ArrayList<String>();
	}
	
	public MendeleyLibrary(int totalResults, int totalPages, int currentPage, int itemsPerPage, ArrayList<String> documentIds) {
		this.totalResults = totalResults;
		this.totalPages = totalPages;
		this.currentPage = currentPage;
		this.itemsPerPage = itemsPerPage;
		this.documentIds = documentIds;
	}
	
	@Override
	public String toString() {
		return "totalResults: "+totalResults + 
			   ", totalPages: "+totalPages + 
			   ", currentPage: "+currentPage + 
			   ", itemsPerPage: "+itemsPerPage + 
			   ", documentIds: "+documentIds.size(); 
	}
	
}
