package cn.edu.gdou.jakec.campusbooking.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.edu.gdou.jakec.campusbooking.R
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.databinding.ListItemStudyRoomBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber
import java.time.Instant

class StudyRoomListAdapter internal constructor(
    private val listener: StudyRoomClickListener,
) : ListAdapter<StudyRoom, StudyRoomListAdapter.ViewHolder>(StudyRoomDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(val binding: ListItemStudyRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val name: TextView = binding.name
        private val status: TextView = binding.status
        private val rating: TextView = binding.rating
        private val favButton: ImageButton = binding.favButton
        private val manageButton: ImageButton = binding.manageButton
        private val ratingBar: RatingBar = binding.ratingBar
        private val imageView: ImageView = binding.image

        fun bind(item: StudyRoom, listener: StudyRoomClickListener) {

            binding.room = item
            binding.executePendingBindings()

            binding.favButton.isEnabled = true

//            set image
            Timber.i("Gliding")
            Glide.with(imageView.context)
                .load(item.imgUrl)
                .apply(RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
                .into(imageView)

//            Set name
            name.text = item.name

//            Set status
            val now = Instant.now().epochSecond
            lateinit var statusText: String
            if (item.isEnabled) {
                if (item.closeAt > now && item.openAt < now) {
                    statusText = context.getString(R.string.opening) + " (" +
                            item.count.toString() + "/" + item.capacity.toString() + ")"
                    status.setTextColor(context.getColor(R.color.light_green_800))
                } else {
                    statusText = context.getString(R.string.not_in_service)
                    status.setTextColor(context.getColor(R.color.light_blue_800))
                }
            } else {
                statusText = context.getString(R.string.closing)
                status.setTextColor(context.getColor(R.color.red_800))
            }
            status.text = statusText

//            Set rating bar
            ratingBar.rating = item.rate.toFloat()
            when (item.rate.toInt()) {
                0 -> {
                    rating.text = context.getString(R.string.no_comments)
                    rating.setTextColor(context.getColor(R.color.green_900))
                }
                1 -> {
                    rating.text = context.getString(R.string.poor)
                    rating.setTextColor(context.getColor(R.color.lime_900))
                }
                2 -> {
                    rating.text = context.getString(R.string.fair)
                    rating.setTextColor(context.getColor(R.color.yellow_900))
                }
                3 -> {
                    rating.text = context.getString(R.string.good)
                    rating.setTextColor(context.getColor(R.color.amber_900))
                }
                4 -> {
                    rating.text = context.getString(R.string.very_good)
                    rating.setTextColor(context.getColor(R.color.orange_900))
                }
                5 -> {
                    rating.text = context.getString(R.string.excellent)
                    rating.setTextColor(context.getColor(R.color.red_900))
                }
            }

//            Set fav button color
            if (item.favId.isNotEmpty()) {
                favButton.drawable.setTint(Color.argb(255, 244, 143, 177))
            } else {
                favButton.drawable.setTint(Color.argb(255, 158, 158, 158))
            }

//            Set manage button color
            if (item.isManageable) {
                manageButton.visibility = View.VISIBLE
            } else {
                manageButton.visibility = View.INVISIBLE
            }

//            Set onClickListener
            binding.card.setOnClickListener() {
                binding.room?.let { room ->
                    listener.onStudyRoomClicked(room)
                }
            }
            binding.favButton.setOnClickListener() {
                binding.room?.let { room ->
                    it.isEnabled = false
                    listener.onFavButtonClicked(room)
                }
            }
            binding.manageButton.setOnClickListener() {
                binding.room?.let { room ->
                    it.isEnabled = false
                    listener.onManageButtonClicked(room)
                }
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemStudyRoomBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    interface StudyRoomClickListener {
        fun onStudyRoomClicked(room: StudyRoom)
        fun onFavButtonClicked(room: StudyRoom)
        fun onManageButtonClicked(room: StudyRoom)
    }

}

class StudyRoomDiffCallback : DiffUtil.ItemCallback<StudyRoom>() {

    override fun areItemsTheSame(oldItem: StudyRoom, newItem: StudyRoom): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: StudyRoom, newItem: StudyRoom): Boolean {
        return oldItem == newItem
    }

}