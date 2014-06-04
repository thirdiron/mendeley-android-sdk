package com.mendeley.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class representing person json object.
 */
public class Person implements Parcelable {

	public String forename;
	public String surname;
	
	public Person(String forename, String surname) {
		this.forename = forename;
		this.surname = surname;
	}
	
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
	    public Person createFromParcel(Parcel in) {
	    	return new Person(in);
	    }

	    public Person[] newArray(int size) {
	    	return new Person[size];
	    }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(forename);
		dest.writeString(surname);
	}
	
	private Person(Parcel in) {
		forename = in.readString();
		surname = in.readString();
	}

}
