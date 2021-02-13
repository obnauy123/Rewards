package csc492.Bo_Y.rewards;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button login_button;
    String api_key = "";
    private SharedPreferences sharedPreferences;
    private EditText fN;
    private EditText lN;
    private EditText Sid;
    private EditText Semail;
    private EditText username;
    private EditText password;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private String locationString;
    private String login_username;
    private String login_password;
    private CheckBox checkBox;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("New begin", "onCreate: ");
        setContentView(R.layout.activity_main);
        login_button = findViewById(R.id.login_button);
        login_button.setBackgroundColor(R.color.grey);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        checkBox = findViewById(R.id.checkBox);
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        SharedPreferences sp = getSharedPreferences("location",Context.MODE_PRIVATE);
        if(netWorkChecker()){
            sharedPreferences = getSharedPreferences("API", Context.MODE_PRIVATE);
            api_key = sharedPreferences.getString("API","");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            determineLocation();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("location",locationString);
            editor.apply();

            if(api_key.length()==0){
                Toast.makeText(this, "NO API", Toast.LENGTH_LONG).show();
                getAPI();
            }
        }
        sp =getSharedPreferences("checked",Context.MODE_PRIVATE);
        checkBox.setChecked(sp.getBoolean("checked",false));
        if(!checkBox.isChecked()){
            sp = getSharedPreferences("saveusername",Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.clear();
            e.apply();
            sp = getSharedPreferences("savepassword",Context.MODE_PRIVATE);
            e = sp.edit();
            e.clear();
            e.apply();
            username.setText("");
            password.setText("");

        }else{
            sp = getSharedPreferences("saveusername",Context.MODE_PRIVATE);
            username.setText(sp.getString("saveusername",""));
            sp = getSharedPreferences("savepassword",Context.MODE_PRIVATE);
            password.setText(sp.getString("savepassword",""));

        }

    }

    public void createProfile(View v){
        Intent intent = new Intent(this, create_profile.class);
        startActivity(intent);
    }
    private void getAPI(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View layout = factory.inflate(R.layout.api_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        fN = (EditText) layout.findViewById(R.id.fName);
        lN = (EditText) layout.findViewById(R.id.lName);
        Sid = (EditText) layout.findViewById(R.id.Sid);
        Semail = (EditText) layout.findViewById(R.id.Semail);
        builder.setTitle("API Key Needed");
        builder.setMessage("You need to request an API Key:");
        builder.setView(layout);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String a = fN.getText().toString();
                String b = lN.getText().toString();
                String c = Sid.getText().toString();
                String d = Semail.getText().toString();
                GetApi(a,b,c,d);
            }
        });
        builder.setIcon(R.drawable.logo);
        AlertDialog alert = builder.create();
        alert.show();

    }
    private void GetApi(String fN, String lN,String Sid,String Semail){
        new Thread(new GetApiRunnable(this,fN,lN,Sid,Semail)).start();
    }

    public void resetAPI(View view){
        api_key = "";
        sharedPreferences = getSharedPreferences("API",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("API","");
        editor.apply();
        getAPI();
    }
    public void saveResult(String jsonString){
        runOnUiThread(() -> {
            JSONObject js = null;
            try {
                js = new JSONObject(jsonString);
                String semail = js.getString("email");
                String api = js.getString("apiKey");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("API",api);
                editor.apply();
                api_key = api;
                LayoutInflater factory = LayoutInflater.from(this);
                final View layout = factory.inflate(R.layout.api_result, null);
                final TextView name = layout.findViewById(R.id.api_result_name);
                final TextView sid = layout.findViewById(R.id.api_result_sid);
                final TextView email = layout.findViewById(R.id.api_result_semail);
                final TextView api_Key = layout.findViewById(R.id.api_result_api);
                name.setText(fN.getText().toString() + " " + lN.getText().toString());
                sid.setText(Sid.getText().toString());
                email.setText(semail);
                api_Key.setText(api);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("API Key Received and Stored");
                builder.setIcon(R.drawable.logo);
                builder.setView(layout);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

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

    @Override
    public void onClick(View view) {

        SharedPreferences sp = getSharedPreferences("username",Context.MODE_PRIVATE);
        login_username = username.getText().toString();
        sp.edit().putString("username",login_username).apply();
        sp = getSharedPreferences("password",Context.MODE_PRIVATE);
        login_password = password.getText().toString();
        sp.edit().putString("password",login_password).apply();

        if(!checkBox.isChecked()){
            sp = getSharedPreferences("saveusername",Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.clear();
            e.apply();
            sp = getSharedPreferences("savepassword",Context.MODE_PRIVATE);
            e = sp.edit();
            e.clear();
            e.apply();
            username.setText("");
            password.setText("");
            sp =getSharedPreferences("checked",Context.MODE_PRIVATE);
            sp.edit().putBoolean("checked",false).apply();
        }else{
            sp = getSharedPreferences("saveusername",Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putString("saveusername",login_username);
            e.apply();
            sp = getSharedPreferences("savepassword",Context.MODE_PRIVATE);
            e = sp.edit();
            e.putString("savepassword",login_password);
            e.apply();
            sp = getSharedPreferences("checked",Context.MODE_PRIVATE);
            sp.edit().putBoolean("checked",true).apply();
        }
        Intent intent = new Intent(this,login_profile.class);
        startActivity(intent);
    }

//    public void toProfile(String jsonString){
//        runOnUiThread(()->{
//                SharedPreferences sp = getSharedPreferences("profile",Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("profile",jsonString);
//                editor.apply();
//                Intent intent = new Intent(this, login_profile.class);
//                startActivity(intent);
//        });
//    }
    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);

                        }
                    })
                    .addOnFailureListener(this, e -> Log.d("fail", e.getMessage()));
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }


    private String getPlace(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            return city + ", " + state + "\n\nProvider: " + loc.getProvider();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Log.d("location fail", "onRequestPermissionsResult: ");
                }
            }
        }
    }
}