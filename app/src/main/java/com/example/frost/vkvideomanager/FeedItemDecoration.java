package com.example.frost.vkvideomanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by frost on 14.03.16.
 */
public class FeedItemDecoration extends RecyclerView.ItemDecoration {

//    private Drawable divider;
//
//    public FeedItemDecoration(Context context, int resId) {
//        divider = ContextCompat.getDrawable(context, resId);
//    }
//
//    @Override
//    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        int left = parent.getPaddingLeft();
//        int right = parent.getWidth() - parent.getPaddingRight();
//
//        int childCount = parent.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = parent.getChildAt(i);
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//
//            int top = child.getBottom() + params.bottomMargin;
//            int bottom = top + divider.getIntrinsicHeight();
//
//            divider.setBounds(left, top, right, bottom);
//            divider.draw(c);
//        }
//    }

    private final int mVerticalSpaceHeight;

    public FeedItemDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mVerticalSpaceHeight;
    }

}
