package com.mendeley.api.params;

/**
 * Available fields to sort lists by.
 */
public enum Sort {
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
