package android.view.support;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Stéphane Guérin on 11/3/13.
 */
public class CompatRelativeLayout extends RelativeLayout implements ViewGroupCompatInterface{

    private ViewGroupCompatHelper mHelper;

    public CompatRelativeLayout(Context context) {
        super(context);
        init();
    }

    public CompatRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompatRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mHelper = new ViewGroupCompatHelper(getContext());
    }

    public ViewGroupOverlay getCompatOverlay() {
        return mHelper.getCompatOverlay(this);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mHelper.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHelper.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHelper.onSizeChanged(w, h, oldw, oldh);
    }
}
