package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.haerul.swipeviewpager.R;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view_model.ProfileViewModel;
import java.io.InputStream;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmUnfollowActivity extends AppCompatActivity {
    TextView cancel, unfollow, following_username;
    CircleImageView following_avatar;
    User following;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm_unfollow);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        following = (User) bundle.getSerializable("following");
        cancel = findViewById(R.id.cancel);
        unfollow = findViewById(R.id.unfollow);
        following_username = findViewById(R.id.following_username);
        following_avatar = findViewById(R.id.following_profile_image);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(following_avatar);
        downloadTask.execute(Common.BASE_PROFILE_IMAGE_URL + following.getImage_url());

        String initial_message = "If you change your mind, you\'ll have to request to follow @" + following.getUsername() + " again.";
        final SpannableStringBuilder sb = new SpannableStringBuilder(initial_message);

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, initial_message.indexOf("@"), initial_message.indexOf("again"), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make the username Bold

        following_username.setText(sb);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileViewModel.unfollowUser(following.getId());
                Session.getInstance().getUser().getFollowing().remove(following);
                Common.followsFragmentCallback.updateAfterUnfollow();
                finish();
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
}
