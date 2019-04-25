package cz.muni.moviewishlist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, DashboardActivity::class.java))

        /* Splash screen
        ic_logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_in)) // 300 ms
        Handler().postDelayed({
            ic_logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_out))
            Handler().postDelayed({
                ic_logo.visibility = View.GONE
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }, 500)
        }, 1500) */
    }
}
