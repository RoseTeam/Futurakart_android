package org.gobgob.map2d;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by GGiraud on 10/27/2016.
 */

public class Goal {

    public PointF position = new PointF();
    public float radius = 50;
    public boolean active = false;
    Paint mPaint = new Paint();

    public Goal()
    {
        init();
    }

    private void init(){
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    public void drawImage(Canvas canvas)
    {
        canvas.drawCircle(position.x, position.y, radius, mPaint);
    }

}
