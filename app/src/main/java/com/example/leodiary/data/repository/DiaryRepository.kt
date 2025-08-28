package com.example.leodiary.data.repository

import androidx.lifecycle.LiveData
import com.example.leodiary.data.dao.DiaryDao
import com.example.leodiary.data.model.Diary
import java.time.LocalDate
import java.time.LocalDateTime

class DiaryRepository(private val diaryDao: DiaryDao) {

    // 获取所有日记（按日期降序排列）
    val allDiaries: LiveData<List<Diary>> = diaryDao.getAllDiaries()

    // 获取今天的日记
    fun getTodayDiaries(): LiveData<List<Diary>> {
        val startOfDay = LocalDate.now().atStartOfDay()
        val endOfDay = LocalDate.now().atTime(23, 59, 59)
        return diaryDao.getTodayDiaries(startOfDay, endOfDay)
    }

    // 获取指定日期的日记
    fun getDiariesByDate(date: LocalDate): LiveData<List<Diary>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(23, 59, 59)
        return diaryDao.getDiariesByDate(startOfDay, endOfDay)
    }

    // 获取收藏的日记
    val favoriteDiaries: LiveData<List<Diary>> = diaryDao.getFavoriteDiaries()

    // 通过ID获取日记
    suspend fun getDiaryById(id: Int): Diary? {
        return diaryDao.getDiaryById(id)
    }

    // 插入日记
    suspend fun insert(diary: Diary) {
        diaryDao.insertDiary(diary)
    }

    // 更新日记
    suspend fun update(diary: Diary) {
        diaryDao.updateDiary(diary)
    }

    // 删除日记
    suspend fun delete(diary: Diary) {
        diaryDao.deleteDiary(diary)
    }

    // 删除所有日记
    suspend fun deleteAll() {
        diaryDao.deleteAllDiaries()
    }

    // 搜索日记
    fun searchDiaries(query: String): LiveData<List<Diary>> {
        return diaryDao.searchDiaries("%$query%")
    }

    // 根据标签筛选日记
    fun getDiariesByTag(tag: String): LiveData<List<Diary>> {
        return diaryDao.getDiariesByTag("%$tag%")
    }

    // 获取日记总数
    val totalDiaryCount: LiveData<Int> = diaryDao.getTotalDiaryCount()

    // 获取指定月份的日记数量
    fun getDiaryCountByMonth(year: Int, month: Int): LiveData<Int> {
        val startDate = LocalDate.of(year, month, 1).atStartOfDay()
        val endDate = LocalDate.of(year, month, startDate.toLocalDate().lengthOfMonth()).atTime(23, 59, 59)
        return diaryDao.getDiaryCountByMonth(startDate, endDate)
    }
}