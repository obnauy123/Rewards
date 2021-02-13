package csc492.Bo_Y.rewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.security.auth.login.LoginException;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetApiRunnable implements Runnable {

    private static final String TAG = "RestGetAsyncTask";
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;
    private final String firstName, lastName, studentId, email;

    GetApiRunnable(MainActivity mainActivity, String firstName, String lastName, String studentId, String email) {
        this.mainActivity = mainActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.email = email;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String urlString = "http://christopherhield.org/api/Profile/GetStudentApiKey?firstName="+firstName+"&lastName="+lastName+"&studentId="+studentId+"&email="+email;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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
                Log.d(TAG, result.toString());
            }
            mainActivity.saveResult(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "run: catch err" + e.getMessage() );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }

    }
}
