package pait.com.kkcabapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;

import pait.com.kkcabapp.adapters.RecyclerViewDataAdapter;
import pait.com.kkcabapp.constant.Constant;
import pait.com.kkcabapp.interfaces.ActivityFragmentInterface;
import pait.com.kkcabapp.model.SectionDataModel;
import pait.com.kkcabapp.model.SingleItemModel;
import pait.com.kkcabapp.model.TripDetailClass;

public class VehicleSelectionActivity extends AppCompatActivity
        implements ActivityFragmentInterface, View.OnClickListener {

    private ArrayList<SectionDataModel> allSampleData;
    private LinearLayout lay_info;
    private TextView tv_veh;
    private ImageView img_next;
    private TripDetailClass trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_selection);

        init();

        createDummyData();
        RecyclerView my_recycler_view = findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, allSampleData);
        adapter.setOnDataListener(this);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(adapter);

    }

    @Override
    public void onCallBack(TripDetailClass _trip) {
        trip = _trip;
        lay_info.setVisibility(View.VISIBLE);
        tv_veh.setText(trip.getVehName());
    }

    private void init(){
        allSampleData = new ArrayList<>();
        lay_info = findViewById(R.id.lay_info);
        tv_veh = findViewById(R.id.tv_vehicle);
        img_next = findViewById(R.id.img_next);

        img_next.setOnClickListener(this);
    }

    public void createDummyData() {
        String[] vehType = {"Hatchback","Sedan","Premium","SUV"};
        String[] hbName = {"HatchBack_1.jpg","HatchBack_2.jpg","HatchBack_3.jpg"};
        String[] sedanName = {"Sedan_1.jpg","Sedan_2.jpg","Sedan_3.jpg"};
        String[] premiumName = {"Premium_1.jpg","Premium_2.jpg","Premium_3.jpg"};
        String[] suvName = {"SUV.jpg"};

        String[] hbVehName = {"Maruti Swift","Renualt Kwid","Maruti Alto"};
        String[] sedanVehName = {"Skoda Rapid","Honda Amaze","Swift Dezire"};
        String[] premiumVehName = {"Mercedes","Audi","BMW"};
        String[] suvVehName = {"Toyota Innova"};

        for (int i = 0; i < vehType.length; i++) {
            SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle(vehType[i]);
            String[] arr, nameArr;
            if(i == 0){
                arr = hbName;
                nameArr = hbVehName;
            } else if (i==1){
                arr = sedanName;
                nameArr = sedanVehName;
            } else if (i==2){
                arr = premiumName;
                nameArr = premiumVehName;
            } else {
                arr = suvName;
                nameArr = suvVehName;
            }
            ArrayList<SingleItemModel> singleItem = new ArrayList<>();
            for (int j = 0; j < arr.length; j++) {
                singleItem.add(new SingleItemModel(nameArr[j], Constant.imgIpaddress1+arr[j]));
            }
            dm.setAllItemsInSection(singleItem);
            allSampleData.add(dm);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.img_next:
                Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                intent.putExtra("trip",trip);
                startActivity(intent);
                break;
        }
    }
}
