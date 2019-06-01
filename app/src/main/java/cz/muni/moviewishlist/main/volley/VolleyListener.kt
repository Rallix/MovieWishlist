package cz.muni.moviewishlist.main.volley

import android.app.Activity
import android.util.Log
import com.android.volley.Response
import org.json.JSONObject

import java.lang.ref.WeakReference

class VolleyListener(activity: Activity, callback: VolleyCallback) : Response.Listener<JSONObject> {

    private val activityWeakReference: WeakReference<Activity> = WeakReference(activity)
    private val callbackWeakReference: WeakReference<VolleyCallback> = WeakReference(callback)

    override fun onResponse(jsonObject: JSONObject) {
        val act = activityWeakReference.get()
        val vc = callbackWeakReference.get()
        if (act != null && vc != null) {
            Log.d("Volley", "$act   $jsonObject")
            // TODO: something you need to do;
            vc.onSuccess(jsonObject)
        }
    }
}