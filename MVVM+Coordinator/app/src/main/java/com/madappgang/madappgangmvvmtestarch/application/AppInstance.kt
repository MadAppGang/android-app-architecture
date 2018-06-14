package com.madappgang.madappgangmvvmtestarch.application

import android.app.Application
import android.content.Context
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
import org.kodein.di.conf.ConfigurableKodein
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
    bind<RecordingRepository>() with provider { RecordingRepositoryMock(10) }
}

class AppInstance : Application(), KodeinAware {
    override val kodein = ConfigurableKodein(mutable = true)
    var overrideModule: Kodein.Module? = null

    override fun onCreate() {
        super.onCreate()
        resetInjection()
    }


    fun addModule(activityModules: Kodein.Module) {
        kodein.addImport(activityModules, true)
        if (overrideModule != null) {
            kodein.addImport(overrideModule!!, true)
        }
    }

    fun resetInjection() {
        kodein.clear()
        kodein.addImport(appDependencies(), true)
        if (overrideModule != null) {
            kodein.addImport(overrideModule!!, true)
        }
    }

    private fun appDependencies(): Kodein.Module {
        return Kodein.Module(allowSilentOverride = true) {
            import(coroutines)
            import(providers)
            bind<PlayerService>() with provider { PlayerService() }
            bind<GetRecordingsUseCase>() with provider { GetRecordingsUseCase(instance()) }
            bind<GetRecordingUseCase>() with provider { GetRecordingUseCase(instance()) }
        }
    }


}
fun Context.asApp() = this as AppInstance
