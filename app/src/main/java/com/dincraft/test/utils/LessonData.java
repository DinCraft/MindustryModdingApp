package com.dincraft.test.utils;

public class LessonData {
    private String name;
    private String relativeLink;

    public LessonData(String name, String relativeLink){
        this.name = name;
        this.relativeLink = relativeLink;
    }

    public String getName() {
        return name;
    }

    public String getRelativeLink() {
        return relativeLink;
    }
}
