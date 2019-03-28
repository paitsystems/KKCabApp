package pait.com.kkcabagent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_city_selection.*
import pait.com.kkcabagent.constant.Constant
import pait.com.kkcabagent.model.CityMasterClass
import pait.com.kkcabagent.R

class CitySelectionActivity : AppCompatActivity(), View.OnClickListener {

    private var mFirebaseDatabase: DatabaseReference? = null
    private val city: MutableList<CityMasterClass> = mutableListOf()
    private val cityList: MutableList<String?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_selection)

        val adapter = ArrayAdapter<String>(
                this, // Context
                android.R.layout.simple_dropdown_item_1line, // Layout
                cityList // Array
        )
        auto_city.threshold = 0
        adapter.setNotifyOnChange(true)

        mFirebaseDatabase=FirebaseDatabase.getInstance().getReference("CityMaster")

        mFirebaseDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                city.clear()
                cityList.clear()
                dataSnapshot.children.mapNotNullTo(city) {
                    it.getValue<CityMasterClass>(CityMasterClass::class.java)
                }

                for(c in city){
                    Constant.showLog(c!!.City+"-"+c.Id)
                    cityList.add(c!!.City)
                }

                /*val city = dataSnapshot.getValue(CityMasterClass::class.java)
                Constant.showLog(city!!.City+""+city.Id)*/

                //setAutoAdapter()

                auto_city.setAdapter(adapter)
                auto_city.setSelection(0)
                adapter.notifyDataSetChanged()
                auto_city.showDropDown()

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        auto_city.setOnClickListener { view ->
            auto_city.showDropDown()
        }

        auto_city.onItemClickListener = AdapterView.OnItemClickListener{
            parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            val c = city.get(position)
            Toast.makeText(applicationContext,"Selected : $selectedItem" +"- Id "+c.Id,Toast.LENGTH_SHORT).show()
        }

        btn_next.setOnClickListener(this)
        rdo_airport.setOnClickListener(this)
        rdo_railway.setOnClickListener(this)
        rdo_arrival.setOnClickListener(this)
        rdo_departure.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> {
                if(validate()) {
                    doThis()
                }
            }
            R.id.rdo_airport -> {
                rdo_airport.isChecked = true
                rdo_railway.isChecked = false
            }
            R.id.rdo_railway -> {
                rdo_airport.isChecked = false
                rdo_railway.isChecked = true
            }
            R.id.rdo_arrival -> {
                rdo_arrival.isChecked = true
                rdo_departure.isChecked = false
            }
            R.id.rdo_departure -> {
                rdo_arrival.isChecked = false
                rdo_departure.isChecked = true
            }
        }
    }

    private fun validate() : Boolean {
        var stat : Boolean = true
        if (auto_city.text==null){
            stat = false
            Toast.makeText(this, "Please Select City", Toast.LENGTH_LONG).show()
        } else if (!rdo_airport.isChecked && !rdo_railway.isChecked) {
            stat = false
            Toast.makeText(this, "Please Select Airport/Railway Station", Toast.LENGTH_LONG).show()
        } else if (!rdo_arrival.isChecked && !rdo_departure.isChecked) {
            stat = false
            Toast.makeText(this, "Please Select Arrival/Departure", Toast.LENGTH_LONG).show()
        }
        return  stat
    }

    private fun doThis() {
        /*val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("city",auto_city.text.toString())
        if(rdo_airport.isChecked)
            intent.putExtra("from",FirstActivity.rdo_Airport)
        else
            intent.putExtra("from",FirstActivity.rdo_Railway)
        if(rdo_arrival.isChecked)
            intent.putExtra("to",FirstActivity.rdo_Arrival)
        else
            intent.putExtra("to",FirstActivity.rdo_Departure)
        startActivity(intent)*/
    }

}
