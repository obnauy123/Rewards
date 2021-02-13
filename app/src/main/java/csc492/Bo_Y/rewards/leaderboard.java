package csc492.Bo_Y.rewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class leaderboard extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    String api;
    private ArrayList<Leader> list;
    private LeaderAdapter adapter;
    private RecyclerView leaderboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Utilities.setupHomeIndicator(getSupportActionBar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        SharedPreferences sp = getSharedPreferences("API", Context.MODE_PRIVATE);
        sp = getSharedPreferences("API", Context.MODE_PRIVATE);
        api = sp.getString("API","");
        list = new ArrayList<>();
        adapter = new LeaderAdapter(list,this);
        leaderboard = findViewById(R.id.leaderboard_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        leaderboard.setLayoutManager(layoutManager);
        leaderboard.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        leaderboard.setAdapter(adapter);
        loading();
    }


    @Override
    public void onClick(View v){
        final int cur_pos = leaderboard.getChildLayoutPosition(v);
        Leader l = list.get(cur_pos);
        JSONObject l_pro = l.getJsonObject();
        String l_p = l_pro.toString();
        SharedPreferences sp = getSharedPreferences("leaderSelected",Context.MODE_PRIVATE);
        sp.edit().putString("leaderSelected",l_p).apply();
        Intent intent = new Intent(this,givePoints.class);
        startActivity(intent);
    }
    @Override
    public boolean onLongClick(View v){
        return true;
    }


    private void loading(){
        new Thread(new GetAllRunnable(api,this, list)).start();
    }

    public void loadall(String jsonString){
        runOnUiThread(()->{
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsLeader = jsonArray.getJSONObject(i);
                        String name = jsLeader.getString("firstName") + " " + jsLeader.getString("lastName");
                        String imageString = jsLeader.getString("imageBytes");

                        String story = jsLeader.getString("story");
                        String department = jsLeader.getString("department");
                        String position = jsLeader.getString("position");
                        JSONArray reward_history = jsLeader.getJSONArray("rewardRecordViews");
                        int points = 0;
                        for (int j = 0; j < reward_history.length(); j++) {
                            JSONObject history = reward_history.getJSONObject(j);
                            points += history.getInt("amount");
                        }

                        list.add(new Leader(name, points, imageString, story, department, position, jsLeader));
                        Collections.sort(list,(a,b)->(b.getPoints()-a.getPoints()));
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.d("Fail", "loadall ");
                }


        });
    }




}