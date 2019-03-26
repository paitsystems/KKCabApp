package pait.com.kkcabdriver.constant;

// Created by anup on 4/3/2017.

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pait.com.kkcabdriver.FirstActivity;
import pait.com.kkcabdriver.R;
import pait.com.kkcabdriver.log.WriteLog;
import pait.com.kkcabdriver.model.UserProfileClass;

public class Constant {

    //TODO: Check Ip Address,App Name, Database Version, Application ID
    public static String folder_name = "KK";
    public static String log_file_name = "KKCabLog";
    //public static final String ipaddress = "http://172.30.1.209/KKCAB/service.svc";
    public static final String ipaddress = "http://license.lnbinfotech.com/KKCAB/service.svc";

    //TODO: Check CustImage Url
    public static final String imgIpaddress = "http://license.lnbinfotech.com/PAIT/PAIT.jpg";
    public static final String imgIpaddress1 = "http://license.lnbinfotech.com/PAIT/KK/";
    public static final String bgImgIPAddress = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";

    public static String automailID = "automail@lnbinfotech.com",
                        autoamilPass = "auto#456",
                        mail_subject = "Log File",
                        mail_body = "Find the Attached Log File",
                        mailReceipient = "anup.p@paitsystems.com";
    public static String ftp_adress = "ftp.lnbinfotech.com";
    public static String ftp_username = "supportftp@lnbinfotech.com";
    public static String ftp_password = "support$456";
    public static String ftp_folder = "KKCab";

    public static String support_mail_id = "anup.p@paitsystems.com";

    private Activity activity;
    private Context context;
    private ProgressDialog pd;

    public static int liveTestFlag = 0;
    public static int TIMEOUT_CON = 10000;
    public static int TIMEOUT_SO = 60;

    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String PREF_NAME = "booking";

    public static void showLog(String log) {
        Log.d("Log", "" + log);
    }

    public static SimpleDateFormat format_display = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public static SimpleDateFormat format_mon = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
    public static SimpleDateFormat format_save = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static SimpleDateFormat format_year = new SimpleDateFormat("yy", Locale.ENGLISH);
    public static SimpleDateFormat format_sql = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
    public static SimpleDateFormat format_time = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

    public Constant(Activity activity) {
        this.activity = activity;
        pd = new ProgressDialog(activity);
        pd.setCancelable(false);
        pd.setMessage("Please Wait");
    }

    public Constant(Context context) {
        this.context = context;
    }

    public void showPD() {
        if (pd.isShowing()) {
            pd.dismiss();
        } else {
            pd.show();
        }
    }

    public void doFinish() {
        activity.finish();
        activity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public String getDate() {
        String str = "";
        try {
            str = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getTime() {
        String str = "";
        try {
            str = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getNextDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy",Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        String todayAsString = dateFormat.format(today);
        String tomorrowAsString = dateFormat.format(tomorrow);
        System.out.println(todayAsString);
        System.out.println(tomorrowAsString);
        String str = "";
        try{
            str = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(tomorrow);
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    public static File checkFolder(String foldername) {
        File extFolder = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + foldername);
        if (!extFolder.exists()) {
            if (extFolder.mkdir()) {
                showLog("Directory Created");
            }
        }
        return extFolder;
    }

    public static File checkSubFolder(String foldername, String subFolder) {
        File extFolder = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + foldername + File.separator + subFolder);
        if (!extFolder.exists()) {
            if (extFolder.mkdir()) {
                showLog("Directory Created");
            }
        }
        return extFolder;
    }

    public String getIMEINo() {
        String myAndroidDeviceId;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (manager.getDeviceId() != null) {
            myAndroidDeviceId = manager.getDeviceId();
        } else {
            myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return myAndroidDeviceId;
    }

    public String getIMEINo1(){
        String imeino="";
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
            Log.d("Log", getFirstMethod.toString());
            Object[] obParameter = new Object[1];
            obParameter[0] = 0;
            //TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String first = (String) getFirstMethod.invoke(telephony, obParameter);
            Log.d("Log", "FIRST :" + first);
            obParameter[0] = 1;
            String second = (String) getFirstMethod.invoke(telephony, obParameter);
            Log.d("Log", "SECOND :" + second);
            imeino = first+"^"+second;
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("getIMEINo1_"+e.getMessage());
        }
        return imeino;
    }

    public String getFinYr(){
        String date1 = getDate();
        String[] dateArr = date1.split("/");
        String year = dateArr[2];
        int y = Integer.parseInt(year.substring(2, 4));
        String finyr = y + "-" + String.valueOf(y + 1);
        showLog(finyr);
        return finyr;
    }

    public void saveToPref(UserProfileClass user){
        SharedPreferences pref = context.getSharedPreferences(FirstActivity.getPREF_NAME(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("obj", json);
        editor.apply();
    }

    public UserProfileClass getPref(){
        SharedPreferences pref = context.getSharedPreferences(FirstActivity.getPREF_NAME(),Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("obj", null);
        return gson.fromJson(json, UserProfileClass.class);
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(context, _data);
    }
}
