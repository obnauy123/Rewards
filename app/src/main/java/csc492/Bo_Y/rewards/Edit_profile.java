package csc492.Bo_Y.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Edit_profile extends AppCompatActivity {
    private final int REQUEST_IMAGE_GALLERY = 1;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView profile_pic;

    private TextView textlength;
    private SharedPreferences.Editor editor;
    private int MAX_LEN = 360;
    private File currentImageFile;
    private String imageString64;
    private String imageString;
    private String api_key;
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText password;
    private EditText department;
    private EditText position;
    private EditText story;
    private String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Utilities.setupHomeIndicator(getSupportActionBar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        profile_pic = findViewById(R.id.profile_pic);
        story = findViewById(R.id.story);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        department = findViewById(R.id.department);
        story = findViewById(R.id.story);
        position = findViewById(R.id.position);
        textlength = findViewById(R.id.character_count);

        setupEditText();
        SharedPreferences sp = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String info = sp.getString("profile","");
        loadContent(info);
        sp = getSharedPreferences("API", Context.MODE_PRIVATE);
        api_key = sp.getString("API","");
        SharedPreferences sp2 = getSharedPreferences("location",Context.MODE_PRIVATE);
        location = sp2.getString("location","Los Angeles, CA");
        story.setMovementMethod(new ScrollingMovementMethod());

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
                    builder.setTitle("Save Changes?");
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            create();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }

    }

    private void loadContent(String jsonString){
        JSONObject js = null;
        try {
            js = new JSONObject(jsonString);
            String firstName = js.getString("firstName");
            String lastName = js.getString("lastName");
            String department = js.getString("department");
            String story = js.getString("story");
            String position = js.getString("position");
            String imageString = js.getString("imageBytes");
            String userName = js.getString("userName");
            String password = js.getString("password");
            textToImage(imageString);
            this.username.setText(userName);
            this.firstName.setText(firstName);
            this.lastName.setText(lastName);
            this.position.setText(position);
            this.department.setText(department);
            this.story.setText(story);
            this.password.setText(password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void textToImage(String imageString64) {
        if (imageString64 == null) return;
        try{
            byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profile_pic.setImageBitmap(bitmap);
        } catch (Exception e){
            profile_pic.setImageResource(R.drawable.default_photo);
        }


    }

    private void create(){
        toBase64();
        new Thread(new UpdateRunnable(this, api_key,firstName.getText().toString(),lastName.getText().toString(),username.getText().toString(),department.getText().toString(),story.getText().toString(),position.getText().toString(),password.getText().toString(),1000,location,imageString)).start();
    }
    public void showProfile(String jsonString){
        runOnUiThread(()->{
            JSONObject js = null;
            try {
                js = new JSONObject(jsonString);
                String fName = js.getString("firstName");
                String lName = js.getString("lastName");
                String userName = js.getString("userName");
                String department = js.getString("department");
                String story = js.getString("story");
                String position = js.getString("position");
                String password = js.getString("password");
                int point_ava = js.getInt("remainingPointsToAward");
                String location = js.getString("location");
                String imageString = js.getString("imageBytes");
                JSONArray reward_record = js.getJSONArray("rewardRecordViews");
                SharedPreferences sp_username = getSharedPreferences("username",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp_username.edit();
                editor.putString("username",userName);
                editor.apply();
                SharedPreferences sp_password = getSharedPreferences("password",Context.MODE_PRIVATE);
                editor = sp_password.edit();
                editor.putString("password",password);
                editor.apply();

                Intent intent = new Intent(this, login_profile.class);
                startActivity(intent);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
    public void add_profile_pic(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");
        builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doGallery(view);
                    }
                }

        );
        builder.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doCamera(view);
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setIcon(R.drawable.logo);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doGallery(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }


    private void doCamera(View v) {
        try {
            currentImageFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(
                this, "com.example.android.fileprovider2", currentImageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private File createImageFile() throws IOException {
        String imageFileName = "image+";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }
    private void processFullCameraImage() {

        Uri selectedImage = Uri.fromFile(currentImageFile);
        profile_pic.setImageURI(selectedImage);

        /// The below is not necessary - it's only done for example purposes
        Bitmap bm = ((BitmapDrawable) profile_pic.getDrawable()).getBitmap();

    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        profile_pic.setImageBitmap(selectedImage);

    }
    public void toBase64() {
        BitmapDrawable drawable = (BitmapDrawable) profile_pic.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        imageString = imageString64;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processFullCameraImage();
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    private void setupEditText() {

        story.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN)
        });

        story.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        textlength.setText(countText);
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {

                    }
                });
    }
}