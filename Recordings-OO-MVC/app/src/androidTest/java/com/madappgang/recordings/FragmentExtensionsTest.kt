/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/15/18.
 */

package com.madappgang.recordings

import android.os.Bundle
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.Fragment
import androidx.core.os.bundleOf
import com.madappgang.recordings.extensions.getArgument
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(AndroidJUnit4::class)
class FragmentExtensionsTest {

    @Test
    fun returned_null_While_check_getArgument_When_value_with_key_is_not_exist_and_default_value_null() {

        val value: String? = Fragment().getArgument("key", null)

        assertNull(value)
    }

    @Test
    fun `returned_expected_nullable_value_While_check_getArgument_When_value_with_key_exist_and_default_value_null`() {
        val key = "key"
        val expectedValue = "expected"
        val fragment = Fragment()
        fragment.arguments = Bundle().apply {
            putString(key, expectedValue)
        }


        val value: String? = fragment.getArgument(key, null)

        assertEquals(expectedValue, value)
    }

    @Test
    fun `returned_expected_nonnull_value_While_check_getArgument_When_value_with_key_exist_and_default_value_null`() {
        val key = "key"
        val expectedValue = "expected"
        val fragment = Fragment()
        fragment.arguments = Bundle().apply {
            putString(key, expectedValue)
        }


        val value: String = fragment.getArgument(key, "defValue")

        assertEquals(expectedValue, value)
    }

    @Test
    fun `returned_default_value_While_check_getArgument_When_value_with_key_is_not_exist_and_default_value_notnull`() {
        val defaultValue = "defValue"

        val value: String = Fragment().getArgument("key", defaultValue)

        assertEquals(defaultValue, value)
    }
}
