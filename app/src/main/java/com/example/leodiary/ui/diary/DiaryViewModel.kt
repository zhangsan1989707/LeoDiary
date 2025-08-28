package com.example.leodiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.leodiary.data.model.Diary
import com.example.leodiary.data.repository.DiaryRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

    // 获取所有日记（实时数据）
    val allDiaries = repository.allDiaries

    // 获取今天的日记（实时数据）
    val todayDiaries = repository.getTodayDiaries()

    // 获取收藏的日记（实时数据）
    val favoriteDiaries = repository.favoriteDiaries

    // 获取日记总数（实时数据）
    val totalDiaryCount = repository.totalDiaryCount

    // 通过ID获取日记
    fun getDiaryById(id: Int) = repository.getDiaryById(id)

    // 插入日记
    fun insertDiary(diary: Diary) = viewModelScope.launch {
        repository.insert(diary)
    }

    // 更新日记
    fun updateDiary(diary: Diary) = viewModelScope.launch {
        repository.update(diary)
    }

    // 删除日记
    fun deleteDiary(diary: Diary) = viewModelScope.launch {
        repository.delete(diary)
    }

    // 删除所有日记
    fun deleteAllDiaries() = viewModelScope.launch {
        repository.deleteAll()
    }

    // 搜索日记
    fun searchDiaries(query: String) = repository.searchDiaries(query)

    // 根据标签筛选日记
    fun getDiariesByTag(tag: String) = repository.getDiariesByTag(tag)

    // 获取指定日期的日记
    fun getDiariesByDate(date: LocalDate) = repository.getDiariesByDate(date)

    // 获取指定月份的日记数量
    fun getDiaryCountByMonth(year: Int, month: Int) = repository.getDiaryCountByMonth(year, month)

    // ViewModel工厂，用于创建ViewModel实例
    class DiaryViewModelFactory(private val repository: DiaryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiaryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}