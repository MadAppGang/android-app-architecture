/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/15/18.
 */

package com.madappgang.recordings.formatters

import org.joda.time.Duration

internal class TimeFormatter {

    fun formatMilliseconds(milliseconds: Int): String {
        val duration = Duration.millis(milliseconds.toLong())
        val hours = duration.toStandardHours()
        val minutes = duration.toStandardMinutes().minus(hours.toStandardMinutes())
        val seconds = duration.toStandardSeconds().minus(minutes.toStandardSeconds())

        return String.format("%02d:%02d:%02d", hours.hours, minutes.minutes, seconds.seconds)
    }
}