package android.transition.support.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by stephane on 11/10/13.
 */
public class ViewOverlayCompat extends View {
    public ViewOverlayCompat(Context context) {
        super(context);
        init();
    }

    private Paint mPaint;
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public ViewOverlayCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewOverlayCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Bitmap bitmap;
    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    private int left;
    private int top;
    public void setBounds(int left, int top)
    {
        this.left = left;
        this.top = top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, left, top, mPaint);
    }
}
