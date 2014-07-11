package com.mendeley.api.params;

public class Page {
	
	public String link;

	@Override
	public String toString() {
		return "link: " + link;
	}

    public static boolean isValidPage(Page page) {
        return page != null && page.link != null && page.link.length() > 0;
    }
	
}
