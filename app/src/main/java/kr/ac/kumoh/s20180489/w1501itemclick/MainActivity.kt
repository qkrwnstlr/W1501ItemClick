package kr.ac.kumoh.s20180489.w1501itemclick

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180489.w1501itemclick.databinding.ActivityMainBinding
import kr.ac.kumoh.s20180489.w1501itemclick.databinding.ItemFootballerBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val model by lazy { ViewModelProvider(this)[FootballerViewModel::class.java] }
    private val footballerAdapter by lazy { FootballerAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.list.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = footballerAdapter
        }

        model.list.observe(this) {
            footballerAdapter.notifyItemRangeInserted(0, footballerAdapter.itemCount)
        }

        model.requestFootballer()
    }

    inner class FootballerAdapter : RecyclerView.Adapter<FootballerAdapter.ViewHolder>() {
        inner class ViewHolder(binding: ItemFootballerBinding) :
            RecyclerView.ViewHolder(binding.root), View.OnClickListener {
            val item: CardView = binding.item
            val txName: TextView = binding.name
            val txTeam:TextView = binding.team
            val niImage: NetworkImageView = binding.image

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                item.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val intent = Intent(application, FootballerActivity::class.java)
                intent.putExtra(FootballerActivity.KEY_NAME, model.list.value?.get(adapterPosition)?.name)
                intent.putExtra(FootballerActivity.KEY_TEAM, model.list.value?.get(adapterPosition)?.team)
                intent.putExtra(FootballerActivity.KEY_IMAGE, model.getImageUrl(adapterPosition))
                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemFootballerBinding.inflate(LayoutInflater.from(parent.context))
            binding.root.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txName.text = model.list.value?.get(position)?.name ?: ""
            holder.txTeam.text = model.list.value?.get(position)?.team ?: ""
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }

        override fun getItemCount() = model.list.value?.size ?: 0
    }
}