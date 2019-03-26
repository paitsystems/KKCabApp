package pait.com.kkcabapp;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import pait.com.kkcabapp.constant.Constant;
import pait.com.kkcabapp.model.TripDetailClass;
import pait.com.kkcabapp.model.UserProfileClass;

public class TripDetailsActivity extends AppCompatActivity
            implements View.OnClickListener{

    private TripDetailClass trip;
    private TextView tv_name, tv_mobno, tv_email, tv_veh, tv_date, tv_time;
    private ImageView img_veh, img_one, img_two, img_three, img_four;
    private EditText ed_remark;
    private Button btn_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        trip = (TripDetailClass) getIntent().getExtras().getSerializable("trip");

        init();

        setData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_date:
                break;
            case R.id.tv_time:
                break;
            case R.id.img_one:
                img_one.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_one_in_a_circle_red));
                img_two.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_two_in_a_circle));
                img_three.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_three_in_a_circle));
                img_four.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_four_in_circular_button));
                break;
            case R.id.img_two:
                img_one.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_one_in_a_circle));
                img_two.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_two_in_a_circle_red));
                img_three.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_three_in_a_circle));
                img_four.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_four_in_circular_button));
                break;
            case R.id.img_three:
                img_one.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_one_in_a_circle));
                img_two.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_two_in_a_circle));
                img_three.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_three_in_a_circle_red));
                img_four.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_four_in_circular_button));
                break;
            case R.id.img_four:
                img_one.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_one_in_a_circle));
                img_two.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_two_in_a_circle));
                img_three.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_three_in_a_circle));
                img_four.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_number_four_in_circular_button_red));
                break;
            case R.id.btn_payment:
                break;
        }
    }

    private void init(){
        tv_name = findViewById(R.id.tv_name);
        tv_mobno = findViewById(R.id.tv_mobno);
        tv_email = findViewById(R.id.tv_emailid);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        tv_veh = findViewById(R.id.tv_veh);
        img_veh = findViewById(R.id.img_veh);
        img_one = findViewById(R.id.img_one);
        img_two = findViewById(R.id.img_two);
        img_three = findViewById(R.id.img_three);
        img_four = findViewById(R.id.img_four);
        ed_remark = findViewById(R.id.ed_remark);
        btn_payment = findViewById(R.id.btn_payment);

        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        img_one.setOnClickListener(this);
        img_two.setOnClickListener(this);
        img_three.setOnClickListener(this);
        img_four.setOnClickListener(this);
        btn_payment.setOnClickListener(this);
    }

    private void setData() {
        tv_veh.setText(trip.getVehName());
        Glide.with(getApplicationContext())
                .load(trip.getVehImgName())
                .into(img_veh);

        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        tv_name.setText(user.getfName()+" "+user.getlName());
        tv_mobno.setText(user.getMobileNo());
        tv_email.setText(user.getEmailId());

        tv_date.setText(new Constant(getApplicationContext()).getNextDateTime());
        tv_time.setText(new Constant(getApplicationContext()).getTime());
    }
}
