package com.harshchourasiya.movies.Data;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.harshchourasiya.movies.Error.NoInternet;

public class Data {

    // Log Test Keys

    public static final String KEY = "Error";

    // Shared Data Key
    public static final String ISFIRSTTIMEKEY = "isFirstTime";
    public static final String EMAILKEY = "email";
    public static final String USERNAME = "User@";
    public static final String USERKEY = "Userkey";
    public static final String ISPAID = "IsPaid";
    public static final String ISDARK = "IsDark";

    // Database Key
    public static final String USERS = "Users";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String VERIFY = "verify";
    public static final String NO_PASSWORD = "NoPassword";
    public static final String LIKE = "likes";


    // Keys
    public static final int RC_SIGN_IN = 12;

    // API Keys Url
    // User Your Own API KEY
    // Here is the API of Key You can get API http://themoviedb.org/ to Run It
    public static final String API_KEY = "YOUR_API_KEY";

    public static final String IMAGE_W200 = "https://image.tmdb.org/t/p/w200";
    public static final String IMAGE_W500 = "https://image.tmdb.org/t/p/w500";
    public static final String GENRE_URL = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY + "&language=en-US";
    public static final String DAY_TREADING_URL = "https://api.themoviedb.org/3/trending/all/day?api_key=" + API_KEY + "&&page=";
    public static final String SPECIFIC_GENRE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY +
            "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&with_genres=";
    public static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=" +
            API_KEY + "&language=en-US&query=";
    public static final String GET_SIMILAR = "https://api.themoviedb.org/3/movie/";
    public static final String GET_DETAILS = "https://api.themoviedb.org/3/movie/";
    public static final String GET_API = "?api_key=" + API_KEY + "&language=en-US";
    public static final String GET_SIMILAR_LAST = "/similar" + GET_API + "&language=en-US&page=1";
    public static final String PAGE_URL = "&page=";

    // API Key Names

    public static final String TITLE = "original_title";
    public static final String ID = "id";
    public static final String RATE = "vote_average";
    public static final String POSTER = "poster_path";
    public static final String NAME = "name";
    public static final String BACKGROUND_IMAGE = "backdrop_path";
    public static final String TIME = "runtime";
    public static final String TAGLINE = "tagline";
    public static final String OVERVIEW = "overview";

    // App name and descriptions for share

    public static final String SHARE_BODY = "Best App to search your next watch movie" +
            " Download Movies App from below Link \n" +
            "https://play.google.com/store/apps/details?id=";
    public static final String SHARE_TITLE = "Movie Lover";


    // Remove Ad ID for make purchase

    public static final String REMOVE_AD_ID = "api.remove";


    // Social Media pages Links

    public static final String FACEBOOK_PAGE_URL = "";
    public static final String INSTAGRAM_PAGE_URL = "";
    public static final String TWITTER_PAGE_URL = "";


    // Intent Keys

    public static final String BACK = "Back";

    // Splash Screen timeout

    public static final long SPLASH_SCREEN_TIME_OUT = 1500;

    // Subscribe Topic name for send push notification

    public static final String TOPIC = "notification";

    // Is User Open App for first Time

    public static boolean isFirstTime(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getBoolean(ISFIRSTTIMEKEY, true);
    }

    // Get User Email

    public static String getEmail(Context context) {
        // In User@, @ is must at last to find username from email
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(EMAILKEY, USERNAME);

    }

    // Get firebase Key

    public static String getUserKey(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(USERKEY, "null");
    }

    // Is User Purchase app or not

    public static boolean isPaid(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getBoolean(ISPAID, false);
    }


    // Get If your Select Dark Mode or Not

    public static boolean isDark(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getBoolean(ISDARK, false);
    }

    // Check Internet

    public static boolean CheckInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isAvailable() &&
                manager.getActiveNetworkInfo().isConnected());
    }


    // Check internet and open activity

    public static void toCheckInternet(Context context) {
        if (!CheckInternet(context)) {
            Intent intent = new Intent(context.getApplicationContext(), NoInternet.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        }
    }

}
