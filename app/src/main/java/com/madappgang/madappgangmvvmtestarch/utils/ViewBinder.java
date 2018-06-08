package com.madappgang.madappgangmvvmtestarch.utils;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by Serhii Chaban sc@madappgang.com on 31.05.18.
 */
public class ViewBinder {
    @BindingAdapter("visible")
    public static void setAdapter(View view, Boolean isVisible) {
        if (isVisible != null && isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}

