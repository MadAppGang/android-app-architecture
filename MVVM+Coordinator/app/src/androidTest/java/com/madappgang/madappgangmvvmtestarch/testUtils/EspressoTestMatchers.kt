package com.madappgang.madappgangmvvmtestarch.testUtils

/**
 * Created by Serhii Chaban sc@madappgang.com on 15.06.18.
 */


import android.view.View

import org.hamcrest.Matcher

object EspressoTestsMatchers {

    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View> {
        return DrawableMatcher(DrawableMatcher.EMPTY)
    }

    fun hasDrawable(): Matcher<View> {
        return DrawableMatcher(DrawableMatcher.ANY)
    }
}
