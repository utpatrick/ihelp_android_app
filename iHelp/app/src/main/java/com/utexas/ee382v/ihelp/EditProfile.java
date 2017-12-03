package com.utexas.ee382v.ihelp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    public final static int PICK_PHOTO_CODE = 1046;
    private Uri photoUri;
    Bitmap selectedImage = null;
    Bitmap bitmap;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void selectFromGallery(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    photoUri = data.getData();
                    // Do something with the photo based on Uri
                    try {
                        Log.d("image", "image path:  " + photoUri.getPath());
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        Log.d("image", "selectedImage1: " + selectedImage);
                        filename = ImageFilePath.getPath(EditProfile.this, data.getData()).substring(ImageFilePath.getPath(EditProfile.this, data.getData()).lastIndexOf("/")+1);
                        Log.d("camera", "gallery file name:  " + filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Load the selected image into a preview
                    ImageView ivPreview = (ImageView) findViewById(R.id.profileButton);
                    ivPreview.setImageBitmap(selectedImage);
                }
            }
        }
    }

}
