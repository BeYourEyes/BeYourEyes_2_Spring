package com.dna.beyoureyes

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/*
일부 뷰 모델의 앱 전역 공유를 위한 Application 클래스 정의
 */
class App : Application(), ViewModelStoreOwner {
    private val appViewModelStore: ViewModelStore = ViewModelStore()

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}