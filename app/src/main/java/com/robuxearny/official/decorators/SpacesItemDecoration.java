/*
 * Created by Fake on 6/29/24, 10:30 AM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 6/29/24, 10:30 AM
 */

package com.robuxearny.official.decorators;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view,
                               RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = space; // Add space below each item

        // Add top space only for the first item to avoid double space at the top
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        }
    }
}