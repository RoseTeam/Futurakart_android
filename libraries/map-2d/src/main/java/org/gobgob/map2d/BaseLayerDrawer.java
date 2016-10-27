package org.gobgob.map2d;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BaseLayerDrawer {

	protected Map2dView map2dView;
	protected Canvas canvas;
	protected Paint paint;
	protected float mCenterX = 0;
	protected float mCenterY = 0; 
	protected float mScale = 1.0f;



	public void prepareCanvas(Canvas canvas){
		this.canvas = canvas;
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		canvas.translate(-mCenterX + map2dView.getmDisplayWidth() / 2, -mCenterY + map2dView.getmDisplayHeight() / 2);
		canvas.rotate(getAngleInDegrees(), mCenterX, mCenterY);
		canvas.scale(mScale, mScale, mCenterX, mCenterY);
	}

	/*************************
	 *	SETTERS & GETTERS 
	 *************************/
	public BaseLayerDrawer(Map2dView mapView){
		this.map2dView = mapView;
	}
	

	public void setCenter(float centerX, float centerY) {
		mCenterX = centerX;
		mCenterY = centerY;	
	}
	
	public void setScale(float scale) {
		mScale = scale;
	}

	public float getCenterX() {
		return mCenterX;
	}

	public float getCenterY() {
		return mCenterY;
	}

	public float getScale() {
		return mScale;
	}

	public float getAngle() {
		return map2dView.getAngle();
	}
	
	public float getAngleInDegrees(){
		return MathHelper.rad2deg(getAngle());
	}
	
	public void setCanvas(Canvas canvas){
		this.canvas = canvas;
	}
	
	public Canvas getCanvas(){
		return this.canvas;
	}

}
