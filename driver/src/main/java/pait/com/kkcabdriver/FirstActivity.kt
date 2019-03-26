package pait.com.kkcabdriver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import pait.com.kkcabdriver.permission.CheckPermission

class FirstActivity : AppCompatActivity() {

    private var permission = CheckPermission()
    companion object {
        @JvmStatic val PREF_NAME = "shared"
        @JvmStatic val rdo_Airport = "airport"
        @JvmStatic val rdo_Railway = "train_station"
        @JvmStatic val rdo_Arrival = "arrival"
        @JvmStatic val rdo_Departure = "departure"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        checkPermission()
    }

    private fun checkPermission() {
        if (!permission.checkCameraPermission(applicationContext)) {
            permission.requestCameraPermission(this)    //1
        } else if (!permission.checkReadPhoneStatePermission(applicationContext)) {
            permission.requestReadPhoneStatePermission(this)    //2
        } else if (!permission.checkReadExternalStoragePermission(applicationContext)) {
            permission.requestReadExternalStoragePermission(this)   //3
        } else if (!permission.checkWriteExternalStoragePermission(applicationContext)) {
            permission.requestWriteExternalStoragePermission(this)  //4
        } else if (!permission.checkFineLocationPermission(applicationContext)) {
            permission.requestFineLocationPermission(this)     //5
        } else if (!permission.checkCoarseLocationPermission(applicationContext)) {
            permission.requestCoarseLocationPermission(this)    //6
        } else {
            Log.d("Log", "Success")
            doThis()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            1 -> checkPermission()
            2 -> checkPermission()
            3 -> checkPermission()
            4 -> checkPermission()
            5 -> checkPermission()
            6 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Log", "Success")
            }
        }
    }

    private fun doThis(){
        finish()
        val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if(!pref.contains("isRegistered")) {
            startActivity(Intent(this, RegistrationActivity::class.java))
        } else {
            startActivity(Intent(this,CitySelectionActivity::class.java))
        }
        //startActivity(Intent(this,VehicleSelectionActivity::class.java))
        //startActivity(Intent(this,MainActivity::class.java))
        //startActivity(Intent(this,CitySelectionActivity::class.java))
        //startActivity(Intent(this,PlaceSearchActivity::class.java))
    }
}
