package pait.com.kkcabagent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import pait.com.kkcabagent.constant.Constant;
import pait.com.kkcabagent.location.LocationProvider;
import pait.com.kkcabagent.log.WriteLog;
import pait.com.kkcabagent.model.AgentDetailClass;

public class AgentDetailActivity extends AppCompatActivity implements
        View.OnClickListener,LocationProvider.LocationCallback1 {

    private EditText ed_city, ed_agentName, ed_agencyName, ed_emailId, ed_mobNo;
    private Button btn_request;
    private ImageView img_pic;
    private int REQUEST_IMAGE_TAKE = 1;
    private String imagePath = "";
    private Bitmap myBitmap;
    private Uri picUri;
    private LinearLayout lay_pic;
    private CircleImageView croppedImageView;
    private Toast toast;
    private LocationProvider provider;
    private double lat, lon;
    private LatLng latLng;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_detail);
        init();
        provider = new LocationProvider(AgentDetailActivity.this,AgentDetailActivity.this,AgentDetailActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_request:
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

    @Override
    protected void onStart() {
        super.onStart();
        provider.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.showLog("onDestroy");
        provider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location, String _address) {
        Constant.showLog("handleNewLocation");
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            String city = addresses.get(0).getLocality();
            ed_city.setText(city);
            lat = location.getLatitude();
            lon = location.getLongitude();
            address = _address;
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Constant.showLog(lat + "-" + lon);
            Constant.showLog(_address);
        } catch (Exception e) {
            ed_city.setText("NA");
            e.printStackTrace();
        }
    }

    @Override
    public void locationAvailable() {
        Constant.showLog("Location Available");
    }

    private void init() {
        ed_agentName = findViewById(R.id.ed_agentName);
        ed_agencyName = findViewById(R.id.ed_agencyName);
        ed_emailId = findViewById(R.id.ed_email);
        ed_city = findViewById(R.id.ed_city);
        ed_mobNo = findViewById(R.id.ed_mobNo);
        btn_request = findViewById(R.id.btn_request);
        img_pic = findViewById(R.id.img_pic);
        lay_pic = findViewById(R.id.lay_pic);
        croppedImageView = findViewById(R.id.img_profile);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        btn_request.setOnClickListener(this::onClick);
        img_pic.setOnClickListener(this::onClick);
        croppedImageView.setOnClickListener(this::onClick);
    }

    private boolean validation() {
        boolean status = true;
        if (TextUtils.isEmpty(ed_agentName.getText().toString())) {
            status = false;
            toast.setText("Agent Name Required");
        } else if (TextUtils.isEmpty(ed_agencyName.getText().toString())) {
            status = false;
            toast.setText("Agency Name Required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(ed_emailId.getText().toString()).matches()) {
            status = false;
            toast.setText("Valid Email id Required");
        } else if (TextUtils.isEmpty(ed_mobNo.getText().toString()) || ed_mobNo.getText().toString().length()!=10) {
            status = false;
            toast.setText("Mobile Number Required");
        }
        if (!status) {
            toast.show();
        }
        return status;
    }

    private void doThis() {
        AgentDetailClass user = new AgentDetailClass();
        user.setCity(ed_city.getText().toString());
        user.setAgentName(ed_agentName.getText().toString());
        user.setAgencyName(ed_agencyName.getText().toString());
        user.setEmailId(ed_emailId.getText().toString());
        user.setMobileNo(ed_mobNo.getText().toString());
        user.setImgName(imagePath);
        new Constant(getApplicationContext()).saveToPref(user);
        SharedPreferences pref = getSharedPreferences(FirstActivity.getPREF_NAME(),MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("isRegistered",true);
        edit.apply();
        finish();
        startActivity(new Intent(getApplicationContext(), FirstTimeVehicleEntryActivity.class));
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
        new WriteLog().writeLog(getApplicationContext(), "AgentDetailActivity_" + _data);
    }

}

