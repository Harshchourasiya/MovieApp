package com.harshchourasiya.movies.Model;

public class Users {
    public String password;
    public String email;
    public String verify;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Users(String email, String password, String verify) {
        this.password = password;
        this.email = email;
        this.verify = verify;
    }

}
