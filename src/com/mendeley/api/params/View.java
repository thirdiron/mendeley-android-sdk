package com.mendeley.api.params;

/**
 * Extended document views. The view specifies which additional fields are returned for document objects.
 * All views return core fields.
 */
public enum View {
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
