package edu.doane.ist.camtest8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Custom SurfaceView that draws a 9x9 grid reticle on top of the camera preview image.
 *
 * @author Mark M. Meysenburg
 * @version 11/21/2017
 */
public class MySurfaceView extends SurfaceView {

    /** Width of reticle line. */
    private static final int STROKE_WIDTH_DP = 1;

    /** Paint object describing weight, color, etc. of the reticle lines. */
    private Paint paint;

    // implement all four versions of the constructor; these just call super-class equivalents,
    // then do the class-specific initialization
    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initialize class-specific data.
     */
    private void init() {
        this.setWillNotDraw(false); // <-- required to do custom drawing!

        // set stroke weight and color for the reticle
        int stkW = (int)(STROKE_WIDTH_DP  * getResources().getDisplayMetrics().density);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stkW);
    }

    /**
     * Override of the onDraw() method to draw the reticle over the preview image.
     *
     * @param canvas Canvas object to draw upon
     */
    @Override public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float f = 0.95f; // <-- fraction of width used for the reticle
        float mul = (1.0f - f) / 2.0f;

        // compute corners of bounding rectangle
        float x0 = mul * width;
        float x1 = (mul + f) * width;
        float y0 = mul * height;
        float y1 = y0 + (x1 - x0);

        // draw bounding rectangle
        canvas.drawRect(x0, y0, x1, y1, paint);

        // draw the vertical & horizontal lines
        float delta = (x1 - x0) / 9.0f;
        float x = x0 + delta;
        float y = y0 + delta;
        for(int i = 0; i < 8; i++) {
            canvas.drawLine(x, y0, x, y1, paint);
            canvas.drawLine(x0, y, x1, y, paint);
            x += delta;
            y += delta;
        }
    }
}
