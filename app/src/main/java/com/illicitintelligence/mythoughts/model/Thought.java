package com.illicitintelligence.mythoughts.model;

public class Thought {
    private String sharedBy;
    private String sharedImage;
    private String sharedThought;

    public Thought() {
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(String sharedBy) {
        this.sharedBy = sharedBy;
    }

    public String getSharedImage() {
        return sharedImage;
    }

    public void setSharedImage(String sharedImage) {
        this.sharedImage = sharedImage;
    }

    public String getSharedThought() {
        return sharedThought;
    }

    public void setSharedThought(String sharedThought) {
        this.sharedThought = sharedThought;
    }
}
