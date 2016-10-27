package org.gobgob.map2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;


public class ScaledImage {

    Bitmap mImage;
    Paint mPaint = new Paint();

    public PointF position = new PointF(0,0); // absolute position in meters on the map
    public float orientation = 0;
    private PointF dimension;

    public ScaledImage(Bitmap image, float scale)
    {
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(30);
        mPaint.setStyle(Paint.Style.STROKE);
        mImage = image;
        dimension = new PointF(image.getWidth(), image.getHeight());
        rescale(scale);
    }

    public void setPosition(float x, float y)
    {
        position.x = x;
        position.y = y;
    }

    public float radius()
    {
        return dimension.length()/2;
    }

    public void drawImage(Canvas canvas, boolean highlight)
    {
        canvas.save();
        canvas.translate((int)(position.x - dimension.x/2), (int)(position.y - dimension.y/2));
        canvas.rotate(orientation, dimension.x/2, dimension.y/2);
        //Matrix matrix = new Matrix();
        //matrix.setRotate(orientation, dimension.x/2, dimension.y/2);
        //matrix.postTranslate(position.x - dimension.x/2, position.y - dimension.y/2);
        //canvas.translate();
        Paint paint = null;
        if(highlight) {
            paint = mPaint;
            final Rect rect = new Rect(0, 0, mImage.getWidth(), mImage.getHeight());
            RectF rectf = new RectF(rect);

            canvas.drawRoundRect(rectf, 5, 5, paint);
        }
        canvas.drawBitmap(mImage, 0, 0, null);
        canvas.restore();
    }


    public void rescale(float scale)
    {
        mImage = Bitmap.createScaledBitmap(
                mImage,
                (int)(mImage.getWidth()*scale),
                (int)(mImage.getHeight()*scale),
                false);

        dimension.x *= scale;
        dimension.y *= scale;
    }
}
