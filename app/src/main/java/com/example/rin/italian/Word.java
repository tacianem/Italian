package com.example.rin.italian;

/**
 * Created by rin on 21/09/17.
 */

public class Word {


    private String english;
    private String italian;
    private int imageResourceId = NO_IMAGE;
    private static final int NO_IMAGE = -1;
    private int audioId;


    public Word(String newEnglish, String newItalian, int audioId) {
        english = newEnglish;
        italian = newItalian;
        this.audioId = audioId;
    }

    public Word(String newEnglish, String newItalian, int imageResourceId, int audioId) {
        english = newEnglish;
        italian = newItalian;
        this.imageResourceId = imageResourceId;
        this.audioId = audioId;
    }

    public String getEnglishTranslation() {
        return english;
    }

    public String getItalianTranslation() {
        return italian;
    }

    public int getImageResourceId() { return imageResourceId; }

    public boolean hasImage(){
        return imageResourceId != NO_IMAGE;
    }

    public int getAudioId () { return audioId; }

}
