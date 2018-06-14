package com.madappgang.madappgangmvvmtestarch


import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.util.HumanReadables
import android.support.test.espresso.util.TreeIterables
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.madappgang.madappgangmvvmtestarch.application.asApp
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.repos.RecordingRepositoryMock
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@LargeTest
@RunWith(AndroidJUnit4::class)
class RecordingsViewTest {

    @Before
    fun setup() {
        //  MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext.asApp()
        app.overrideModule = Kodein.Module {
            app.resetInjection()
            bind<RecordingRepository>() with provider { RecordingRepositoryMock(10) }
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

    fun waitId(viewId: Int, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "wait for a specific view with id <$viewId> during $millis millis."
            }

            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher = withId(viewId)

                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                // timeout happens
                throw PerformException.Builder()
                        .withActionDescription(description)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(TimeoutException())
                        .build()
            }
        }
    }

    fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
