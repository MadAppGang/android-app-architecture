package com.madappgang.madappgangmvvmtestarch.application

import android.app.Application
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepositoryImpl
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingUseCase
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingsUseCase
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.android.UI
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

/**
 * Created by Serhii Chaban sc@madappgang.com on 30.05.18.
 */
val coroutines by lazy {
    Kodein.Module {
        bind<CoroutineDispatcher>("uiContext") with provider { UI }
        bind<CoroutineDispatcher>("bgContext") with provider { CommonPool }
    }
}
val providers = Kodein.Module {
    bind<RecordingRepository>() with provider { RecordingRepositoryImpl() }
}

class AppInstance : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidModule(this@AppInstance))
        import(coroutines)
        import(providers)

        bind<PlayerService>() with provider { PlayerService() }
        bind<GetRecordingsUseCase>() with provider { GetRecordingsUseCase(instance()) }
        bind<GetRecordingUseCase>() with provider { GetRecordingUseCase(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
    }
}