package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.haerul.swipeviewpager.R;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view_model.EditProfileViewModel;
import java.io.File;
import java.io.InputStream;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;



public class EditProfileActivity extends AppCompatActivity {

    /* Widgets*/
    TextView username, firstName, lastName, email,
            bio, upload_image, change_pwd, upgrade;
    ToggleButton private_mode;
    CircleImageView img;

    EditProfileViewModel editProfileViewModel;
    User user;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;

    private void initialize()
    {
        editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        user = Session.getInstance().getUser();

        username = findViewById(R.id.edit_profile_username);
        firstName = findViewById(R.id.edit_profile_firstName);
        lastName = findViewById(R.id.edit_profile_lastName);
        email = findViewById(R.id.edit_profile_email);
        bio = findViewById(R.id.edit_profile_bio);
        private_mode = findViewById(R.id.edit_profile_toggle_private);
        img = (CircleImageView) findViewById(R.id.edit_profile_photo);
        upload_image = findViewById(R.id.changeProfilePhoto);

        username.setText(user.getUsername());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        bio.setText(user.getBio());
        private_mode.setChecked(user.isPrivate());

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(img);
        downloadTask.execute(BASE_URL+user.getImage_url());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initialize();

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });

        ImageView backArrow = (ImageView) findViewById(R.id.edit_profile_backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        ImageView confirm = (ImageView) findViewById(R.id.edit_profile_saveChanges);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update user in database
                if(editProfileViewModel.updateUser(user.getId(), username.getText().toString(), firstName.getText().toString(),
                        lastName.getText().toString(), email.getText().toString(), bio.getText().toString(), private_mode.isChecked()))
                {
                    // apply Changes to Session and close activity
                    Session.getInstance().getUser().setUsername(username.getText().toString());
                    Session.getInstance().getUser().setFirstName(firstName.getText().toString());
                    Session.getInstance().getUser().setLastName(lastName.getText().toString());
                    Session.getInstance().getUser().setEmail(email.getText().toString());
                    Session.getInstance().getUser().setBio( bio.getText().toString());
                    Session.getInstance().getUser().setPrivate(private_mode.isChecked());

                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


    // Load image from server url
    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageWithURLTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,1);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    //data.getData returns the content URI for the selected Image
                    Uri photo = data.getData();
                    img.setImageURI(photo);

                    //Create a file object using file path
                    File file = new File(getPathFromURI(photo));
                    RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(photo)),
                            file);
                    MultipartBody.Part mfile = MultipartBody.Part.createFormData("profile_image", file.getName(), filePart);
                    editProfileViewModel.uploadProfilePhoto(mfile);

                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    break;
            }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
