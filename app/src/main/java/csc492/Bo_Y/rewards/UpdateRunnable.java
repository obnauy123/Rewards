package csc492.Bo_Y.rewards;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateRunnable implements Runnable {
    private String api,firstName,lastName,username,department,story,position,password,location, imageString;
    private int points_ava;
    private Edit_profile activity;
    UpdateRunnable(Edit_profile activity, String api, String firstName, String lastName, String username, String department, String story, String position, String password, int points_ava, String location, String imageString){
        this.activity = activity;
        this.api = api;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.points_ava = points_ava;
        this.location = location;
        this.imageString = imageString;
    }

    @Override
    public void run(){
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/UpdateProfile?firstName=" + firstName + "&lastName=" +lastName +"&userName=" + username +"&department=" + department + "&story=" + story + "&position=" + position + "&password=" + password + "&remainingPointsToAward="+points_ava+"&location="+location;

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", api);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageString);
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("updated", "run: ");
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                Log.d("updated", "fail");
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }

            activity.showProfile(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }

    }

}
