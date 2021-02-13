package csc492.Bo_Y.rewards;

import org.json.JSONObject;

public class Leader {

    private String name;
    private String story;
    private String position;
    private String department;
    private int points;
    private String imageString;
    private JSONObject jsonObject;
    public Leader( String name, int points, String imageString, String story, String position, String department, JSONObject jsonObject){

        this.name = name;
        this.points= points;
        this.imageString = imageString;
        this.story =story;
        this.position = position;
        this.department = department;
        this.jsonObject = jsonObject;
    }


    public String getName(){
        return this.name;
    }
    public int getPoints(){return this.points;}
    public String getImageString(){return this.imageString;}
    public String getStory(){return this.story;}
    public String getPosition(){return this.position;}
    public String getDepartment(){return this.department;}
    public JSONObject getJsonObject(){return  this.jsonObject;}
}
