package com.madappgang.madappgangmvvmtestarch.utils;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Serhii Chaban sc@madappgang.com on 31.05.18.
 */
public class RecyclerViewBinder {
    @BindingAdapter("singleItemAdapter")
    public static void setAdapter(RecyclerView view, BindableAdapter adapter) {
        try {

            view.setAdapter((BindableAdapter) adapter);
        } catch (Exception e) {
            Log.e("RecyclerViewBinder", e.getMessage());
        }
    }
}


