package com.example.leodiary.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.leodiary.data.model.Diary
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary)

    @Update
    suspend fun updateDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("DELETE FROM diary_table")
    suspend fun deleteAllDiaries()

    // 获取所有日记（按日期降序排列）
    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getAllDiaries(): LiveData<List<Diary>>

    // 获取今天的日记
    @Query("SELECT * FROM diary_table WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date DESC")
    fun getTodayDiaries(startOfDay: LocalDateTime, endOfDay: LocalDateTime): LiveData<List<Diary>>

    // 获取指定日期的日记
    @Query("SELECT * FROM diary_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getDiariesByDate(startDate: LocalDateTime, endDate: LocalDateTime): LiveData<List<Diary>>

    // 通过ID获取日记
    @Query("SELECT * FROM diary_table WHERE id = :diaryId")
    suspend fun getDiaryById(diaryId: Int): Diary?

    // 获取收藏的日记
    @Query("SELECT * FROM diary_table WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavoriteDiaries(): LiveData<List<Diary>>

    // 搜索日记
    @Query("SELECT * FROM diary_table WHERE title LIKE :query OR content LIKE :query OR tags LIKE :query ORDER BY date DESC")
    fun searchDiaries(query: String): LiveData<List<Diary>>

    // 根据标签筛选日记
    @Query("SELECT * FROM diary_table WHERE tags LIKE :tag ORDER BY date DESC")
    fun getDiariesByTag(tag: String): LiveData<List<Diary>>

    // 获取日记总数
    @Query("SELECT COUNT(*) FROM diary_table")
    fun getTotalDiaryCount(): LiveData<Int>

    // 获取指定月份的日记数量
    @Query("SELECT COUNT(*) FROM diary_table WHERE date BETWEEN :startDate AND :endDate")
    fun getDiaryCountByMonth(startDate: LocalDateTime, endDate: LocalDateTime): LiveData<Int>
}