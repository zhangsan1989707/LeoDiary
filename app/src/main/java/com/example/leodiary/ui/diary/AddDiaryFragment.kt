package com.example.leodiary.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.leodiary.R
import com.example.leodiary.data.model.Diary
import com.example.leodiary.data.repository.DiaryRepository
import com.example.leodiary.data.database.DiaryDatabase
import com.example.leodiary.databinding.FragmentAddDiaryBinding
import com.example.leodiary.ui.diary.DiaryViewModel.DiaryViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AddDiaryFragment : Fragment() {

    private lateinit var binding: FragmentAddDiaryBinding
    private lateinit var diaryViewModel: DiaryViewModel
    private var isEditMode = false
    private var diaryId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化数据库和ViewModel
        val database = DiaryDatabase.getDatabase(requireContext())
        val repository = DiaryRepository(database.diaryDao())
        diaryViewModel = ViewModelProvider(this, DiaryViewModelFactory(repository))[DiaryViewModel::class.java]

        // 检查是否是编辑模式
        isEditMode = arguments?.getBoolean("isEditMode", false) ?: false
        if (isEditMode) {
            diaryId = arguments?.getInt("diaryId", -1) ?: -1
            val title = arguments?.getString("diaryTitle", "") ?: ""
            val content = arguments?.getString("diaryContent", "") ?: ""
            val tags = arguments?.getString("diaryTags", "") ?: ""

            // 填充编辑界面
            binding.titleEditText.setText(title)
            binding.contentEditText.setText(content)
            if (!tags.isNullOrEmpty()) {
                binding.tagsEditText.setText(tags)
            }
        }

        // 设置保存按钮点击事件
        binding.saveButton.setOnClickListener {
            if (isEditMode) {
                updateDiary()
            } else {
                saveDiary()
            }
        }

        // 设置取消按钮点击事件
        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_add_diary_fragment_to_navigation_home)
        }

        // 自动聚焦到内容输入框
        binding.contentEditText.requestFocus()
    }

    private fun saveDiary() {
        val title = binding.titleEditText.text.toString().trim()
        val content = binding.contentEditText.text.toString().trim()
        val tags = binding.tagsEditText.text.toString().trim()

        // 验证输入
        if (content.isEmpty()) {
            Toast.makeText(context, "日记内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 创建日记对象
        val diary = Diary(
            title = if (title.isEmpty()) "无标题日记" else title,
            content = content,
            date = LocalDateTime.now(),
            tags = if (tags.isEmpty()) null else tags,
            isFavorite = false
        )

        // 保存日记到数据库
        diaryViewModel.insertDiary(diary)

        // 显示保存成功提示
        Toast.makeText(context, "日记保存成功", Toast.LENGTH_SHORT).show()

        // 返回首页
        findNavController().navigate(R.id.action_add_diary_fragment_to_navigation_home)
    }

    private fun updateDiary() {
        val title = binding.titleEditText.text.toString().trim()
        val content = binding.contentEditText.text.toString().trim()
        val tags = binding.tagsEditText.text.toString().trim()

        // 验证输入
        if (content.isEmpty()) {
            Toast.makeText(context, "日记内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 获取当前日记并更新
        CoroutineScope(Dispatchers.IO).launch {
            val diary = diaryViewModel.getDiaryById(diaryId)
            if (diary != null) {
                val updatedDiary = diary.copy(
                    title = if (title.isEmpty()) "无标题日记" else title,
                    content = content,
                    tags = if (tags.isEmpty()) null else tags,
                    date = LocalDateTime.now() // 更新时间为当前时间
                )
                diaryViewModel.updateDiary(updatedDiary)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "日记更新成功", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_add_diary_fragment_to_navigation_home)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "无法更新日记", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}