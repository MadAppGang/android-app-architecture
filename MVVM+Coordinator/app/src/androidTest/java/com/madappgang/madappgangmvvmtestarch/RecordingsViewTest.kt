package com.madappgang.madappgangmvvmtestarch


import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepositoryImpl
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Unconfined
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider

@LargeTest
@RunWith(AndroidJUnit4::class)
class RecordingsViewTest {
    lateinit var kodein: Kodein
    @Before
    fun setup() {
        //  MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.asApp()
        app.kodein.kodein.kodein.kodein
        kodein = Kodein(allowSilentOverride = true) {
            extend(app.kodein)
            bind<CoroutineDispatcher>("uiContext", overrides = true) with provider { Unconfined }
            bind<CoroutineDispatcher>("bgContext", overrides = true) with provider { Unconfined }
            bind<RecordingRepository>(overrides = true) with provider { RecordingRepositoryImpl() }
        }
    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ContainerActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.WRITE_EXTERNAL_STORAGE")

    @Test
    fun containerActivityTest() {

    }

}
