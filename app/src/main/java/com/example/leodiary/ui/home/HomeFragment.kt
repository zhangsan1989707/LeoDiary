package com.example.leodiary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leodiary.R
import com.example.leodiary.data.model.Diary
import com.example.leodiary.data.repository.DiaryRepository
import com.example.leodiary.data.database.DiaryDatabase
import com.example.leodiary.databinding.FragmentHomeBinding
import com.example.leodiary.ui.diary.DiaryViewModel
import com.example.leodiary.ui.diary.DiaryViewModel.DiaryViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var todayDiaryAdapter: DiaryAdapter
    private lateinit var recentDiaryAdapter: DiaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化数据库和ViewModel
        val database = DiaryDatabase.getDatabase(requireContext())
        val repository = DiaryRepository(database.diaryDao())
        diaryViewModel = ViewModelProvider(this, DiaryViewModelFactory(repository))[DiaryViewModel::class.java]

        // 初始化适配器
        initAdapters()

        // 加载今天的日记
        loadTodayDiaries()

        // 加载最近的日记（不包括今天）
        loadRecentDiaries()

        // 设置搜索功能
        setupSearch()

        // 设置添加日记按钮点击事件
        binding.addDiaryFab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_add_diary_fragment)
        }
    }

    private fun initAdapters() {
        // 今天的日记适配器
        todayDiaryAdapter = DiaryAdapter {
            navigateToViewDiary(it)
        }
        binding.todayDiaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = todayDiaryAdapter
        }

        // 最近的日记适配器
        recentDiaryAdapter = DiaryAdapter {
            navigateToViewDiary(it)
        }
        binding.recentDiaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = recentDiaryAdapter
        }
    }

    private fun loadTodayDiaries() {
        diaryViewModel.todayDiaries.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                // 如果今天没有日记，添加一条示例数据
                addSampleDiary()
            } else {
                todayDiaryAdapter.submitList(it)
            }
        })
    }

    private fun loadRecentDiaries() {
        diaryViewModel.allDiaries.observe(viewLifecycleOwner, Observer {
            // 过滤掉今天的日记
            val today = LocalDate.now()
            val recentDiaries = it.filter { diary ->
                diary.date.toLocalDate() != today
            }
            recentDiaryAdapter.submitList(recentDiaries)
        })
    }

    private fun setupSearch() {
        binding.searchEditText.setOnEditorActionListener {\ _, _, _ ->
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchDiaries(query)
            } else {
                // 搜索框为空时，显示所有日记
                loadTodayDiaries()
                loadRecentDiaries()
            }
            true
        }
    }

    private fun searchDiaries(query: String) {
        diaryViewModel.searchDiaries(query).observe(viewLifecycleOwner, Observer {
            // 清空当前列表
            todayDiaryAdapter.submitList(emptyList())
            recentDiaryAdapter.submitList(emptyList())

            if (it.isEmpty()) {
                Toast.makeText(context, "没有找到匹配的日记", Toast.LENGTH_SHORT).show()
            } else {
                // 将搜索结果显示在最近日记列表中
                recentDiaryAdapter.submitList(it)
                // 隐藏今天日记标题和列表
                binding.todayDiaryTitle.visibility = View.GONE
                binding.todayDiaryRecyclerView.visibility = View.GONE
            }
        })
    }

    private fun navigateToViewDiary(diary: Diary) {
        val bundle = Bundle().apply {
            putInt("diaryId", diary.id)
        }
        findNavController().navigate(R.id.action_navigation_home_to_view_diary_fragment, bundle)
    }

    // 添加示例日记
    private fun addSampleDiary() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val sampleDiary = Diary(
            title = "欢迎使用LeoDiary",
            content = "这是您的第一篇示例日记。点击右下角的加号按钮开始记录您的每一天吧！\n\n您可以添加文字、标签，还可以将重要的日记设为收藏。",
            date = LocalDateTime.now(),
            tags = "示例, 欢迎",
            isFavorite = false
        )
        diaryViewModel.insertDiary(sampleDiary)
    }
}