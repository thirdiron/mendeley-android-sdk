package com.mendeley.api.model;

/**
 * Model class representing photo json object.
 *
 */
public class Photo {

	public String standard;
	public String square;
    public String original;
	
	public Photo() {}
	
	public Photo(String standard, String square) {
		this.standard = standard;
		this.square = square;
	}
	
	@Override
	public String toString() {
		return "standard: " + standard + 
				", square: " + square; 
	}
}
