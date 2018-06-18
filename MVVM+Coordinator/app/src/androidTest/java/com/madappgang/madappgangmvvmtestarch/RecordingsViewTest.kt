package com.madappgang.madappgangmvvmtestarch


import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.madappgang.madappgangmvvmtestarch.application.asApp
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.repos.RecordingRepositoryMock
import com.madappgang.madappgangmvvmtestarch.testUtils.childAtPosition
import com.madappgang.madappgangmvvmtestarch.testUtils.waitId
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import java.util.concurrent.TimeUnit

class RecordingsViewTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ContainerActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.WRITE_EXTERNAL_STORAGE")

    @Before
    fun setup() {
        //  MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext.asApp()
        app.overrideModule = Kodein.Module(name = "Override module") {
            bind<RecordingRepository>(overrides = true) with provider { RecordingRepositoryMock(10) }
        }
    }

    @Test
    fun initialtest() {
        runBlocking(context = UI) {
            val activity = mActivityTestRule.activity
            (activity as Coordinator).onSelectFolder(SourceFile("", "", "", false))
        }
        onView(isRoot())
                .perform(waitId(R.id.list_item, TimeUnit.SECONDS.toMillis(50)))
        val textView = onView(
                Matchers.allOf(withId(R.id.title), ViewMatchers.withText("file1.mp4"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list_item),
                                        0),
                                0),
                        ViewMatchers.isDisplayed()))
        textView.check(ViewAssertions.matches(ViewMatchers.withText("file1.mp4")))
    }
}
