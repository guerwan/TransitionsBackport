package android.transition.support.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by stephane on 11/10/13.
 */
public class OverlayCompatibilityHelper {

    public static void addViewOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY)
    {
        int[] loc = new int[2];
        sceneRoot.getLocationOnScreen(loc);
        overlayView.offsetLeftAndRight((screenX - loc[0]) - overlayView.getLeft());
        overlayView.offsetTopAndBottom((screenY - loc[1]) - overlayView.getTop());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            sceneRoot.getOverlay().add(overlayView);
        else
        {
            //TODO ViewOverlay
            if(sceneRoot instanceof FrameLayout)
            {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(overlayView.getLayoutParams());
                params.leftMargin = (screenX - loc[0]) - overlayView.getLeft();
                params.topMargin = (screenY - loc[1]) - overlayView.getTop();
                ((FrameLayout)sceneRoot).addView(overlayView, params);
            }
        }
    }

    public static void removeViewOverlay(ViewGroup finalSceneRoot, View finalOverlayView)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            if (finalOverlayView != null) {
                finalSceneRoot.getOverlay().remove(finalOverlayView);
            }
        }
        else
        {
            //TODO ViewOverlay
        }
    }

    public static void removeViewOverlay(ViewGroup sceneRoot, Drawable drawable)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            sceneRoot.getOverlay().remove(drawable);
        }
        else
        {
            //TODO ViewOverlay
        }
    }

    public static void addViewOverlay(ViewGroup sceneRoot, Drawable drawable)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            sceneRoot.getOverlay().add(drawable);
        }
        else
        {
            //TODO ViewOverlay
        }
    }
}
