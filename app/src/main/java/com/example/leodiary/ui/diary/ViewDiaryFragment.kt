package com.example.leodiary.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.leodiary.R
import com.example.leodiary.data.model.Diary
import com.example.leodiary.data.repository.DiaryRepository
import com.example.leodiary.data.database.DiaryDatabase
import com.example.leodiary.databinding.FragmentViewDiaryBinding
import com.example.leodiary.ui.diary.DiaryViewModel.DiaryViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ViewDiaryFragment : Fragment() {

    private lateinit var binding: FragmentViewDiaryBinding
    private lateinit var diaryViewModel: DiaryViewModel
    private var diaryId: Int = -1
    private var currentDiary: Diary? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 获取传入的diaryId
        diaryId = arguments?.getInt("diaryId", -1) ?: -1

        if (diaryId == -1) {
            Toast.makeText(context, "无法加载日记", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_view_diary_fragment_to_navigation_home)
            return
        }

        // 初始化数据库和ViewModel
        val database = DiaryDatabase.getDatabase(requireContext())
        val repository = DiaryRepository(database.diaryDao())
        diaryViewModel = ViewModelProvider(this, DiaryViewModelFactory(repository))[DiaryViewModel::class.java]

        // 加载日记数据
        loadDiaryData()

        // 设置编辑按钮点击事件
        binding.editButton.setOnClickListener {
            currentDiary?.let {\ diary ->
                val bundle = Bundle().apply {
                    putInt("diaryId", diary.id)
                    putString("diaryTitle", diary.title)
                    putString("diaryContent", diary.content)
                    putString("diaryTags", diary.tags)
                    putBoolean("isEditMode", true)
                }
                findNavController().navigate(R.id.action_view_diary_fragment_to_add_diary_fragment, bundle)
            }
        }

        // 设置删除按钮点击事件
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun loadDiaryData() {
        CoroutineScope(Dispatchers.IO).launch {
            val diary = diaryViewModel.getDiaryById(diaryId)
            if (diary != null) {
                currentDiary = diary
                withContext(Dispatchers.Main) {
                    displayDiaryData(diary)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "无法加载日记", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_view_diary_fragment_to_navigation_home)
                }
            }
        }
    }

    private fun displayDiaryData(diary: Diary) {
        // 显示标题
        binding.diaryTitle.text = diary.title

        // 显示日期
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        binding.diaryDate.text = diary.date.format(formatter)

        // 显示内容
        binding.diaryContent.text = diary.content

        // 显示标签
        if (diary.tags != null && diary.tags.isNotEmpty()) {
            binding.diaryTags.text = diary.tags
            binding.tagsContainer.visibility = View.VISIBLE
        } else {
            binding.tagsContainer.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_diary))
            .setMessage(getString(R.string.delete_diary_confirm))
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                currentDiary?.let {
                    diaryViewModel.deleteDiary(it)
                    Toast.makeText(context, "日记已删除", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_view_diary_fragment_to_navigation_home)
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_diary)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}