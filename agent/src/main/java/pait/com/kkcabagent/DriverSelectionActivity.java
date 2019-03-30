package pait.com.kkcabagent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pait.com.kkcabagent.adapters.RecyclerViewDataAdapter;
import pait.com.kkcabagent.constant.Constant;
import pait.com.kkcabagent.interfaces.ActivityFragmentInterface;
import pait.com.kkcabagent.model.SectionDataModel;
import pait.com.kkcabagent.model.SingleItemModel;
import pait.com.kkcabagent.model.TripDetailClass;

public class DriverSelectionActivity  extends AppCompatActivity
        implements ActivityFragmentInterface, View.OnClickListener {

    private ArrayList<SectionDataModel> allSampleData;
    private LinearLayout lay_info;
    private TextView tv_veh;
    private ImageView img_next;
    private TripDetailClass trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_selection);

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
        String[] vehType = {"Select Driver"};
        String[] hbName = {"HatchBack_11.jpg","HatchBack_21.jpg","HatchBack_31.jpg","HatchBack_11.jpg","HatchBack_21.jpg","HatchBack_31.jpg"};

        String[] hbVehName = {"ABC","XYZ","PQR","ABC1","XYZ1","PQR1"};

        for (int i = 0; i < vehType.length; i++) {
            SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle(vehType[i]);
            String[] arr, nameArr;
            arr = hbName;
            nameArr = hbVehName;
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
                /*Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                intent.putExtra("trip",trip);
                startActivity(intent);*/
                break;
        }
    }
}

