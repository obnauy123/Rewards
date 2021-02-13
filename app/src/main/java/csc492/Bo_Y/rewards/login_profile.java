package csc492.Bo_Y.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class login_profile extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private String api_key;
    String profile;

    private TextView profile_name;
    private TextView profile_username;
    private TextView profile_address;
    private TextView points_awarded;
    private TextView profile_department;
    private TextView profile_position;
    private TextView point_ava;
    private TextView profile_story;
    private RecyclerView award_history;
    private TextView reward_count;
    private JSONArray reward_history;
    private ImageView profile_image;
    private ArrayList<Award_record> list;
    private recordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        list = new ArrayList<>();
        adapter = new recordAdapter(list,this);
        profile_name = findViewById(R.id.profile_name);
        profile_username = findViewById(R.id.profile_username);
        profile_address = findViewById(R.id.profile_address);
        points_awarded = findViewById(R.id.point_awarded);
        point_ava = findViewById(R.id.point_available);
        profile_story = findViewById(R.id.profile_story);
        profile_department = findViewById(R.id.profile_deparment);
        profile_position = findViewById(R.id.profile_position);
        award_history = findViewById(R.id.reward_history_recycler_view);
        reward_count = findViewById(R.id.reward_history_count);
        profile_image = findViewById(R.id.your_pic);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        award_history.setLayoutManager(layoutManager);
        award_history.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        award_history.setAdapter(adapter);
        SharedPreferences sp = getSharedPreferences("API", Context.MODE_PRIVATE);
        api_key = sp.getString("API","");
        login();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(netWorkChecker()){
            switch (item.getItemId()){
                case R.id.profile_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Delete Profile?");
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete();
                        }
                    });
                    builder.setIcon(R.drawable.icon);
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                case R.id.profile_edit:
                    editProfile();
                    return true;
                case R.id.leaderboard:
                    goLeader();
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }else{
            return true;
        }
    }
    private void delete(){
        new Thread(new DeleteRunnable(this,profile_username.getText().toString(),api_key)).start();
    }
    private void login(){
        SharedPreferences sp = getSharedPreferences("username",Context.MODE_PRIVATE);
        String login_username = sp.getString("username","");
        sp = getSharedPreferences("password",Context.MODE_PRIVATE);
        String login_password = sp.getString("password","");
        new Thread(new LoginRunnable(login_username,login_password,api_key,this)).start();
    }
    private boolean netWorkChecker(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected()){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;

                }
            });
            android.app.AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return true;

    }

    public void loadContent(String jsonString){
        runOnUiThread(()->{
            profile = jsonString;
            JSONObject js = null;
            try {
                js = new JSONObject(jsonString);
                String firstName = js.getString("firstName");
                String lastName = js.getString("lastName");
                String userName = js.getString("userName");
                String department = js.getString("department");
                String story = js.getString("story");
                String position = js.getString("position");
                int remainingPointsToAward = js.getInt("remainingPointsToAward");
                String location = js.getString("location");
                String imageString = js.getString("imageBytes");
                reward_history = js.getJSONArray("rewardRecordViews");
                textToImage(imageString);
                profile_name.setText(firstName+ " "+lastName);
                profile_username.setText("("+userName+")");
                profile_position.setText(position);
                profile_department.setText(department);
                profile_story.setText(story);
                profile_address.setText(location);
                point_ava.setText(String.valueOf(remainingPointsToAward));
                loadHistory();
                reward_count.setText("Reward Histroy ("+adapter.getItemCount()+"):");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }
    public void textToImage(String imageString64) {
        if (imageString64 == null) return;
        try{
            byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profile_image.setImageBitmap(bitmap);
        } catch (Exception e){
            Log.d("err", e.getMessage());
            profile_image.setImageResource(R.drawable.default_photo);
        }


    }

    @Override
    public void onClick(View view){
        return;
    }
    @Override
    public boolean onLongClick(View v){
        return true;
    }
    private void loadHistory(){
        list.clear();
        try{
            int points = 0;
            for(int i = 0; i < reward_history.length(); i++){
                JSONObject jsonObject = reward_history.getJSONObject(i);
                String name = jsonObject.getString("giverName");
                String date = jsonObject.getString("awardDate");
                int point = jsonObject.getInt("amount");
                String comment = jsonObject.getString("note");
                points+=point;
                list.add(
                        new Award_record(date,name,point,comment)
                );
                adapter.notifyDataSetChanged();
            }
            points_awarded.setText(String.valueOf(points));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void editProfile(){
        try {
            SharedPreferences sp = getSharedPreferences("profile",Context.MODE_PRIVATE);
            sp.edit().putString("profile",profile).apply();
            Intent intent = new Intent(this, Edit_profile.class);
            startActivityForResult(intent,1);
            Log.d("good", "editProfile: ");
        }catch (Exception e){
            Log.d("fail", e.getMessage());
        }
    }

    private void goLeader(){
        try{
        Intent intent = new Intent(this,leaderboard.class);
        startActivityForResult(intent,1);
        Log.d("good", "goLeader: ");
        }catch (Exception e){
            Log.d("Fail", "goLeader: ");
        }
    }

    public void exit(){
        runOnUiThread(()->{
            finishAffinity();
        });

    }



}