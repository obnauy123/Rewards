package csc492.Bo_Y.rewards;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginRunnable implements Runnable {
    private String username;
    private String password;
    private String api;
    private login_profile mainActivity;

    public LoginRunnable(String username,String password, String api, login_profile mainActivity){
        this.username = username;
        this.password = password;
        this.api = api;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Log.d("loginrunnable", "run: ");
        try {
            String urlString = "http://christopherhield.org/api/Profile/Login?userName="+username+"&password="+password;
            Log.d("From loggin runnable ", "run: ");
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

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                Log.d("loginrunnable", "finish ");
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }

            mainActivity.loadContent(result.toString());

        } catch (Exception e) {
            Log.d("loginrunnable", "fail1 ");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }


    }
}
