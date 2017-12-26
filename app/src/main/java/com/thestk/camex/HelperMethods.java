package com.thestk.camex;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

/**
 * Created by Neba on 08-Oct-17.
 */

public class HelperMethods {


    public static final int REQUEST_SUCCESSFUL = 200;

    public static String getStringViewsFromInt(Integer numberOfViews, String context) {
        String returnString;
        if (numberOfViews / 1000000 > 0) {
            Integer number = (int) Math.floor(numberOfViews / 1000000);
            returnString = number.toString() + "M " + context;
        } else if (numberOfViews / 1000 > 0) {
            Integer number = (int) Math.floor(numberOfViews / 1000);
            returnString = number.toString() + "K " + context;
        } else {
            returnString = numberOfViews.toString() + " " + context;

        }
        return returnString;
    }


    /**
     * Rerun animation during update process
     * ideas gotten from https://proandroiddev.com/enter-animation-using-recyclerview-and-layoutanimation-part-1-list-75a874a5d213
     *
     * @param recyclerView
     */

    public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_enter_from_below);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


}
