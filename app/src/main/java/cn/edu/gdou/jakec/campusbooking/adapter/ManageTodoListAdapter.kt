package cn.edu.gdou.jakec.campusbooking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.edu.gdou.jakec.campusbooking.R
import cn.edu.gdou.jakec.campusbooking.data.ManageTodo
import cn.edu.gdou.jakec.campusbooking.data.Todo
import cn.edu.gdou.jakec.campusbooking.databinding.ListItemManageTodoBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber

class ManageTodoListAdapter internal constructor(
    private val listener: ManageTodoClickListener,
) : ListAdapter<ManageTodo, ManageTodoListAdapter.ViewHolder>(ManageTodoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(val binding: ListItemManageTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ManageTodo, listener: ManageTodoClickListener) {

            binding.nickname.text = item.nickname

            binding.imageView.let {
                Timber.i("Gliding")
                Glide.with(it.context)
                    .load(item.imgUrl)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                    .into(it)
            }

            binding.content.text = when (item.type) {
                Todo.MANAGER_APPLICATION -> context.getString(R.string.applying_to_be_a_manager)
                Todo.STUDY_ROOM_APPLICATION -> context.getString(R.string.applying_to_create_a_new_room)
                else -> ""
            }


            binding.card.setOnClickListener {
                listener.onManagerTodoClicked(item)
            }


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemManageTodoBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    interface ManageTodoClickListener {
        fun onManagerTodoClicked(item: ManageTodo)
    }

}


class ManageTodoDiffCallback : DiffUtil.ItemCallback<ManageTodo>() {

    override fun areItemsTheSame(oldItem: ManageTodo, newItem: ManageTodo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ManageTodo, newItem: ManageTodo): Boolean {
        return oldItem == newItem
    }

}