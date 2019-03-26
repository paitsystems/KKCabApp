package pait.com.kkcabapp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import pait.com.kkcabapp.constant.Constant
import pait.com.kkcabapp.fragments.HistoryFragment
import pait.com.kkcabapp.fragments.HomeFragment
import android.view.WindowManager
import android.os.Build
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.support.design.widget.NavigationView
import pait.com.kkcabapp.fragments.SettingFragment


class MainActivity : AppCompatActivity() {

    private var navItemIndex: Int = 0
    private var navigationListNames: Array<String>? = null
    private var toolbar: Toolbar? = null

    private var mHandler: Handler? = null
    private var CURRENT_TAG: String? = null
    private val TAG_HOME = "Home"
    private val TAG_HISTORY = "History"
    private val TAG_SETTING = "Setting"
    private val TAG_LOGOUT = "logout"
    private var city : String? = null
    private var from : String? = null
    private var to : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        city = intent.getStringExtra("city")
        from = intent.getStringExtra("from")
        to = intent.getStringExtra("to")

        mHandler = Handler()
        navigationListNames = resources.getStringArray(R.array.navigationName)

        setupToolbar()
        setUpNavigationView()
        loadNavHeader()

        if (savedInstanceState == null) {
            navItemIndex = 0
            CURRENT_TAG = TAG_HOME
            loadHomeFragment()
        }
    }

    private fun loadNavHeader() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val header = navigationView.getHeaderView(0)
        val name = header.findViewById<View>(R.id.name) as TextView
        val website = header.findViewById<View>(R.id.website) as TextView
        name.setText("TEST")
        website.setText("TEST")
        val img_header_bg = header.findViewById<View>(R.id.img_header_bg) as ImageView
        val img_profile = header.findViewById<View>(R.id.img_profile) as ImageView

        Picasso.get()
                .load(Constant.bgImgIPAddress)
                .fit()
                .into(img_header_bg)

        var str = "pa.png"
        str = Constant.imgIpaddress+str

        Picasso.get()
                .load(Constant.imgIpaddress)
                .into(img_profile)
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        ab.setDisplayHomeAsUpEnabled(true)
        //toolbar.setBackgroundColor(Color.TRANSPARENT);
        val window = this.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setUpNavigationView() {
        nav_view.setNavigationItemSelectedListener({ menuItem ->
            when (menuItem.getItemId()) {
                R.id.home -> {
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                }
                R.id.history -> {
                    navItemIndex = 1
                    CURRENT_TAG = TAG_HISTORY
                }
                R.id.settings -> {
                    navItemIndex = 2
                    CURRENT_TAG = TAG_SETTING
                }
                else -> navItemIndex = 0
            }
            if (menuItem.isChecked()) {
                menuItem.setChecked(false)
            } else {
                menuItem.setChecked(true)
            }
            menuItem.setChecked(true)
            loadHomeFragment()
            true
        })

        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }

        drawer.setDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun loadHomeFragment() {
        selectNavMenu()

        //setToolbarTitle()
        if (supportFragmentManager.findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers()
            return
        }
        val mPendingRunnable = Runnable {
            if (CURRENT_TAG != TAG_LOGOUT) {
                val fragment = getHomeFragment()
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                fragmentTransaction.replace(R.id.content_frame, fragment, CURRENT_TAG)
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
        if (mPendingRunnable != null) {
            mHandler!!.post(mPendingRunnable)
        }
        drawer.closeDrawers()
        invalidateOptionsMenu()
    }

    private fun getHomeFragment(): Fragment? {
        var fragment: Fragment? = null
        when (navItemIndex) {
            0 -> {
                fragment = HomeFragment.newInstance(city,from,to)
                CURRENT_TAG = TAG_HOME
            }
            1 -> {
                fragment = HistoryFragment()
                CURRENT_TAG = TAG_HISTORY
            }
            2 -> {
                fragment = SettingFragment()
                CURRENT_TAG = TAG_SETTING
            }
            else -> {
            }
        }
        return fragment
    }

    private fun selectNavMenu() {
        nav_view.getMenu().getItem(navItemIndex).setChecked(true)
    }

    private fun setToolbarTitle() {
        supportActionBar!!.setTitle(navigationListNames!!.get(navItemIndex))
    }
}
