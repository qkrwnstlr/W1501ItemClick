package kr.ac.kumoh.s20180489.w1501itemclick

import android.app.Application
import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class FootballerViewModel(application: Application) : AndroidViewModel(application) {
    data class Footballer(var name: String, var team: String, var image: String)

    companion object {
        const val SERVER_URL = "https://expresssongdb-vhkim.run.goorm.io"
        const val QUEUE_TAG = "FootballerVolleyRequest"
    }

    private val footballers = ArrayList<Footballer>()
    private val _list = MutableLiveData<ArrayList<Footballer>>()
    val list: LiveData<ArrayList<Footballer>>
        get() = _list

    private lateinit var queue: RequestQueue
    val imageLoader: ImageLoader


    init {
        _list.value = footballers
        queue = Volley.newRequestQueue(getApplication())

        imageLoader = ImageLoader(queue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(100)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(footballers[i].image, "utf-8")

    fun requestFootballer() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/footballer",
            null,
            {
                footballers.clear()
                fromJson(it)
                _list.value = footballers
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        queue.add(request)
    }

    private fun fromJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item = items[i] as JSONObject
            val name = item.getString("name")
            val team = item.getString("team")
            val image = item.getString("image")
            footballers.add(Footballer(name, team, image))
        }
    }

    override fun onCleared() {
        super.onCleared()
        queue.cancelAll(QUEUE_TAG)
    }
}