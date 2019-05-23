package cz.muni.moviewishlist


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

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

        /*
        val jsonobj = JSONObject()
        test_search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // Get a RequestQueue
                // val queue = IMDb.getInstance(applicationContext).requestQueue
                val url = "$OMDB_API&type=movie&s=${s.toString()}"

                // Add a request (in this example, called stringRequest) to your RequestQueue.
                // IMDb.getInstance(this@MainActivity).addToRequestQueue(stringRequest)

                val queue = Volley.newRequestQueue(this@MainActivity)
                val req = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->
                        Toast.makeText(this@MainActivity, "$response[\"success\"]", Toast.LENGTH_SHORT).show()
                    },
                    Response.ErrorListener {
                        Toast.makeText(this@MainActivity, "Connection failed.", Toast.LENGTH_SHORT).show()
                    })
                queue.add(req)
            }
        })
        */
    }
/*
    override fun onStop() {
        super.onStop()
        queue?.cancelAll(OMDB_TAG) // Cancel pending requests when the activity closes
    }

    fun applyIMDbSuggestions(search: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "$OMDB_API&type=movie&s=$search"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // TODO: Success message
                test_result.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener {
                // TODO: Error message
                test_result.text = it.message
            })
        stringRequest.tag = OMDB_TAG
        queue?.add(stringRequest)
    }
   */
}
