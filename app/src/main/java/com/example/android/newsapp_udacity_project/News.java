package com.example.android.newsapp_udacity_project;

public class News {
    private String title;
    private String date;
    private String category;
    private String url;
    private String author;

    public News(String title, String date, String category, String url, String author) {
        this.title = title;
        this.date = date;
        this.category = category;
        this.url = url;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }
}
