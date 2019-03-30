package pait.com.kkcabagent

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import pait.com.kkcabagent.fragments.DriverDetailsFragment
import pait.com.kkcabagent.fragments.VehicleDetailFragment

class FirstTimeVehicleEntryActivity : AppCompatActivity(),
        VehicleDetailFragment.OnFragmentInteractionListener,
        DriverDetailsFragment.OnFragmentInteractionListener{

    companion object {
        @JvmStatic
        var flag : Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_vehicle_entry)
        addFragment(VehicleDetailFragment.newInstance("",""))
    }

    override fun onFragmentInteraction() {
        when (flag) {
            0 -> {
                finish()
                startActivity(Intent(this,MainActivity::class.java))
            }
            1 -> addFragment(DriverDetailsFragment.newInstance("", ""))
        }
    }

    private fun addFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.content_frame, fragment, "Vehicle Details")
        fragmentTransaction.commitAllowingStateLoss()
    }
}
