package pait.com.kkcabagent;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import pait.com.kkcabagent.R;
import pait.com.kkcabagent.constant.Constant;

public class RegistrationActivity extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private GoogleApiClient apiClient;
    private EditText ed_number;
    private Button btn_otp;
    public static String mobNumber = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ed_number = findViewById(R.id.ed_number);
        btn_otp = findViewById(R.id.btn_otp);
        btn_otp.setOnClickListener(this);

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        requestHint();
    }

    @Override
    public void onClick(View view) {
        mobNumber = ed_number.getText().toString();
        if(!mobNumber.equals("")&&mobNumber.length()==10) {
            finish();
            startActivity(new Intent(this, OTPVerificationActivity.class));
        } else {
            Toast.makeText(getApplicationContext(),"Please Enter Mobile Number",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Constant.showLog(requestCode+" "+resultCode+" "+RESULT_OK);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                Constant.showLog(credential.getId());// <-- will need to process phone number string
                String digits = credential.getId(); // replace bigInteger with GrandTotal in your case
                int numberLength = digits.length();
                String number = "";
                for(int i = numberLength ; i > numberLength - 10; i--) {
                    int digit = digits.charAt(i-1) - '0';
                    number = number + digit;
                    Constant.showLog(""+digit+"-"+number);
                }
                number = new StringBuilder(number).reverse().toString();
                Constant.showLog(number);
                ed_number.setText(number);
                ed_number.setSelection(ed_number.getText().length());
                mobNumber = number;
            } else {
                mobNumber = ed_number.getText().toString();
            }
        }
    }

    private void requestHint() {
        try {
            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                    apiClient, hintRequest);
            startIntentSenderForResult(intent.getIntentSender(),
                    1, null, 0, 0, 0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
