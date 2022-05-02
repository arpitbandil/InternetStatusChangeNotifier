package com.example.connectiondemo

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.example.connectiondemo.databinding.ActivitySecondBinding
import com.example.connectiondemo.utils.PrefUtils

class SecondActivity: BaseActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_second)
        showToolbarTitle(getString(R.string.title_second))


        onNetworkCall()
        setVisibility()

    }

}