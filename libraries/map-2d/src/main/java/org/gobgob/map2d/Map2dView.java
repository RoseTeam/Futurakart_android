package org.gobgob.map2d;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;



public class Map2dView extends View implements
		MultiTouchController.MultiTouchObjectCanvas<BaseLayerDrawer> {

	private ScaledImage mBackgroundMap;
	private ScaledImage mMobileBase;
	private boolean mMobileSelected = false;
	private Goal mGoal = new Goal();

	private int mDisplayWidth, mDisplayHeight;
	
	/**
	 * element to handle multitouch drag, zooming and rotation
	 */
	private MultiTouchController<BaseLayerDrawer> mMultiTouchController = new MultiTouchController<>(
			this);
	private float mTouchOriginX;
	private float mTouchOriginY;
	private float mTouchOriginScale;
	private boolean mDoubleTapMode = false;
	GestureDetector detector;


	/**
	 * control attributes
	 */
	private BaseLayerDrawer currentBaseLayer;


	protected float mAngle = 0;


	/**********************************
	 * Listener for lock button disable
	 **********************************/
	private ArrayList<OnMapChangedListener> onMapChangedListeners = new ArrayList<>();
	private ArrayList<OnGoalUpdatedListener> onGoalUpdateListeners = new ArrayList<>();

	public interface OnMapChangedListener {
		void onMapPanned();
		void onMapRotated();
	}

	public interface OnGoalUpdatedListener {
		void onGoalUpdated(float x, float y);
	}

	public void addOnMapChangedListener(OnMapChangedListener listener) {
		this.onMapChangedListeners.add(listener);
	}

	public void addOnGoalUpdated(OnGoalUpdatedListener  listener) {
		this.onGoalUpdateListeners.add(listener);
	}

	private void updateGoal(float x, float y) {
		mGoal.active = true;
		mGoal.position.x = x;
		mGoal.position.y = y;

		for (OnGoalUpdatedListener listener : onGoalUpdateListeners) {
			listener.onGoalUpdated(x,y);
		}
	}

	private void onMapPanned() {
		for (OnMapChangedListener listener : onMapChangedListeners) {
			listener.onMapPanned();
		}
	}

	private void onMapRotated() {
		for (OnMapChangedListener listener : onMapChangedListeners) {
			listener.onMapRotated();
		}
	}

	/**********************************
	 * Listener for orientation button rotation
	 **********************************/
	private ArrayList<RotationOrientationListener> rotationListeners = new ArrayList<>();

	public interface RotationOrientationListener {
		void onRotateOrientationButton(float origin, float angle);
	}

	public void addRotationOrientationListener (RotationOrientationListener listener) {
		this.rotationListeners.add(listener);
	}

	private void onRotateOrientationButton(float origin, float angle) {
		for  (RotationOrientationListener listener : rotationListeners) {
			listener.onRotateOrientationButton(origin, angle);
		}
	}

	private void setAngle(float angle) {
		angle = (float)MathHelper.modulo2PI(angle);
		onRotateOrientationButton((float) (mAngle * 180 / Math.PI), (float) (angle * 180 / Math.PI));
		mAngle = angle;
	}

	/***************
	 * Constructors
	 ***************/
	public Map2dView(Context context) {
		super(context);
		init();
	}

	public Map2dView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Map2dView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){

		
		updateScreenSize();

		// initiate gesture detector
		detector = new GestureDetector(getContext(),
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onDown(MotionEvent e) {

						PointF point = getCanvasTouchedPoint(e);

						if(PointF.length(point.x - mMobileBase.position.x, point.y - mMobileBase.position.y) < mMobileBase.radius())
							mMobileSelected = true;
						else
							mMobileSelected = false;


						return false;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {

						return false;
					}

					@Override
					public boolean onDoubleTap(MotionEvent e) {
						mDoubleTapMode = true;
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
											float distanceX, float distanceY) {

						if(mMobileSelected) {
							float canvasAngle = currentBaseLayer.getAngle();
							float cosa = (float) Math.cos(canvasAngle);
							float sina = (float) Math.sin(canvasAngle);
							float scale = currentBaseLayer.getScale();
							distanceX /= scale;
							distanceY /= scale;
							mMobileBase.position.offset(-distanceX*cosa-distanceY*sina, -distanceY*cosa+distanceX*sina);
						}
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						PointF point = getCanvasTouchedPoint(e);
						Log.d("2dMap", String.format("onLongPress = %f,%f vs %f,%f", point.x, point.y, mMobileBase.position.x, mMobileBase.position.y));

						mMobileSelected = false;

						if(PointF.length(point.x- mGoal.position.x, point.y- mGoal.position.y) < mGoal.radius)
						{
							mGoal.active = !mGoal.active;
							Log.d("2dMap", String.format("Goal activation %b", mGoal.active));
						}
						else
						{
							updateGoal(point.x, point.y);
						}
						//mMobileBase.setPosition(point.x, point.y);
					}
				});

		initOverlays();

		try {
			InputStream stream = getContext().getAssets().open("plan.jpg");
			mBackgroundMap = new ScaledImage(BitmapFactory.decodeStream(stream), 1.0f);
		}
		catch (Exception ignored) {
		}

		try {
			InputStream stream = getContext().getAssets().open("mobile_base.jpg");
			mMobileBase = new ScaledImage(BitmapFactory.decodeStream(stream), .2f);
			mMobileBase.position.set(0, 0);
			mMobileBase.orientation = -45;
		}
		catch (Exception ignored) {
		}


	}

	/*************************
	 * Getters and setters
	 *************************/
	public float getAngle() {
		return mAngle;
	}
	
	public int getmDisplayWidth() {
		return mDisplayWidth;
	}

	public int getmDisplayHeight() {
		return mDisplayHeight;
	}
	
	private void initOverlays(){
		currentBaseLayer = new BaseLayerDrawer(this);
	}


	/**
	 * geometric resources and methods
	 * called in ondraw to ensure the width and the height are right according to orientation
	 */
	private void updateScreenSize() {
		Resources res = getContext().getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		mDisplayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
				metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);

		mDisplayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
				metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
	}

	public BaseLayerDrawer getCurrentBaseLayer() {
		return currentBaseLayer;
	}

	/**************************
	 * Touch events methods
	 **************************/	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			mDoubleTapMode = false;
		}

		detector.onTouchEvent(ev); // to get the double tap
		return mMultiTouchController.onTouchEvent(ev);
	}


	protected PointF getCanvasTouchedPoint(MotionEvent ev){
		return getCanvasTouchedPoint(ev.getX(), ev.getY());
	}

	protected PointF getCanvasTouchedPoint(float x, float y){
		float canvasAngle = currentBaseLayer.getAngle();
		double canvasAngleCos = Math.cos(canvasAngle);
		double canvasAngleSin = Math.sin(canvasAngle);
		
		// We compute the delta from the center point of the screen.
		float deltaX = x - mDisplayWidth / 2;
		float deltaY = y - mDisplayHeight / 2;

		// We reverse the canvas scale
		float scale = currentBaseLayer.getScale();
		deltaX = deltaX / scale;
		deltaY = deltaY / scale;

		// We reverse the canvas rotation
		float deltaXOld = deltaX;
		float deltaYOld = deltaY;
		
		deltaX = (float) (deltaXOld * canvasAngleCos + deltaYOld * canvasAngleSin);
		deltaY = (float) (deltaYOld * canvasAngleCos - deltaXOld * canvasAngleSin);

		// We put the coordinate back into absolute coordinate
		return new PointF(currentBaseLayer.getCenterX() + deltaX,
				currentBaseLayer.getCenterY() + deltaY);
	}
	
	@Override
	public void selectObject(BaseLayerDrawer mapViewDrawer,
			MultiTouchController.PointInfo touchPoint) {
		if (touchPoint.isDown()) {
			currentBaseLayer = mapViewDrawer;
		}
		refresh();
	}

	@Override
	public BaseLayerDrawer getDraggableObjectAtPoint(MultiTouchController.PointInfo pt) {
		return currentBaseLayer;
	}

	@Override
	public void getPositionAndScale(
			BaseLayerDrawer mapViewDrawer,
			MultiTouchController.PositionAndScale objPosAndScaleOut) {
		objPosAndScaleOut.set(0, 0, true, mapViewDrawer.getScale(),
				false, mapViewDrawer.getScale(),
				mapViewDrawer.getScale(), true,
				mapViewDrawer.getAngle());
		mTouchOriginX = mapViewDrawer.getCenterX();
		mTouchOriginY = mapViewDrawer.getCenterY();
		mTouchOriginScale = mapViewDrawer.getScale();

	}
	
	@Override
	public boolean setPositionAndScale(
			BaseLayerDrawer mapViewDrawer,
			MultiTouchController.PositionAndScale newImgPosAndScale,
			MultiTouchController.PointInfo touchPoint)
	{
		if(!mMobileSelected)
		{
			if (touchPoint.isMultiTouch())
			{
					onRotate(newImgPosAndScale.getAngle());
					onScale(newImgPosAndScale.getScale());
					mDoubleTapMode=false;
			}
			else if (mDoubleTapMode) {
				onDoubleTapScale(newImgPosAndScale.getYOff());
			}
			else {
				onPan(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff());
			}
		}
		else
		{
			if (touchPoint.isMultiTouch()) {
				mMobileBase.orientation += newImgPosAndScale.getAngle();
			}
		}
		refresh();
		return true;

	}


	/*************************************
	 * map view transformation methods
	 *************************************/

	public void zoomIn() {
		MapScaleAnimation z = new MapScaleAnimation(currentBaseLayer.getScale(), (float) (currentBaseLayer.getScale() * 1.25));
		z.setDuration(200);
		this.startAnimation(z);
	}

	public void zoomOut() {
		MapScaleAnimation z = new MapScaleAnimation(currentBaseLayer.getScale(), (float) (currentBaseLayer.getScale() * 0.75));
		z.setDuration(200);
		this.startAnimation(z);
	}
	
	public void resetMapRotationToZero(){
		MapRotationAnimation r = new MapRotationAnimation(currentBaseLayer.getAngle(), 0);
		r.setDuration(300);

		this.startAnimation(r);
	}


	/*******************************
	 * Animation classes
	 ******************************/
	
	public class MapRotationAnimation extends RotateAnimation{
		float fromRadians;
		float toRadians;
		public MapRotationAnimation(float fromRadians, float toRadians) {
			super(fromRadians, toRadians, 0, 0);
			this.fromRadians = (float) MathHelper.modulo2PI(fromRadians);
			this.toRadians = (float)MathHelper.modulo2PI(toRadians);
		}
		
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			float angle = (float) ((fromRadians + MathHelper.modulo2PI((toRadians - fromRadians))*interpolatedTime));
			setAngle(angle);
			refresh();
		}
	}
	
	private class MapScaleAnimation extends ScaleAnimation{
		float fromScale;
		float toScale;
		public MapScaleAnimation(float fromScale, float toScale) {
			super(fromScale, toScale, 0, 0);
			this.fromScale = fromScale;
			this.toScale = toScale;
		}
		
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			float scale = (fromScale + (toScale - fromScale)*interpolatedTime);
			currentBaseLayer.setScale(scale);
			refresh();
		}
	}
	
	public class MapTranslateAnimation extends TranslateAnimation{
		float fromXValue;
		float toXValue;
		float fromYValue;
		float toYValue;
		public MapTranslateAnimation(float fromXValue, float toXValue,float fromYValue, float toYValue) {
			super(fromXValue, toXValue, fromYValue, toYValue);
			this.fromXValue = fromXValue;
			this.toXValue = toXValue;
			this.fromYValue = fromYValue;
			this.toYValue = toYValue;
		}
		
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			float centerX = (fromXValue + (toXValue - fromXValue)*interpolatedTime);
			float centerY = (fromYValue + (toYValue - fromYValue)*interpolatedTime);
			
			currentBaseLayer.setCenter(centerX, centerY);
			refresh();
		}
	}

	public void refresh() {
		invalidate();
	}
	
	private void stopAllAnimations(){
		clearAnimation();
	}

	/************************
	 * Draw methods
	 ************************/
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
			updateScreenSize(); // in case of orientation change
			canvas.drawColor(Color.WHITE);
			if (currentBaseLayer != null) {
				canvas.save();
				currentBaseLayer.prepareCanvas(canvas);

				mBackgroundMap.drawImage(canvas, false);

				mMobileBase.drawImage(canvas, mMobileSelected);

				if(mGoal.active)
					mGoal.drawImage(canvas);

				canvas.restore();
			}
	}

	/***********************
	 * gesture handling
	 ***********************/
	private void onDoubleTapScale(float moved){
		float scale= mTouchOriginScale * (1 - moved / 200);
		scale= (float) ((scale<0.1) ? 0.1 : scale);
		onScale(scale);
	}
	
	private void onScale(float scale){
		currentBaseLayer.setScale(scale);
	}

	private void onRotate(float angle){
		setAngle(angle);
	}
	
	private void onPan(float x, float y){
		float scale;
		float angle;
		float offsetX;
		float offsetY;
		scale = currentBaseLayer.getScale();
		angle = currentBaseLayer.getAngle();
		
		offsetX = (float) (x
				* Math.cos((double) angle) + y
				* Math.sin((double) angle));
		offsetY = (float) (-x
				* Math.sin((double) angle) + y
				* Math.cos((double) angle));
		offsetX = offsetX / scale;
		offsetY = offsetY / scale;
		
		float[] XY = new float[2];
		XY[0]= (mTouchOriginX - offsetX);
		XY[1]= (mTouchOriginY - offsetY);
		
		currentBaseLayer.setCenter(XY[0], XY[1]);

	}

}
