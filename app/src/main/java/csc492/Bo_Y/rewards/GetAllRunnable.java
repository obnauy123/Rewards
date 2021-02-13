package csc492.Bo_Y.rewards;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetAllRunnable implements Runnable {
    String api;
    leaderboard parentact;
    ArrayList<Leader> list;
    GetAllRunnable(String api, leaderboard parentact, ArrayList<Leader> list){
        this.api = api;
        this.parentact = parentact;
        this.list = list;
    }

    @Override
    public void run(){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String urlString = "http://christopherhield.org/api/Profile/GetAllProfiles";
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", api);

            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("getall", "success");
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

            }
            Log.d("getall", "run:");
            parentact.loadall(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getall", "run: catch err" + e.getMessage() );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("getall", "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
    }

}
