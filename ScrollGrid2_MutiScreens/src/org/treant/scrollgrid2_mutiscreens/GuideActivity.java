package org.treant.scrollgrid2_mutiscreens;

import org.treant.scrollgrid2_mutiscreens.util.AnimationListenerImpl;
import org.treant.scrollgrid2_mutiscreens.util.Configure;
import org.treant.scrollgrid2_mutiscreens.util.OnViewChangeListener;
import org.treant.scrollgrid2_mutiscreens.util.Utils;
import org.treant.scrollgrid2_mutiscreens.widgets.ScrollGuideLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GuideActivity extends Activity implements OnClickListener, OnViewChangeListener {
	
	private ScrollGuideLayout guideScroll;
	private ImageView[] pointsArray;
	private int count;
	private int mCurItem;
	private RelativeLayout guideMain;
	private LinearLayout pointsLayout;
	private LinearLayout leftAnimLayout;
	private LinearLayout rightAnimLayout;
	private LinearLayout animOpenLayout;
	private CheckBox checkGuide;
	private Button startBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_guide);
		initsWidgets();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int [] guideImagesId={R.drawable.help_1, R.drawable.help_neirong, R.drawable.help_news, 
				R.drawable.help_shouye, R.drawable.help_weibo, R.drawable.loginback};
		int guideImageCount=guideScroll.getChildCount();
		for(int i=0;i<guideImageCount;i++){
			Utils.getOptBitmapById(guideScroll.getChildAt(i),getResources(), guideImagesId[i],
						Configure.screenWidth, Configure.screenHeight);
		}
	}
	
	private void initsWidgets(){
		guideScroll=(ScrollGuideLayout)findViewById(R.id.scrollGuide);
		guideMain=(RelativeLayout)findViewById(R.id.guideMain);
		pointsLayout=(LinearLayout)findViewById(R.id.pointsLayout);
		leftAnimLayout=(LinearLayout)findViewById(R.id.leftAnim);
		rightAnimLayout=(LinearLayout)findViewById(R.id.rightAnim);
		animOpenLayout=(LinearLayout)findViewById(R.id.animOpen);
		checkGuide=(CheckBox)findViewById(R.id.checkBoxGuide);
		startBtn=(Button)findViewById(R.id.startButton);
		count=pointsLayout.getChildCount();
		pointsArray=new ImageView[count];
		for(int i=0;i<count;i++){
			pointsArray[i]=(ImageView)pointsLayout.getChildAt(i);
			pointsArray[i].setEnabled(false);
		}
		pointsArray[0].setEnabled(true);
		mCurItem=0;
		//用此Activity实现接口 
		startBtn.setOnClickListener(this);
		guideScroll.setOnViewChangeListener(this);
		checkGuide.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				boolean flag=GuideActivity.this.getPreferences(Context.MODE_PRIVATE)
						.getBoolean(Configure.PREFERENCES_GUIDE, false);
				GuideActivity.this.getPreferences(Context.MODE_PRIVATE).edit()
				.putBoolean(Configure.PREFERENCES_GUIDE, !flag).commit();
				Toast.makeText(GuideActivity.this,""+flag,3).show();
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.startButton:
			guideScroll.setVisibility(View.GONE);
			pointsLayout.setVisibility(View.GONE);
			animOpenLayout.setVisibility(View.VISIBLE);
//			guideMain.setBackgroundResource(R.drawable.whatsnew_bg);
			Animation leftAnim=AnimationUtils.loadAnimation(this, R.anim.translate_left);
			Animation rightAnim=AnimationUtils.loadAnimation(this, R.anim.translate_right);
			leftAnimLayout.startAnimation(leftAnim);
			rightAnimLayout.startAnimation(rightAnim);
			rightAnim.setAnimationListener(new AnimationListenerImpl(){
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					super.onAnimationEnd(animation);
					leftAnimLayout.setVisibility(View.GONE);
					rightAnimLayout.setVisibility(View.GONE);
					Intent intent=new Intent();
					intent.setClass(GuideActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
//					overridePendingTransition(R.anim.anim_fromright_toup6,
//							R.anim.anim_down_toleft6);
				}
			});
			break;
			default:
				break;
		}
	}

	@Override
	public void onViewChange(int viewIndex) {
		// TODO Auto-generated method stub
		if(viewIndex<0||viewIndex>count-1||mCurItem==viewIndex){
			return;
		}
		pointsArray[mCurItem].setEnabled(false);
		pointsArray[viewIndex].setEnabled(true);
		mCurItem=viewIndex;
	}
}
