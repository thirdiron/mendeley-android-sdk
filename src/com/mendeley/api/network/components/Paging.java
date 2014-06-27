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

    public static boolean isValidPage(Paging paging) {
        return paging != null && paging.linkNext != null && paging.linkNext.length() > 0;
    }
	
}
