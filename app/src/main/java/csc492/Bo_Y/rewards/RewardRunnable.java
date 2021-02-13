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

public class RewardRunnable implements Runnable {

    String api;
    String receiver;
    String giver;
    int amount;
    String note;
    String giverName;
    givePoints parentAct;
    RewardRunnable(givePoints parentAct, String api,String receiver,String giver,String note, int amount, String giverName){
        this.api = api;
        this.parentAct = parentAct;
        this.giver = giver;
        this.receiver = receiver;
        this.amount = amount;
        this.giverName = giverName;
        this.note = note;
    }



    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Rewards/AddRewardRecord?receiverUser="+receiver+"&giverUser="+giver+"&giverName="+giverName+"&amount="+amount+"&note="+note;

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", api);
            connection.connect();


            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == 201) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("given points", "success");
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
            parentAct.returnToBoard();

        } catch (Exception e) {
            Log.d("err", e.getMessage());
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
