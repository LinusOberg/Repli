package huka.com.repli;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import views.RoundedImageView;


public class UserInfoActivity extends Activity {

    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private SharedPreferences sharedPreferences;
    private TextView usernameText;
    private TextView emailText;
    private CircleImageView profilePicture;
    private Button changePictureButton;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setImageFileDir();
        // Set these with values from server. Use predefined values for front-end only.
        usernameText = (TextView) findViewById(R.id.userinfo_usernameText);
        emailText = (TextView) findViewById(R.id.userinfo_emailText);
        profilePicture = (CircleImageView) findViewById(R.id.userinfo_picture);
        changePictureButton = (Button) findViewById(R.id.userinfo_changepictureButton);
        setProfilePicture();
    }

    private void setProfilePicture() {
        sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("PROFILE_PICTURE", "none");
        if(imagePath.equals("none")) {
            profilePicture.setImageResource(R.drawable.user_profile_picture);
        } else {
            profilePicture.setImageURI(Uri.parse(imagePath));
        }
    }

    private void setImageFileDir() {
        File dir = UserInfoActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(dir+"/"+"profilePic.jpg");
    }

    public void changePictureListener(View v) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(file);
        Log.v("file: ", "uri: " + uri.getPath());
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Send image to server, for now set it to the view.
            profilePicture.setImageURI(null);
            profilePicture.setImageURI(Uri.fromFile(file));
            SharedPreferences.Editor spEditor = sharedPreferences.edit();
            spEditor.putString("PROFILE_PICTURE", Uri.fromFile(file).getPath()).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            DialogHandler.logoutDialog(this);
        }
        return super.onOptionsItemSelected(item);
    }


}
