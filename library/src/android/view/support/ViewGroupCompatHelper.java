package android.view.support;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewGroup;

/**
 * Created by Stéphane Guérin on 11/3/13.
 */
public class ViewGroupCompatHelper {
    /**
     * The view's overlay layer. Developers get a reference to the overlay via getOverlay()
     * and add/remove objects to/from the overlay directly through the Overlay methods.
     */
    android.view.support.ViewOverlay mOverlay;
    private Context mContext;

    public ViewGroupCompatHelper(Context context)
    {
        mContext = context;
    }


    /**
     * Returns the ViewGroupOverlay for this view group, creating it if it does
     * not yet exist. In addition to {@link android.view.ViewOverlay}'s support for drawables,
     * {@link android.view.ViewGroupOverlay} allows views to be added to the overlay. These
     * views, like overlay drawables, are visual-only; they do not receive input
     * events and should not be used as anything other than a temporary
     * representation of a view in a parent container, such as might be used
     * by an animation effect.
     *
     * <p>Note: Overlays do not currently work correctly with {@link
     * android.view.SurfaceView} or {@link android.view.TextureView}; contents in overlays for these
     * types of views may not display correctly.</p>
     *
     * @return The ViewGroupOverlay object for this view.
     * @see android.view.ViewGroupOverlay
     */
    public ViewGroupOverlay getCompatOverlay(ViewGroup group) {
        if (mOverlay == null) {
            mOverlay = new ViewGroupOverlay(mContext, group);
            group.setWillNotDraw(false);
        }
        return (ViewGroupOverlay) mOverlay;
    }

    protected void dispatchDraw(Canvas canvas) {
        if (mOverlay != null && !mOverlay.isEmpty()) {
            mOverlay.getOverlayView().invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (mOverlay != null && !mOverlay.isEmpty()) {
            mOverlay.getOverlayView().dispatchDraw(canvas);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mOverlay != null) {
            mOverlay.getOverlayView().setRight(w);
            mOverlay.getOverlayView().setBottom(h);
        }
    }
}
