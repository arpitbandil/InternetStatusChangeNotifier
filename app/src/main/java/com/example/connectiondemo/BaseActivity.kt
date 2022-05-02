package com.example.connectiondemo

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.example.connectiondemo.utils.PrefUtils
import android.view.*
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.RelativeLayout
import android.util.DisplayMetrics
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.schedule


abstract class BaseActivity : AppCompatActivity() {
    private lateinit var tvConnection: TextView
    private lateinit var linearLayout: LinearLayout

    private lateinit var networkMonitor: NetworkUtil
    private lateinit var viewGroup: ViewGroup

    private lateinit var animFadeIn: Animation
    private lateinit var animSlideDown: Animation
    private lateinit var animSlideUp: Animation

    private var TEXT_POSITION_TOP: Int = 0
    private var TEXT_POSITION_BOTTOM: Int = 0

    private lateinit var CONNECTION_TEXT: String
    private lateinit var NO_INTERNET_CONNECTION_TEXT: String

    var isConnect: Boolean = false


    override fun onResume() {
        super.onResume()
        if (this::networkMonitor.isInitialized)
            networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        if (this::networkMonitor.isInitialized)
            networkMonitor.unregister()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CONNECTION_TEXT = resources.getString(R.string.status_online)
        NO_INTERNET_CONNECTION_TEXT = resources.getString(R.string.status_no_connection)
    }


    /**
     * Static method call without changes.
     * check change connection and apply connection.
     */
    protected fun onNetworkCall() {
        networkMonitor = NetworkUtil(this)

        viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup)
            .getChildAt(TEXT_POSITION_TOP) as ViewGroup

        for (index in 0 until viewGroup.childCount) {
            TEXT_POSITION_BOTTOM = index
        }

        val rootParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        /**
         * slide up and down animation for textview
         */
        animFadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in);
        animSlideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        animSlideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

        linearLayout = LinearLayout(this)

        val tParams: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
//        tParams.setMargins(width / 3, 0, 0, 0)
//        tParams.addRule(RelativeLayout.CENTER_IN_PARENT)


        tvConnection = TextView(this)

        networkMonitor.result = { isAvailable, _ ->
            runOnUiThread {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tvConnection.setTextColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    tvConnection.setTextColor(resources.getColor(R.color.white))
                }
                tvConnection.textSize = 10f
                tvConnection.compoundDrawablePadding = 15
                tvConnection.setPadding(0, 7, 0, 0)

                tvConnection.gravity = Gravity.CENTER
//                tvConnection.layoutParams = tParams

                isConnect = isAvailable

                linearLayout.gravity = Gravity.CENTER
                when (isAvailable) {
                    true -> {
                        /**
                         * network is on when online. layout remove
                         */

                        linearLayout.postDelayed({
                            linearLayout.removeView(tvConnection)
                            viewGroup.removeView(linearLayout)
                        },3000)

                        viewGroup.postDelayed({
                            window.statusBarColor =
                                ContextCompat.getColor(applicationContext, R.color.teal_700)
                            tvConnection.text = CONNECTION_TEXT

                            linearLayout.setBackgroundResource(R.color.teal_700)
                            tvConnection.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_baseline_public_24,
                                0,
                                0,
                                0
                            )
                            linearLayout.startAnimation(animFadeIn)
                            linearLayout.addView(tvConnection)
                            viewGroup.addView(linearLayout, TEXT_POSITION_TOP)
//                            viewGroup.layoutParams = rootParams
                        }, 3000)


                        viewGroup.postDelayed(Runnable {
//                            linearLayout.startAnimation(animSlideUp)
                            linearLayout.visibility = View.GONE
//                            if (tvConnection.parent != null) {
//                                viewGroup.removeView(linearLayout)
//                                viewGroup.layoutParams = rootParams
//                            }
                        }, 8000)

                        PrefUtils.setStatus(this, true)

                    }
                    false -> {
                        /**
                         *  Network is not available when always show offline layout
                         */

                        if (!PrefUtils.getStatus(this)) {
                            window.statusBarColor =
                                ContextCompat.getColor(applicationContext, R.color.black)
                            linearLayout.visibility = View.VISIBLE
                            linearLayout.setBackgroundResource(R.color.black)

                            tvConnection.text = NO_INTERNET_CONNECTION_TEXT
                            tvConnection.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_baseline_cloud_off_24,
                                0,
                                0,
                                0
                            )
                            linearLayout.addView(tvConnection)
                            viewGroup.addView(linearLayout, TEXT_POSITION_TOP)
                        } else {
                            linearLayout.removeView(tvConnection)
                            viewGroup.removeView(linearLayout)

                            viewGroup.postDelayed({
                                window.statusBarColor =
                                    ContextCompat.getColor(applicationContext, R.color.black)
                                linearLayout.visibility = View.VISIBLE
                                linearLayout.setBackgroundResource(R.color.black)

                                tvConnection.text = NO_INTERNET_CONNECTION_TEXT
                                tvConnection.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_baseline_cloud_off_24,
                                    0,
                                    0,
                                    0
                                )
                                linearLayout.startAnimation(animSlideDown)
                                linearLayout.addView(tvConnection)
                                viewGroup.addView(linearLayout, TEXT_POSITION_TOP)
                            }, 8000)

                        }
//                        viewGroup.layoutParams = rootParams// temporary comment
                        PrefUtils.setStatus(this, false)


                    }
                }

                /*  linearLayout.addView(tvConnection)
                  viewGroup.postDelayed({
                      viewGroup.addView(linearLayout, TEXT_POSITION_TOP)
                  }, 1200)*/

            }
        }
    }

    protected fun checkCustomNetworkConnection(
        color: Int, isAnim: Boolean,
        connectionText: String, colorBackground: Int, icon: Int
    ) {
        networkMonitor = NetworkUtil(this)

        viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup)
            .getChildAt(TEXT_POSITION_TOP) as ViewGroup

        for (index in 0 until viewGroup.childCount) {
            TEXT_POSITION_BOTTOM = index
        }

        val rootParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        /**
         * slide up and down animation for textview
         */
        animSlideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        animSlideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

        linearLayout = LinearLayout(this)

        val tParams: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        tParams.setMargins(width / 3, 0, 0, 0)
        tParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

        tvConnection = TextView(this)

        networkMonitor.result = { isAvailable, _ ->
            runOnUiThread {
                setTextColor(color)
                tvConnection.textSize = 12f
                tvConnection.compoundDrawablePadding = 15
                tvConnection.setPadding(0, 7, 0, 5)

                tvConnection.layoutParams = tParams
                checkAnimation(isAnim)

                linearLayout.removeView(tvConnection)
                viewGroup.removeView(linearLayout)

                isConnect = isAvailable
                when (isAvailable) {
                    true -> {
                        setConnectionText(connectionText)
                        setBackgroundColorFilter(colorBackground)
                        setIcon(icon)
                        /**
                         * network is on when online. layout remove
                         */
                        tvConnection.postDelayed(Runnable {
                            linearLayout.startAnimation(animSlideUp)
                            tvConnection.visibility = View.GONE
                            if (tvConnection.parent != null) {
                                viewGroup.removeView(linearLayout)
                                viewGroup.layoutParams = rootParams
                            }
                        }, 3000)
                        PrefUtils.setStatus(this, true)
                    }
                    false -> {
                        /**
                         *  Network is not available when always show offline layout
                         */
                        tvConnection.visibility = View.VISIBLE
                        setConnectionText(connectionText)
                        setBackgroundColorFilter(colorBackground)
                        setIcon(icon)
                        PrefUtils.setStatus(this, false)
                        Log.i("NETWORK_MONITOR_STATUS", "No Connection")
                        viewGroup.layoutParams = rootParams
                    }
                }

                linearLayout.addView(tvConnection)
                viewGroup.addView(linearLayout, TEXT_POSITION_TOP)

            }
        }

    }


    /**
     * Sets the Text of the View
     *
     * @param value check to Animation start or stop.
     */
    fun checkAnimation(value: Boolean) {
        try {
            if (value) {
                linearLayout.startAnimation(animSlideDown)
            } else {
                linearLayout.clearAnimation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Sets the Text of the View
     *
     * @param text String object to be used as the View text
     */
    fun setConnectionText(text: String) {
        if (!TextUtils.isEmpty(text)) {
            tvConnection.visibility = View.VISIBLE
            tvConnection.text = text
            /* when (isConnect) {
                 true -> {
                     tvConnection.text = text
                 }
                 false -> {
                     tvConnection.text = text
                 }
             }*/
        }
        /* else {
             when (isConnect) {
                 true -> {
                     tvConnection.text = CONNECTION_TEXT
                 }
                 false -> {
                     tvConnection.text = NO_INTERNET_CONNECTION_TEXT
                 }
             }
         }*/

    }

    /**
     * Sets the Text color of the View
     *
     * @param colorId integer object to be used as the View text color
     */
    fun setTextColor(colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvConnection.setTextColor(ContextCompat.getColor(this, colorId))
        } else {
            tvConnection.setTextColor(resources.getColor(colorId))
        }
    }

    /**
     * Set the inline icon for the Alert
     *
     * @param position using view set Top Or Bottom
     */
    fun setPosition(position: Int) {
        TEXT_POSITION_BOTTOM = position
    }

    /**
     * Set the inline icon for the View
     *
     * @param icon Drawable resource id of the icon to use
     */
    fun setIcon(icon: Int) {
        if (icon != null) {
            tvConnection.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        }
    }

    /**
     * Set the icon color
     *
     * @param color Color int
     */
    fun setBackgroundColorFilter(color: Int) {
        if (color != null) {
            linearLayout.setBackgroundResource(color)
        }
    }

    protected fun setVisibility() {
        if (this::linearLayout.isInitialized) {
            if (PrefUtils.getStatus(this)) {
                linearLayout.visibility = View.GONE
//                if (tvConnection.parent != null) {
//                    viewGroup.removeView(linearLayout)
//                }
            } else {
                linearLayout.visibility = View.VISIBLE
            }
        }
    }

    /**
     * call custom toolbar
     */
    fun showToolbarTitle(title: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val tvTitle = toolbar.findViewById(R.id.toolbarTitle) as TextView
        tvTitle.text = title

//        toolbar.setNavigationOnClickListener {
//            onBackPressed()
//        }
    }


}