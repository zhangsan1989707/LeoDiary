package com.example.leodiary.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.leodiary.R
import com.example.leodiary.ui.diary.DiaryViewModel

class ProfileFragment : Fragment() {
    
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var tvDiaryCount: TextView
    private lateinit var cardDiaryStatistics: CardView
    private lateinit var cardSettings: CardView
    private lateinit var cardAbout: CardView
    private lateinit var tvVersion: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        // 初始化UI组件
        tvDiaryCount = view.findViewById(R.id.tv_diary_count)
        cardDiaryStatistics = view.findViewById(R.id.card_diary_statistics)
        cardSettings = view.findViewById(R.id.card_settings)
        cardAbout = view.findViewById(R.id.card_about)
        tvVersion = view.findViewById(R.id.tv_version)
        
        // 初始化ViewModel
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        
        // 加载日记统计数据
        loadDiaryStatistics()
        
        // 设置菜单项点击事件
        setMenuItemClickListeners()
        
        // 设置版本信息
        setVersionInfo()
        
        return view
    }
    
    /**
     * 加载日记统计数据
     */
    private fun loadDiaryStatistics() {
        diaryViewModel.allDiaries.observe(viewLifecycleOwner) {
            val count = it.size
            tvDiaryCount.text = "$count篇日记"
        }
    }
    
    /**
     * 设置菜单项点击事件
     */
    private fun setMenuItemClickListeners() {
        // 日记统计点击事件
        cardDiaryStatistics.setOnClickListener {
            // 这里可以跳转到日记统计页面
            // 目前暂时用Toast提示
            showToast("日记统计功能开发中")
        }
        
        // 设置点击事件
        cardSettings.setOnClickListener {
            // 这里可以跳转到设置页面
            // 目前暂时用Toast提示
            showToast("设置功能开发中")
        }
        
        // 关于点击事件
        cardAbout.setOnClickListener {
            // 这里可以跳转到关于页面
            // 目前暂时用Toast提示
            showToast("关于功能开发中")
        }
    }
    
    /**
     * 设置版本信息
     */
    private fun setVersionInfo() {
        // 获取应用版本号
        val versionName = try {
            requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        
        tvVersion.text = getString(R.string.version, versionName)
    }
    
    /**
     * 显示Toast消息
     */
    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}