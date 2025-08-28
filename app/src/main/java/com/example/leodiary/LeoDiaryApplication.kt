package com.example.leodiary

import android.app.Application
import android.content.res.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * 应用程序入口类，用于初始化全局组件
 */
class LeoDiaryApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化ThreeTenABP库，用于处理日期和时间
        AndroidThreeTen.init(this)
        
        // 中国市场特定初始化配置
        initializeChinaMarketConfig()
    }
    
    /**
     * 初始化中国市场特定配置
     */
    private fun initializeChinaMarketConfig() {
        // 这里可以添加针对中国市场的特定初始化代码
        // 例如：处理华为、小米等厂商的特定API
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 监听配置变化，确保在语言切换时正确显示中文
    }
}