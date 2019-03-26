package pait.com.kkcabapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import pait.com.kkcabapp.constant.Constant;
import pait.com.kkcabapp.log.WriteLog;
import pait.com.kkcabapp.model.UserProfileClass;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_fName, ed_lName, ed_emailId;
    private Button btn_Save;
    private ImageView img_pic;
    private int REQUEST_IMAGE_TAKE = 1;
    private String imagePath = "";
    private Bitmap myBitmap;
    private Uri picUri;
    private LinearLayout lay_pic;
    private CircleImageView croppedImageView;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                if(validation()){
                    doThis();
                }
                break;
            case R.id.img_pic:
                startActivityForResult(getPickImageChooserIntent(), 200);
                break;
            case R.id.img_profile:
                startActivityForResult(getPickImageChooserIntent(), 200);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {
            lay_pic.setVisibility(View.GONE);
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    myBitmap = getResizedBitmap(myBitmap, 500);
                    croppedImageView.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap = bitmap;
                if (croppedImageView != null) {
                    croppedImageView.setImageBitmap(myBitmap);
                }
            }
        }
    }

    private void init() {
        ed_fName = findViewById(R.id.ed_fName);
        ed_lName = findViewById(R.id.ed_lName);
        ed_emailId = findViewById(R.id.ed_email);
        btn_Save = findViewById(R.id.btn_save);
        img_pic = findViewById(R.id.img_pic);
        lay_pic = findViewById(R.id.lay_pic);
        croppedImageView = findViewById(R.id.img_profile);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        btn_Save.setOnClickListener(this::onClick);
        img_pic.setOnClickListener(this::onClick);
        croppedImageView.setOnClickListener(this::onClick);
    }

    private boolean validation() {
        boolean status = true;
        if (TextUtils.isEmpty(ed_fName.getText().toString())) {
            status = false;
            toast.setText("First Name Required");
        } else if (TextUtils.isEmpty(ed_lName.getText().toString())) {
            status = false;
            toast.setText("Last Name Required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(ed_emailId.getText().toString()).matches()) {
            status = false;
            toast.setText("Valid Email id Required");
        }
        if (!status) {
            toast.show();
        }
        return status;
    }

    private void doThis() {
        UserProfileClass user = new UserProfileClass();
        user.setfName(ed_fName.getText().toString());
        user.setlName(ed_lName.getText().toString());
        user.setEmailId(ed_emailId.getText().toString());
        user.setMobileNo(RegistrationActivity.mobNumber);
        user.setImgName(imagePath);
        new Constant(getApplicationContext()).saveToPref(user);
        finish();
        startActivity(new Intent(getApplicationContext(), CitySelectionActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        Constant.checkFolder(Constant.folder_name);
        File getImage = new File(Environment.getExternalStorageDirectory()+"/"+Constant.folder_name);
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList();
        PackageManager packageManager = getPackageManager();
        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "UserDetailActivity_" + _data);
    }

}
