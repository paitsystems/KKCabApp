package pait.com.kkcabapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

import pait.com.kkcabapp.broadcastreceiver.MySMSBroadcastReceiver;
import pait.com.kkcabapp.constant.Constant;
import pait.com.kkcabapp.helper.AppSignatureHelper;
import pait.com.kkcabapp.interfaces.ServerCallback;
import pait.com.kkcabapp.log.WriteLog;
import pait.com.kkcabapp.volleyrequests.VolleyRequests;

public class OTPVerificationActivity extends AppCompatActivity implements
        MySMSBroadcastReceiver.OTPReceiveListener, View.OnClickListener {

    private SmsRetrieverClient client;
    private MySMSBroadcastReceiver receiver;
    private MySMSBroadcastReceiver.OTPReceiveListener otpReceiveListener = this;
    private EditText ed1, ed2, ed3, ed4, ed5, ed6;
    private AppCompatButton btn_verifyotp, btn_resendotp;
    private Toast toast;
    private String response_value;

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

        ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed1.getText().toString().length() == 1) {
                    ed2.requestFocus();
                }
            }
        });

        ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed2.getText().toString().length() == 1) {
                    ed3.requestFocus();
                } else if (ed2.getText().toString().length() == 0) {
                    ed1.requestFocus();
                }
            }
        });

        ed3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed3.getText().toString().length() == 1) {
                    ed4.requestFocus();
                } else if (ed3.getText().toString().length() == 0) {
                    ed2.requestFocus();
                }
            }
        });

        ed4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed4.getText().toString().length() == 1) {
                    ed5.requestFocus();
                } else if (ed4.getText().toString().length() == 0) {
                    ed3.requestFocus();
                }
            }
        });

        ed5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed5.getText().toString().length() == 1) {
                    ed6.requestFocus();
                } else if (ed5.getText().toString().length() == 0) {
                    ed4.requestFocus();
                }
            }
        });

        ed6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable editable) {
                if (ed6.getText().toString().length() == 1) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ed6.getWindowToken(), 0);
                    verifyOTP();
                    btn_resendotp.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.lightgray));
                } else if (ed6.getText().toString().length() == 0) {
                    ed5.requestFocus();
                }
            }
        });
    }

    private void init() {
        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        ed3 = findViewById(R.id.ed3);
        ed4 = findViewById(R.id.ed4);
        ed5 = findViewById(R.id.ed5);
        ed6 = findViewById(R.id.ed6);

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
                    startActivity(new Intent(getApplicationContext(),UserDetailActivity.class));
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
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
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
