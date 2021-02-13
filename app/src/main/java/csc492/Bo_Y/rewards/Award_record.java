package csc492.Bo_Y.rewards;

import android.content.SharedPreferences;

public class Award_record {
    private String date;
    private String name;
    private int points;
    private String comment;
    public Award_record(String date,String name,int points,String comment){
        this.date = date;
        this.name = name;
        this.points= points;
        this.comment = comment;
    }

    public String getDate(){
        return this.date;
    }
    public String getName(){
        return this.name;
    }
    public int getPoints(){return this.points;}
    public String getComment(){return this.comment;}

}
