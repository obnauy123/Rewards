package csc492.Bo_Y.rewards;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class DeleteRunnable implements Runnable {


    private final login_profile mainActivity;
    private  String userName, api;

    DeleteRunnable(login_profile mainActivity, String userName, String api) {
        this.mainActivity = mainActivity;
        this.api = api;
        if(userName != null && userName.length()>0){
            this.userName = userName.substring(1,userName.length()-1);
        }

    }

    @Override
    public void run() {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/DeleteProfile?userName="+userName;

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey",api);
            connection.connect();


            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("Delete", "success");
                mainActivity.exit();

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                Log.d("Delete", "f");
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

            }

            return;
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
