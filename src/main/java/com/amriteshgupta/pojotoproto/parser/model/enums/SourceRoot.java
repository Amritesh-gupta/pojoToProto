package com.amriteshgupta.pojotoproto.parser.model.enums;

public enum SourceRoot {
    JAVA("src/main/java/"),
    PROTO("src/main/proto/"),
    RESOURCES("src/main/resources/"),
    SRC("src/");

    private final String path;

    SourceRoot(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}