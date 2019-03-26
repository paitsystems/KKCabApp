package pait.com.kkcabapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import pait.com.kkcabapp.PlaceSearchActivity;
import pait.com.kkcabapp.R;
import pait.com.kkcabapp.VehicleSelectionActivity;
import pait.com.kkcabapp.constant.Constant;
import pait.com.kkcabapp.location.LocationProvider;
import pait.com.kkcabapp.volleyrequests.DirectionsJSONParser;


public class HomeFragment extends Fragment implements OnMapReadyCallback,
        LocationProvider.LocationCallback1,View.OnClickListener, GoogleMap.OnCameraIdleListener {
        //GoogleMap.OnCameraChangeListener, GoogleMap.OnCameraMoveListener

    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2", ARG_PARAM3 = "param3";
    private String city, from, to;
    private LocationProvider provider;
    private double lat, lon, perKm = 100;
    private LatLng sourceLatLng, destLatLng;
    private String address = "Pinned Location";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private EditText ed_source, ed_destination;
    private ImageView img_reverse, img_next, img_pin, img_van, img_reset;
    private TextView tv_km, tv_fare;
    private LinearLayout lay_info;
    private int sourceDestFlag = -1;
    private String sourceTitle = "", destTitle = "";
    private float zoom = 15;
    private Marker sourceMarker, destMarker;
    private Button btn_confirm;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2, String param3) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            city = getArguments().getString(ARG_PARAM1);
            from = getArguments().getString(ARG_PARAM2);
            to = getArguments().getString(ARG_PARAM3);
        }
        provider = new LocationProvider(getContext(),this,getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        init(rootView);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  HomeFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);

        /*mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.4219999,-122.0862462))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4219999, -122.0862462))
                        .title("Spider Man")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_male)));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4629101,-122.2449094))
                        .title("Iron Man")
                        .snippet("His Talent : Plenty of money"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.3092293,-122.1136845))
                        .title("Captain America"));
            }
        });*/
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setCompassEnabled(false);
        /*googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);*/
        //provider.connect();
        map.setOnCameraIdleListener(this);
        //map.setOnCameraMoveListener(this);
        //map.setOnCameraChangeListener(this);
        new DataLongOperationAsynchTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        provider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location, String _address) {
        Constant.showLog("handleNewLocation");
        lat = location.getLatitude();
        lon = location.getLongitude();
        //address = _address;
        sourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Constant.showLog(lat+"-"+lon);
        Constant.showLog(_address);
        ed_source.setText(address);
        ed_destination.requestFocus();
        sourceDestFlag = 1;
        if(sourceLatLng!=null){
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(sourceLatLng)
                    .zoom(zoom)
                    .bearing(0)
                    .tilt(45)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
            /*map.addMarker(new MarkerOptions()
                    .position(sourceLatLng)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/
            img_pin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void locationAvailable() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ed_source:
                sourceDestFlag = 1;
                if (destLatLng != null) {
                    destMarker = map.addMarker(new MarkerOptions()
                            .position(destLatLng)
                            .title(destTitle)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                startActivityForResult(new Intent(getActivity(), PlaceSearchActivity.class), 2);
                break;
            case R.id.ed_destination:
                //startActivity(new Intent(getActivity(), PlaceSearchActivity.class));
                sourceDestFlag = 2;
                if (sourceLatLng != null) {
                    sourceMarker = map.addMarker(new MarkerOptions()
                            .position(sourceLatLng)
                            .title(sourceTitle)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                startActivityForResult(new Intent(getActivity(), PlaceSearchActivity.class), 1);
                break;
            case R.id.img_reverse:
                img_reverse.startAnimation(animate(true));
                break;
            case R.id.img_next:
                startActivity(new Intent(getContext(), VehicleSelectionActivity.class));
                break;
            case R.id.img_reset:
                reset();
                break;
            case R.id.img_van:
                img_van.startAnimation(inFromLeftAnimation());
                break;
            case R.id.btn_confirm:
                btn_confirm.setVisibility(View.GONE);
                img_pin.setVisibility(View.GONE);
                map.setOnCameraIdleListener(null);
                if (sourceDestFlag == 2) {
                    if (destLatLng != null) {
                        destMarker = map.addMarker(new MarkerOptions()
                                .position(destLatLng)
                                .title(destTitle)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    }
                } else if (sourceDestFlag == 1) {
                    if (sourceLatLng != null) {
                        sourceMarker = map.addMarker(new MarkerOptions()
                                .position(sourceLatLng)
                                .title(sourceTitle)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
                String url = getDirectionsUrl(sourceLatLng, destLatLng);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                CalculationByDistance(sourceLatLng, destLatLng);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = data.getParcelableExtra("MESSAGE");
                Constant.showLog("Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getLatLng() + ", " + place.getId());
                ed_destination.setText(place.getAddress());
                img_pin.setImageDrawable(getResources().getDrawable(R.drawable.ic_pin_dest));
                if (destMarker != null) {
                    destMarker.remove();
                }
                destLatLng = place.getLatLng();
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(destLatLng)
                        .zoom(zoom)
                        .bearing(0)
                        .tilt(45)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
                btn_confirm.setVisibility(View.VISIBLE);
                /*map.addMarker(new MarkerOptions()
                        .position(destLatLng)
                        .title("Your Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));*/

                /*String url = getDirectionsUrl(sourceLatLng, destLatLng);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);*/
                //CalculationByDistance(sourceLatLng, destLatLng);
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = data.getParcelableExtra("MESSAGE");
                Constant.showLog("Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getLatLng() + ", " + place.getId());
                ed_source.setText(place.getAddress());
                img_pin.setImageDrawable(getResources().getDrawable(R.drawable.ic_pin_source));
                if(sourceMarker!=null) {
                    sourceMarker.remove();
                }
                sourceLatLng = place.getLatLng();
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(sourceLatLng)
                        .zoom(zoom)
                        .bearing(0)
                        .tilt(45)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
                /*map.addMarker(new MarkerOptions()
                        .position(sourceLatLng)
                        .title(sourceTitle)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));*/

                /*String url = getDirectionsUrl(sourceLatLng, destLatLng);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);*/
                //CalculationByDistance(sourceLatLng, destLatLng);
            }
        }
    }

    @Override
    public void onCameraIdle() {
        /*map.clear();
        map.addMarker(new MarkerOptions().position(map.getCameraPosition().target)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));*/
        LatLng _latLng = map.getCameraPosition().target;
        if(sourceDestFlag == 1) {
            sourceLatLng = _latLng;
            sourceTitle = getLocationString(_latLng);
            ed_source.setText(sourceTitle);
        } else if(sourceDestFlag == 2){
            destLatLng = _latLng;
            destTitle = getLocationString(_latLng);
            ed_destination.setText(destTitle);
        }
    }

    /*@Override
    public void onCameraChange(CameraPosition cameraPosition) { }

    @Override
    public void onCameraMove(){}*/

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(1500);
        inFromLeft.setRepeatCount(20);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0,0,view.getHeight(),0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private String getLocationString(LatLng _latLng){
        String str = "Pinned Location";
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(_latLng.latitude, _latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (!locality.isEmpty() && !country.isEmpty())
                    //ed_source.setText(locality + "  " + country);
                    str = locality;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private class DownloadTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                map.addPolyline(lineOptions);
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(sourceLatLng)
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
            }else {
                Toast.makeText(getContext(),"Empty",Toast.LENGTH_LONG).show();
            }
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                + parameters+"&key="+getString(R.string.api_key2)+"&mode=driving&alternatives=false";
        Constant.showLog(url);

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Constant.showLog(data);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Constant.showLog(valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
        String str = "KM : " + kmInDec;
        tv_km.setText(str);
        str = " Rs. : " + (kmInDec * perKm);
        tv_fare.setText(str);
        lay_info.setVisibility(View.VISIBLE);
        slideUp(lay_info);
        return Radius * c;
    }

    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {

        ProgressDialog dialog = new ProgressDialog(getContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                String url = "https://maps.google.com/maps/api/geocode/json?address="+from+"%20"+city+"&sensor=false" +
                        "&key="+getString(R.string.api_key2);
                Constant.showLog(url);
                response = getLatLongByURL(url);
                Constant.showLog(""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");
                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                sourceLatLng = new LatLng(lat,lng);
                Constant.showLog(lat+"-"+lon);
                ed_source.setText(address);
                ed_destination.requestFocus();
                sourceDestFlag = 1;

                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(lat,lng))
                        .zoom(zoom)
                        .bearing(0)
                        .tilt(45)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
                img_pin.setVisibility(View.VISIBLE);

                Constant.showLog("" + lat);
                Constant.showLog("" + lng);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private void reset(){
        map.clear();
        provider.connect();
        ed_source.setText(null);
        ed_destination.setText(null);
        ed_destination.requestFocus();
        slideDown(lay_info);
    }

    private void init(View view){
        ed_source = view.findViewById(R.id.ed_source);
        ed_destination = view.findViewById(R.id.ed_destination);
        img_reverse = view.findViewById(R.id.img_reverse);
        tv_km = view.findViewById(R.id.tv_km);
        tv_fare = view.findViewById(R.id.tv_fare);
        img_next = view.findViewById(R.id.img_next);
        img_pin = view.findViewById(R.id.img_pin);
        img_van = view.findViewById(R.id.img_van);
        img_reset = view.findViewById(R.id.img_reset);
        lay_info = view.findViewById(R.id.lay_info);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        ed_source.setOnClickListener(this);
        ed_destination.setOnClickListener(this);
        img_reverse.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        img_van.setOnClickListener(this);
        img_next.setOnClickListener(this);
        img_reset.setOnClickListener(this);
    }

    private Animation animate(boolean up) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), up ? R.anim.rotate_up : R.anim.rotate_up);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
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
