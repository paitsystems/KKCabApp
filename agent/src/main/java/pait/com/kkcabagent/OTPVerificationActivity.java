package pait.com.kkcabagent;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import pait.com.kkcabagent.R;
import pait.com.kkcabagent.broadcastreceiver.MySMSBroadcastReceiver;
import pait.com.kkcabagent.constant.Constant;
import pait.com.kkcabagent.helper.AppSignatureHelper;
import pait.com.kkcabagent.interfaces.ServerCallback;
import pait.com.kkcabagent.log.WriteLog;
import pait.com.kkcabagent.volleyrequests.VolleyRequests;

public class OTPVerificationActivity extends AppCompatActivity implements
        MySMSBroadcastReceiver.OTPReceiveListener, View.OnClickListener {

    private SmsRetrieverClient client;
    private MySMSBroadcastReceiver receiver;
    private MySMSBroadcastReceiver.OTPReceiveListener otpReceiveListener = this;
    private EditText ed1, ed2, ed3, ed4, ed5, ed6;
    private AppCompatButton btn_verifyotp, btn_resendotp;
    private Toast toast;
    private String response_value;
    private EditText[] editTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        init();

        client = SmsRetriever.getClient(this);

        receiver = new MySMSBroadcastReceiver();
        receiver.initOTPListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(receiver, filter);
        startSMSListener();

        int minutes = 5 * 60 * 1000;
        startTimerCount(minutes);
        if(!RegistrationActivity.mobNumber.equals("0")){
            requestOTP();
        }

        if(Constant.liveTestFlag==0) {
            AppSignatureHelper hash = new AppSignatureHelper(getApplicationContext());
            ArrayList<String> appCodes = hash.getAppSignatures();
            String yourhash = appCodes.get(0);
            Constant.showLog("yourhash-" + yourhash);
        }


    }

    private void init() {
        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        ed3 = findViewById(R.id.ed3);
        ed4 = findViewById(R.id.ed4);
        ed5 = findViewById(R.id.ed5);
        ed6 = findViewById(R.id.ed6);

        editTexts = new EditText[]{ed1, ed2, ed3, ed4, ed5, ed6};
        ed1.addTextChangedListener(new PinTextWatcher(0));
        ed2.addTextChangedListener(new PinTextWatcher(1));
        ed3.addTextChangedListener(new PinTextWatcher(2));
        ed4.addTextChangedListener(new PinTextWatcher(3));
        ed5.addTextChangedListener(new PinTextWatcher(4));
        ed6.addTextChangedListener(new PinTextWatcher(5));

        ed1.setOnKeyListener(new PinOnKeyListener(0));
        ed2.setOnKeyListener(new PinOnKeyListener(1));
        ed3.setOnKeyListener(new PinOnKeyListener(2));
        ed4.setOnKeyListener(new PinOnKeyListener(3));
        ed5.setOnKeyListener(new PinOnKeyListener(4));
        ed6.setOnKeyListener(new PinOnKeyListener(5));

        btn_verifyotp = findViewById(R.id.btn_verifyotp);
        btn_resendotp = findViewById(R.id.btn_resendotp);

        btn_verifyotp.setOnClickListener(this::onClick);
        btn_resendotp.setOnClickListener(this::onClick);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    private void startSMSListener() {
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Constant.showLog("onSuccess");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Constant.showLog("onFailure");
            }
        });
    }

    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0));

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        @SuppressLint("RestrictedApi")
        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
                verifyOTP();
                btn_resendotp.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.lightgray));

            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }

    private void requestOTP() {
        try {
            String url = Constant.ipaddress+"/GetOTPCode?mobileno=" + RegistrationActivity.mobNumber +
                    "&emailId=A&IMEINo=356029084226389&clientId=NA";
            Constant.showLog(url);

            VolleyRequests requests = new VolleyRequests(OTPVerificationActivity.this);
            requests.getOTPCode(url, new ServerCallback() {
                @Override
                public void onSuccess(String response) {
                    if (!response.equals("0") && !response.equals("-1") && !response.equals("-2")) {
                        String arr[] = response.split("-");
                        response_value = arr[1];
                        Constant.showLog(response_value);
                    } else if (!response.equals("0") && response.equals("-1") && !response.equals("-2")) {
                    } else if (!response.equals("0") && !response.equals("-1") && response.equals("-2")) {
                    }
                }

                @Override
                public void onFailure(String result) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyOTP() {
        if (ed1.getText().toString().length() == 1 && ed2.getText().toString().length() == 1 &&
                ed3.getText().toString().length() == 1 && ed4.getText().toString().length() == 1 &&
                ed5.getText().toString().length() == 1 && ed6.getText().toString().length() == 1) {
            String otp = ed1.getText().toString() + ed2.getText().toString() +
                    ed3.getText().toString() + ed4.getText().toString() +
                    ed5.getText().toString() + ed6.getText().toString();
            Constant.showLog("OTP VERIFIED");
            showDia(1);
        }
    }

    private void startTimerCount(int noOfMinutes) {
        new CountDownTimer(noOfMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                //Constant.showLog("count:" + ms);
                btn_verifyotp.setText("VERIFY OTP  "+ms);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                response_value = "0";
                ed1.setText(null);ed2.setText(null);ed3.setText(null);
                ed4.setText(null);ed5.setText(null);ed6.setText(null);
                ed1.requestFocus();
                btn_verifyotp.setVisibility(View.GONE);
                btn_resendotp.setVisibility(View.VISIBLE);
                btn_resendotp.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.maroon));
            }
        }.start();
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OTPVerificationActivity.this);
        builder.setCancelable(false);
        if (a == 1) {
            builder.setMessage("Registration Success");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    SharedPreferences pref = getSharedPreferences(FirstActivity.getPREF_NAME(),MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putBoolean("isRegistered",true);
                    edit.apply();
                    finish();
                    //startActivity(new Intent(getApplicationContext(),UserDetailActivity.class));
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "RegistrationActivity_" + _data);
    }

    @Override
    public void onOTPReceived(String otp) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
        Constant.showLog("onOTPReceived - " + otp +" " +response_value);
        Constant.showLog("message:" + otp);
        ed1.setText(otp.substring(0, 1));
        ed2.setText(otp.substring(1, 2));
        ed3.setText(otp.substring(2, 3));
        ed4.setText(otp.substring(3, 4));
        ed5.setText(otp.substring(4, 5));
        ed6.setText(otp.substring(5, 6));
        ed6.setSelection(ed6.getText().length());
    }

    @Override
    public void onOTPTimeOut() {
        Constant.showLog("onOTPTimeOut");
        btn_verifyotp.setVisibility(View.GONE);
        btn_resendotp.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_otp:
                finish();
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.btn_resendotp:
                startSMSListener();
                requestOTP();
                btn_verifyotp.setVisibility(View.VISIBLE);
                btn_resendotp.setVisibility(View.GONE);
                break;
        }
    }
}
