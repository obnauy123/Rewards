package csc492.Bo_Y.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class givePoints extends AppCompatActivity {
    private String jsonString;
    private String api;
    private String giver;
    private String giverName;
    private String receiver;
    private TextView name;
    private TextView department;
    private TextView position;
    private TextView story;
    private TextView pointsAwarded;
    private EditText givingPoints;
    private EditText comment;
    private ImageView pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.setupHomeIndicator(getSupportActionBar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_give_points);
        name = findViewById(R.id.addPoint_name);
        department = findViewById(R.id.add_points_department);
        position = findViewById(R.id.awarded_position);
        pointsAwarded = findViewById(R.id.add_points_awarded);
        story = findViewById(R.id.awarded_story);
        givingPoints = findViewById(R.id.points_send);
        comment = findViewById(R.id.award_comment);
        pic = findViewById(R.id.addPoint_pic);

        SharedPreferences sp = getSharedPreferences("leaderSelected", Context.MODE_PRIVATE);
        jsonString = sp.getString("leaderSelected","");
        if(jsonString!=null){
            load(jsonString);
        }

    }
    private void load(String jsonString){
        JSONObject jsReceiver;
        try {
            jsReceiver = new JSONObject(jsonString);
            String receiver_name = jsReceiver.getString("firstName") + " " + jsReceiver.getString("lastName");
            name.setText(receiver_name);
            String receiver_dapartment = jsReceiver.getString("department");
            department.setText(receiver_dapartment);
            String receiver_position = jsReceiver.getString("position");
            position.setText(receiver_position);
            String receiver_story = jsReceiver.getString("story");
            story.setText(receiver_story);
            String imageString = jsReceiver.getString("imageBytes");
            textToImage(imageString);
            receiver = jsReceiver.getString("userName");
            JSONArray reward_history = jsReceiver.getJSONArray("rewardRecordViews");
            try{
                int points = 0;
                for(int i = 0; i < reward_history.length(); i++){
                    JSONObject jsonObject = reward_history.getJSONObject(i);
                    int point = jsonObject.getInt("amount");
                    points+=point;
                }
                pointsAwarded.setText(String.valueOf(points));
            }catch (Exception e){
                e.printStackTrace();
            }


            SharedPreferences sp = getSharedPreferences("API",Context.MODE_PRIVATE);
            api = sp.getString("API","");
            sp = getSharedPreferences("profile", Context.MODE_PRIVATE);
            JSONObject js;
            try{
                js = new JSONObject(sp.getString("profile",""));
                giver = js.getString("userName");
                giverName = js.getString("firstName") + " " + js.getString("lastName");
            }catch (Exception e){
                Log.d("Failed", "load giverinfo");
            }

        }catch (Exception e){
            Log.d("Failed", "load leader");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Rewards Points?");
                builder.setIcon(R.drawable.logo);
                builder.setMessage("Add rewards for "+name.getText().toString() + "?");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        give();

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void give(){
        new Thread(new RewardRunnable(this,api,receiver,giver,comment.getText().toString(),Integer.parseInt(givingPoints.getText().toString()),giverName)).start();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, login_profile.class);
        startActivity(intent);
        finish();
    }
    public void returnToBoard(){
        runOnUiThread(()->{
            Intent intent = new Intent(this,leaderboard.class);
            startActivity(intent);
        });
    }


    public void textToImage(String imageString64) {
        if (imageString64 == null) return;
        try{
            byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            pic.setImageBitmap(bitmap);
        } catch (Exception e){
            pic.setImageResource(R.drawable.default_photo);
        }

    }
}