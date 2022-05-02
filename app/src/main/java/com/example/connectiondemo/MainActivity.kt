package com.example.connectiondemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.connectiondemo.databinding.ActivityMainBinding
import kotlin.math.min


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mBtnClick: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        showToolbarTitle(getString(R.string.title_first))


        /*if (Utility.isInternetAvailable(this)) {
            Log.i("TAG", "onCreate: ==>if")
            checkNetworkConnection(
                R.color.white, false, "Connection Established", R.color.darkBlue,
                R.drawable.ic_baseline_public_24
            )
        } else {
            Log.i("TAG", "onCreate: ==>else")
            checkNetworkConnection(
                R.color.black, false, "No Connection", R.color.lightBrown,
                R.drawable.ic_baseline_cloud_off_24
            )
        }*/

        onNetworkCall()
        setVisibility()


        mBtnClick = findViewById(R.id.btnClick)
        mBtnClick.setOnClickListener {
            startActivity(
                Intent(this, SecondActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }

    }



}