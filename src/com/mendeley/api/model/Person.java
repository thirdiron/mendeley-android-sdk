package com.mendeley.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class representing person json object.
 */
public class Person implements Parcelable {

	public String firstName;
	public String lastName;
	
	public Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
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
		dest.writeString(firstName);
		dest.writeString(lastName);
	}
	
	private Person(Parcel in) {
		firstName = in.readString();
		lastName = in.readString();
	}

}
