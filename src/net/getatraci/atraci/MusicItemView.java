package net.getatraci.atraci;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MusicItemView extends ImageView {

	public MusicItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MusicItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MusicItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }

}
