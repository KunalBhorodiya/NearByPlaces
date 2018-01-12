package com.example.kunal.food.Profile;

import java.util.ArrayList;

/**
 * Created by Kunal on 10/14/2017.
 */
public class PlacesPOJO {

    String place_name, place_address, place_rating, latitide, logitude;
    ArrayList<String> type;

    public PlacesPOJO(String place_name, String place_address, String place_rating, String latitide, String logitude, ArrayList<String> type) {
        this.place_name = place_name;
        this.place_address = place_address;
        this.place_rating = place_rating;
        this.latitide = latitide;
        this.logitude = logitude;
        this.type = type;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getPlace_address() {
        return place_address;
    }

    public String getPlace_rating() {
        return place_rating;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public String getLatitide() {
        return latitide;
    }

    public String getLogitude() {
        return logitude;
    }
}
