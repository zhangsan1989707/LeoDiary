package com.example.leodiary.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leodiary.R
import com.example.leodiary.data.model.Diary
import com.example.leodiary.databinding.DiaryItemBinding
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DiaryAdapter(private val onDiaryClick: (Diary) -> Unit) :
    ListAdapter<Diary, DiaryAdapter.DiaryViewHolder>(DIARY_COMPARATOR) {

    companion object {
        private val DIARY_COMPARATOR = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = DiaryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val currentDiary = getItem(position)
        holder.bind(currentDiary)
    }

    inner class DiaryViewHolder(private val binding: DiaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val diary = getItem(position)
                    onDiaryClick(diary)
                }
            }
        }

        fun bind(diary: Diary) {
            // 设置日记标题
            binding.diaryTitle.text = if (diary.title.isNotEmpty()) diary.title else "无标题日记"

            // 设置日记内容（截取前100个字符）
            val content = diary.content.trim()
            binding.diaryContent.text = if (content.length > 100) {
                content.substring(0, 100) + "..."
            } else {
                content
            }

            // 设置日记日期
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            binding.diaryDate.text = diary.date.format(formatter)

            // 设置标签
            if (diary.tags != null && diary.tags.isNotEmpty()) {
                binding.diaryTags.text = diary.tags
                binding.diaryTags.visibility = android.view.View.VISIBLE
            } else {
                binding.diaryTags.visibility = android.view.View.GONE
            }

            // 设置收藏状态
            if (diary.isFavorite) {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_red_24dp)
            } else {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
        }
    }
}