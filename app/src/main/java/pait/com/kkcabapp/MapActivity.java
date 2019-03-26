package pait.com.kkcabapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pait.com.kkcabapp.constant.Constant;
import pait.com.kkcabapp.location.LocationProvider;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationProvider.LocationCallback1{

    private LocationProvider provider;
    private double lat, lon;
    private LatLng latLng;
    private String address;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    public MapActivity(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapActivity = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);

        provider = new LocationProvider(MapActivity.this,MapActivity.this,MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        map = mMap;
        provider.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.showLog("onDestroy");
        provider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location,String _address) {
        Constant.showLog("handleNewLocation");
        lat = location.getLatitude();
        lon = location.getLongitude();
        address = _address;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Constant.showLog(lat+"-"+lon);
        Constant.showLog(_address);
        if(latLng!=null){
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(latLng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(45)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
            map.addMarker(new MarkerOptions().position(latLng)
                    .position(new LatLng(lat,lon))
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        }
    }

    @Override
    public void locationAvailable() {
        Constant.showLog("Location Available");
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}