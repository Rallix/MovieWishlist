package cz.muni.moviewishlist.main.volley

import org.json.JSONObject

interface VolleyCallback {
    fun onSuccess(result: JSONObject)
    fun onFailed(error: String)
}