package net.getatraci.atraci.layouthelpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AlbumArtImage extends ImageView {

	public AlbumArtImage(Context context) {
		super(context);
	}

	public AlbumArtImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlbumArtImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }

}