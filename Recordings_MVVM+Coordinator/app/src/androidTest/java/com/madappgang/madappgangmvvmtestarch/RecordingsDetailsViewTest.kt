package com.madappgang.madappgangmvvmtestarch


import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.widget.SeekBar
import com.madappgang.madappgangmvvmtestarch.application.asApp
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import com.madappgang.madappgangmvvmtestarch.service.PlayerServiceMock
import com.madappgang.madappgangmvvmtestarch.testUtils.EspressoTestsMatchers
import com.madappgang.madappgangmvvmtestarch.testUtils.waitId
import com.schibsted.spain.barista.interaction.BaristaSeekBarInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import java.util.concurrent.TimeUnit

class RecordingsDetailsViewTest {

    @Before
    fun setup() {
        //  MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext.asApp()
        app.overrideModule = Kodein.Module(name = "Override module") {
            bind<PlayerService>(overrides = true) with provider { PlayerServiceMock() }
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
    fun initialtest() {
        runBlocking(context = UI) {
            (mActivityTestRule.activity as Coordinator).onSelectRecording(SourceFile("", "", "", false))
        }
        onView(isRoot())
                .perform(waitId(R.id.playPauseButton, TimeUnit.SECONDS.toMillis(50)))
        val button = onView(ViewMatchers.withId(R.id.playPauseButton))
        sleep(1000)
        button.check(ViewAssertions.matches(EspressoTestsMatchers.withDrawable(R.drawable.ic_pause_circle_filled_black_24dp)))
        button.perform(ViewActions.click())
        button.check(ViewAssertions.matches(EspressoTestsMatchers.withDrawable(R.drawable.ic_play_circle_filled_black_24dp)))
        sleep(1000)
        BaristaSeekBarInteractions.setProgressTo(R.id.seekBar, 30000)
        sleep(1000)

    }
}
