package com.mendeley.api.model;

import java.util.ArrayList;

/**
 * Model class representing Group json object.
 *
 */
public class Group {

    /** Different access levels for a group */
    public static enum AccessLevel {
        PUBLIC("public"),
        PRIVATE("private"),
        INVITE_ONLY("invite_only");

        private final String stringValue;

        private AccessLevel(String stringValue) {
            this.stringValue = stringValue;
        }

        public static AccessLevel fromValue(String accessLevelStr) {
            for(AccessLevel level : AccessLevel.values()) {
                if (level.toValue().equals(accessLevelStr)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Invalid access level: " + accessLevelStr);
        }

        public String toValue() {
            return stringValue;
        }
    }

    public final String id;
    public final String created;
    public final String owningProfileId;
    public final String link;
    public final String role;
    public final AccessLevel accessLevel;
    public final String name;
    public final String description;
    public final ArrayList<String> tags;
    public final String webpage;
    public final ArrayList<String> disciplines;
    public final Photo photo;

    private Group(
            String id,
            String created,
            String owningProfileId,
            String link,
            String role,
            AccessLevel accessLevel,
            String name,
            String description,
            ArrayList<String> tags,
            String webpage,
            ArrayList<String> disciplines,
            Photo photo) {
        this.id = id;
        this.created = created;
        this.owningProfileId = owningProfileId;
        this.link = link;
        this.role = role;
        this.accessLevel = accessLevel;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.webpage = webpage;
        this.disciplines = disciplines;
        this.photo = photo;
    }

    public static class Builder {
            private String id;
            private String created;
            private String owningProfileId;
            private String link;
            private String role;
            private AccessLevel accessLevel;
            private String name;
            private String description;
            private ArrayList<String> tags;
            private String webpage;
            private ArrayList<String> disciplines;
            private Photo photo;

        public Builder() {}

        public Builder(Group from) {
            this.id = from.id;
            this.created = from.created;
            this.owningProfileId = from.owningProfileId;
            this.link = from.link;
            this.role = from.role;
            this.accessLevel = from.accessLevel;
            this.name = from.name;
            this.description = from.description;
            this.tags = from.tags==null?new ArrayList<String>():from.tags;
            this.webpage = from.webpage;
            this.disciplines = from.disciplines==null?new ArrayList<String>():from.disciplines;
            this.photo = from.photo;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setCreated(String created) {
            this.created = created;
            return this;
        }

        public Builder setOwningProfileId(String owingProfileId) {
            this.owningProfileId = owingProfileId;
            return this;
        }

        public Builder setLink(String link) {
            this.link = link;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setAccessLevel(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTags(ArrayList<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setWebpage(String webpage) {
            this.webpage = webpage;
            return this;
        }

        public Builder setDisciplines(ArrayList<String> disciplines) {
            this.disciplines = disciplines;
            return this;
        }

        public Builder setPhoto(Photo photo) {
            this.photo = photo;
            return this;
        }

        public Group build() {
            return new Group(
                    id,
                    created,
                    owningProfileId,
                    link,
                    role,
                    accessLevel,
                    name,
                    description,
                    tags,
                    webpage,
                    disciplines,
                    photo);
        }
    }
}
