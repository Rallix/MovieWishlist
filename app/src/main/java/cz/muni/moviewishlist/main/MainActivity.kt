package cz.muni.moviewishlist.main


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.categories.CategoryActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Proc teda vubec existuje tahle aktivita, kdyz vubec nic nedela a pouze nastartuje dalsi aktivitu? Neni potreba.
        startActivity(Intent(this, CategoryActivity::class.java))
    }
    // Je zbytecne mit tady zakomentovany kod. V pripade potreby ho mas v historii na gitu
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
