package net.getatraci.atraci.layouthelpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

	
    public static float radius = 40.0f;  
    RectF rect;
    Path mCanvas;

    public RoundedImageView(Context context) {
        super(context);
        mCanvas = new Path();
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCanvas = new Path();
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCanvas = new Path();
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	rect.right = this.getWidth();
    	rect.bottom = this.getWidth();
    	mCanvas.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(mCanvas);
        super.onDraw(canvas);
    }
    
    
}
