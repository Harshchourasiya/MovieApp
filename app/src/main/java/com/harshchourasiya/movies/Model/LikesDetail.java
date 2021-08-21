package com.harshchourasiya.movies.Model;

public class LikesDetail {
    String title;
    String poster;
    String rate;
    String overview;
    String id;

    public LikesDetail(String title, String poster, String rate, String overview, String id){
        this.title = title;
        this.poster = poster;
        this.rate = rate;
        this.overview = overview;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
