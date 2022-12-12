package kr.ac.kumoh.s20180489.w1501itemclick

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import kr.ac.kumoh.s20180489.w1501itemclick.databinding.ActivityFootballerBinding

class FootballerActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFootballerBinding.inflate(layoutInflater) }
    private lateinit var imageLoader: ImageLoader

    companion object {
        const val KEY_NAME = "FootballerName"
        const val KEY_TEAM = "FootballerTeam"
        const val KEY_IMAGE = "FootballerImage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imageLoader = ImageLoader(
            Volley.newRequestQueue(this),
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(100)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })

        binding.image.setImageUrl(intent.getStringExtra(KEY_IMAGE), imageLoader)
        binding.name.text = intent.getStringExtra(KEY_NAME)
        binding.team.text = intent.getStringExtra(KEY_TEAM)
    }
}