package com.example.leodiary.ui.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.leodiary.R
import com.example.leodiary.data.model.Diary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarAdapter(
    private val context: Context,
    private val calendarDays: List<CalendarDay>,
    private val diaryDates: Map<String, Diary>,
    private val currentMonth: Int,
    private val today: LocalDate
) : BaseAdapter() {
    
    private val diaryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var onDateClickListener: ((LocalDate) -> Unit)? = null
    
    /**
     * 设置日期点击监听器
     */
    fun setOnDateClickListener(listener: (LocalDate) -> Unit) {
        onDateClickListener = listener
    }
    
    override fun getCount(): Int {
        return calendarDays.size
    }
    
    override fun getItem(position: Int): Any {
        return calendarDays[position]
    }
    
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.calendar_day_item, parent, false)
        
        val calendarDay = calendarDays[position]
        val date = calendarDay.date
        val isCurrentMonth = calendarDay.isCurrentMonth
        
        // 找到UI组件
        val tvDay = view.findViewById<TextView>(R.id.tv_day)
        val cardDay = view.findViewById<CardView>(R.id.card_day)
        val indicatorHasDiary = view.findViewById<View>(R.id.indicator_has_diary)
        
        // 设置日期文本
        tvDay.text = date.dayOfMonth.toString()
        
        // 设置日期样式
        if (isCurrentMonth) {
            tvDay.setTextColor(context.resources.getColor(R.color.colorTextPrimary, null))
        } else {
            tvDay.setTextColor(context.resources.getColor(R.color.colorTextSecondary, null))
            tvDay.alpha = 0.5f
        }
        
        // 检查是否是今天
        if (date == today) {
            cardDay.setCardBackgroundColor(context.resources.getColor(R.color.colorAccent, null))
            tvDay.setTextColor(context.resources.getColor(android.R.color.white, null))
        } else {
            cardDay.setCardBackgroundColor(context.resources.getColor(android.R.color.transparent, null))
        }
        
        // 检查是否有日记
        val dateKey = diaryDateFormatter.format(date)
        if (diaryDates.containsKey(dateKey)) {
            indicatorHasDiary.visibility = View.VISIBLE
        } else {
            indicatorHasDiary.visibility = View.GONE
        }
        
        // 设置点击事件
        view.setOnClickListener {
            onDateClickListener?.invoke(date)
        }
        
        return view
    }
}

/**
 * 日历日期项数据类
 */
class CalendarDay(val date: LocalDate, val isCurrentMonth: Boolean)