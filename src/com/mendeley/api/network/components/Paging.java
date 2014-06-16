package com.mendeley.api.network.components;

public class Paging {
	
	public Integer mendeleyCount;
	public String linkNext;
	public String linkLast;

	@Override
	public String toString() {
		return "linkNext: "+linkNext+
   			   ", linkLast: "+linkLast+
   			   ", mendeleyCount: " +mendeleyCount;
	}
	
}
