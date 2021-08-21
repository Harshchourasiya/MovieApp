package com.harshchourasiya.movies.Model;

public class Movie {
    String title,id,poster,rate;
    public Movie(String title, String id, String poster, String rate){
        this.title = title;
        this.id=id;
        this.poster=poster;
        this.rate=rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
