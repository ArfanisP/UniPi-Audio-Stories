package com.example.unipiaudiostories.model;

//Μοντέλο δεδομένων για μία ιστορία
public class Story {
    //Πεδία
    private String id;
    private String title;
    private String author;
    private long year;
    private String text;
    private String imageUrl;

    //Κενός constructor για μετατροπή από Firestore σε αντικείμενο
    public Story() {}

    //Getters και Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getYear() { return year; }
    public void setYear(long year) { this.year = year; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}