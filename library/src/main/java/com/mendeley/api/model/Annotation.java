package com.mendeley.api.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Annotation {
    public enum Type {
        STICKY_NOTE("sticky_note"), HIGHLIGHT("highlight"), DOCUMENT_NOTE("note");

        public final String name;

        public static Type fromName(String name) {
            for (Type type : values()) {
                if (type.name.equals(name))
                    return type;
            }
            return null;
        }
        Type(String name) {
            this.name = name;
        }
    }

    public enum PrivacyLevel {
        PRIVATE("private"), GROUP("group"), PUBLIC("public");

        public final String name;

        public static PrivacyLevel fromName(String name) {
            for (PrivacyLevel level : values()) {
                if (level.name.equals(name))
                    return level;
            }
            return null;
        }

        PrivacyLevel(String name) {
            this.name = name;
        }
    }

    public final String id;
    public final Type type;
    public final String previousId;
    public final int color;
    public final String text;
    public final String profileId;
    public final List<Box> positions;
    public final String created;
    public final String lastModified;
    public final PrivacyLevel privacyLevel;
    public final String fileHash;
    public final String documentId;

    private Annotation(
            String id,
            Type type,
            String previousId,
            int color,
            String text,
            String profileId,
            List<Box> positions,
            String created,
            String lastModified,
            PrivacyLevel privacyLevel,
            String fileHash,
            String documentId) {
        this.id = id;
        this.type = type;
        this.previousId = previousId;
        this.color = color;
        this.text = text;
        this.profileId = profileId;
        this.positions = positions;
        this.created = created;
        this.lastModified = lastModified;
        this.privacyLevel = privacyLevel;
        this.fileHash = fileHash;
        this.documentId = documentId;
    }

    public static class Builder {
        private String id;
        private Type type;
        private String previousId;
        private int color;
        private String text;
        private String profileId;
        private List<Box> positions;
        private String created;
        private String lastModified;
        private PrivacyLevel privacyLevel;
        private String fileHash;
        private String documentId;

        public Builder() {
            // Reasonable defaults:
            this.privacyLevel = PrivacyLevel.PRIVATE;
            this.positions = new ArrayList<>();
            this.color = Color.BLACK;
        }

        public Builder(Annotation from) {
            this.id = from.id;
            this.type = from.type;
            this.previousId = from.previousId;
            this.color = from.color;
            this.text = from.text;
            this.profileId = from.profileId;
            this.positions = from.positions;
            this.created = from.created;
            this.lastModified = from.lastModified;
            this.privacyLevel = from.privacyLevel;
            this.fileHash = from.fileHash;
            this.documentId = from.documentId;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setPreviousId(String previousId) {
            this.previousId = previousId;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setProfileId(String profileId) {
            this.profileId = profileId;
            return this;
        }

        public Builder setPositions(List<Box> positions) {
            this.positions = positions;
            return this;
        }

        public Builder setCreated(String created) {
            this.created = created;
            return this;
        }

        public Builder setLastModified(String lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder setPrivacyLevel(PrivacyLevel privacyLevel) {
            this.privacyLevel = privacyLevel;
            return this;
        }

        public Builder setFileHash(String fileHash) {
            this.fileHash = fileHash;
            return this;
        }

        public Builder setDocumentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public Annotation build() {
            return new Annotation(
                    id,
                    type,
                    previousId,
                    color,
                    text,
                    profileId,
                    positions,
                    created,
                    lastModified,
                    privacyLevel,
                    fileHash,
                    documentId);
        }
    }
}
