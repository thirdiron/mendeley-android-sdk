package com.mendeley.api.model;

/**
 * Model class representing photo json object.
 *
 */
public class Photo {

	public final String photoUrl;
    public byte[] photoBytes;

	
	public Photo(String photoUrl) {
        this.photoUrl = photoUrl;
    }

	@Override
	public String toString() {
		return "photoUrl: " + photoUrl;
	}
}
