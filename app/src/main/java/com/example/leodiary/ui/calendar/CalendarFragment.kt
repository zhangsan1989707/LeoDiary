package com.example.leodiary.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.leodiary.R
import com.example.leodiary.data.model.Diary
import com.example.leodiary.ui.diary.DiaryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarFragment : Fragment() {
    
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var gridView: GridView
    private lateinit var tvCurrentMonth: TextView
    private lateinit var btnPreviousMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var llDiaryPreview: LinearLayout
    private lateinit var tvDiaryPreviewTitle: TextView
    private lateinit var tvDiaryPreviewContent: TextView
    
    private var currentDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月")
    private val diaryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    // 保存有日记的日期
    private val diaryDates = mutableMapOf<String, Diary>()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        
        // 初始化UI组件
        gridView = view.findViewById(R.id.grid_calendar)
        tvCurrentMonth = view.findViewById(R.id.tv_current_month)
        btnPreviousMonth = view.findViewById(R.id.btn_previous_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)
        llDiaryPreview = view.findViewById(R.id.ll_diary_preview)
        tvDiaryPreviewTitle = view.findViewById(R.id.tv_diary_preview_title)
        tvDiaryPreviewContent = view.findViewById(R.id.tv_diary_preview_content)
        
        // 初始化ViewModel
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        
        // 加载所有日记数据
        loadDiaryData()
        
        // 设置月份按钮点击事件
        btnPreviousMonth.setOnClickListener {
            currentDate = currentDate.minusMonths(1)
            updateCalendar()
        }
        
        btnNextMonth.setOnClickListener {
            currentDate = currentDate.plusMonths(1)
            updateCalendar()
        }
        
        // 初始化日历
        updateCalendar()
        
        return view
    }
    
    /**
     * 从ViewModel加载所有日记数据
     */
    private fun loadDiaryData() {
        diaryViewModel.allDiaries.observe(viewLifecycleOwner) {
            diaryDates.clear()
            it.forEach {\ diary ->
                // 将日记日期转换为yyyy-MM-dd格式并存储
                val dateKey = diaryDateFormatter.format(diary.date)
                diaryDates[dateKey] = diary
            }
            updateCalendar()
        }
    }
    
    /**
     * 更新日历显示
     */
    private fun updateCalendar() {
        // 更新当前月份标题
        tvCurrentMonth.text = dateFormatter.format(currentDate)
        
        // 生成日历数据
        val calendarData = generateCalendarData(currentDate)
        
        // 创建并设置适配器
        val calendarAdapter = CalendarAdapter(
            requireContext(), 
            calendarData,
            diaryDates,
            currentDate.month.value,
            LocalDate.now()
        )
        
        // 设置日期点击监听器
        calendarAdapter.setOnDateClickListener {
            val dateKey = diaryDateFormatter.format(it)
            diaryDates[dateKey]?.let {\ diary ->
                // 如果有日记，显示预览
                showDiaryPreview(diary)
                // 点击预览跳转到日记详情
                llDiaryPreview.setOnClickListener {
                    val action = CalendarFragmentDirections.actionNavigationCalendarToViewDiaryFragment(diary.id)
                    view?.findNavController()?.navigate(action)
                }
            } ?: run {
                // 如果没有日记，隐藏预览
                llDiaryPreview.visibility = View.GONE
            }
        }
        
        gridView.adapter = calendarAdapter
    }
    
    /**
     * 生成日历数据
     */
    private fun generateCalendarData(date: LocalDate): List<CalendarDay> {
        val calendarDays = mutableListOf<CalendarDay>()
        
        // 获取月份第一天
        val firstDayOfMonth = date.withDayOfMonth(1)
        // 获取月份最后一天
        val lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        
        // 获取月份第一天是星期几 (1-7, 1是星期一)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        // 调整为(0-6, 0是星期日)
        val adjustedFirstDayOfWeek = if (firstDayOfWeek == 7) 0 else firstDayOfWeek
        
        // 添加上月的日期
        for (i in adjustedFirstDayOfWeek - 1 downTo 0) {
            val day = firstDayOfMonth.minusDays((i + 1).toLong())
            calendarDays.add(CalendarDay(day, false))
        }
        
        // 添加当月的日期
        for (i in 1..lastDayOfMonth.dayOfMonth) {
            val day = date.withDayOfMonth(i)
            calendarDays.add(CalendarDay(day, true))
        }
        
        // 添加下月的日期，补齐到42个格子(6行7列)
        val remainingDays = 42 - calendarDays.size
        for (i in 1..remainingDays) {
            val day = lastDayOfMonth.plusDays(i.toLong())
            calendarDays.add(CalendarDay(day, false))
        }
        
        return calendarDays
    }
    
    /**
     * 显示日记预览
     */
    private fun showDiaryPreview(diary: Diary) {
        llDiaryPreview.visibility = View.VISIBLE
        tvDiaryPreviewTitle.text = diary.title.ifEmpty { getString(R.string.no_diary_title) }
        tvDiaryPreviewContent.text = diary.content
    }
}