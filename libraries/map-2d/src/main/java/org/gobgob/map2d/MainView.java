package org.gobgob.map2d;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * This is the main activity
 * displayed while the map is seen by the user. It is also in charge of creating
 * most of the internal components.
 */
public class MainView extends LinearLayout {

	private Map2dView mMapView;

	private RelativeLayout mainLayout;

	public MainView(Context context) {
		super(context);
		initView(context);
	}

	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_activity, this);

		mMapView = (Map2dView) findViewById(R.id.main_view_map_view);

		mainLayout = (RelativeLayout) findViewById(R.id.main_view_main_layout);

	}

	public void switchToMapLayout() {
		mainLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(l, t, r, b);
		}
	}
}
