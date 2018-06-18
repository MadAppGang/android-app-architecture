package com.madappgang.madappgangmvvmtestarch.utils.timeUtils;

import android.support.v4.util.TimeUtils;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Serhii Chaban sc@madappgang.com on 11.06.18.
 */
public class TimeConverters {
    public static String getTimeFromMillis(Integer millis) {
        long pTime = 0;
        if (millis != null) {
            pTime = millis / 1000;
        }
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }
}
