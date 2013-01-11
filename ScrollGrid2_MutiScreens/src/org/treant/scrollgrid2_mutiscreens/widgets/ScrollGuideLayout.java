package org.treant.scrollgrid2_mutiscreens.widgets;

import org.treant.scrollgrid2_mutiscreens.util.OnViewChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollGuideLayout extends ViewGroup {

	private static final String TAG="ScrollGuideLayout";
	
	private VelocityTracker mVelocityTracker;   //速度跟踪
	private static final int SNAP_VELOCITY=600;  //翻页阈值
	
	private Scroller mScroller;
	private int mCurScreen;
	private int mDefaultScreen;
	private float mLastMotionX;
	private int mTouchSlop;
	
	private OnViewChangeListener mOnViewChangeListener;
	
	public ScrollGuideLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ScrollGuideLayout(Context context, AttributeSet attrs){
		super(context, attrs);
		init(context);
	}
	
	public ScrollGuideLayout(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context){
		mScroller=new Scroller(context);
		mCurScreen=mDefaultScreen;
		mTouchSlop=ViewConfiguration.getTouchSlop();
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if(changed){
			final int childCount=this.getChildCount();
			int childLeft=0;
			for(int i=0;i<childCount;i++){
				final View childView=this.getChildAt(i);
				if(childView.getVisibility()!=View.GONE){
					final int childWidth=childView.getMeasuredWidth();
					childView.layout(childLeft, 0,
							childLeft+childWidth, childView.getMeasuredHeight());
					childLeft+=childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		final int width=MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode=MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode=MeasureSpec.getMode(heightMeasureSpec);
		if(widthMode!=MeasureSpec.EXACTLY||heightMode!=MeasureSpec.EXACTLY){
			throw new IllegalStateException("ScrollLayout can only be at EXACTLY MODE");
		}
		final int childCount=this.getChildCount();
		for(int i=0;i<childCount;i++){
			this.getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen*width, 0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
	
		final int action=event.getAction();
		final float x=event.getX();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			Log.i("DOWN","ACTION___DOWN");
			if(mVelocityTracker==null){
				mVelocityTracker=VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);  
			}
			if(!mScroller.isFinished()){  //如果ACTION_DOWN时,正在scrolling  则停止动画
				mScroller.abortAnimation();
			}
			mLastMotionX=x;  //起点
			break;
		case MotionEvent.ACTION_MOVE:
			//手势左滑时 scroll位置值增大,delta>0,起点是mLastMotionx>终点x
			int deltaX=(int)(mLastMotionX-x);
			if(canScrollFurther(deltaX)){ //限定可以定范围  第一页不能右移   
				if(mVelocityTracker!=null){
					mVelocityTracker.addMovement(event);//除非在switch之前调用addMovement否则每个case都要add一下
				}
				mLastMotionX=x;
				scrollBy(deltaX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			int velocityX=0;
			if(mVelocityTracker!=null){
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000, Float.MAX_VALUE);
				velocityX=(int)mVelocityTracker.getXVelocity();//must first call computeCurrentVelocity(int) before calling this 
			}
			if(velocityX>SNAP_VELOCITY&&mCurScreen>0){     //速度为正是右滑     超过正速度阈值  并且  不是第一页
				Log.i("Left", "Snap Left");
				snapToScreen(mCurScreen-1);
			}else if(velocityX<-SNAP_VELOCITY&&mCurScreen<(getChildCount()-1)){
				Log.i("Right", "Snap Right");
				snapToScreen(mCurScreen+1);
			}else{
				//速度没超过阈值  根据缓慢滑动时ACTION_UP位置判定自动走向
				snaoToDestination();
			}
			if(mVelocityTracker!=null){
				mVelocityTracker.recycle();
				mVelocityTracker=null;
			}
			break;
		}
		
		return true;
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	private void snapToScreen(int whichScreen){
		whichScreen=Math.max(0, Math.min(whichScreen, getChildCount()-1));  //进一步限定 位置在0~~~getChildCount()-1
		if(getScrollX()!=whichScreen*getWidth()){
			final int deltaX=whichScreen*getWidth()-getScrollX();
			mScroller.startScroll(getScrollX(), 0, deltaX, 0, Math.abs(deltaX*3));
			mCurScreen=whichScreen;
			invalidate();//If the view is visible, onDraw(android.graphics.Canvas) will be called at some point in the future.
			//在翻页过程中回调接口 --->方法的实现由其具体重写来决定
			if(mOnViewChangeListener!=null){
				mOnViewChangeListener.onViewChange(mCurScreen);
			}
		}
	}
	
	private void snaoToDestination(){
		final int screenWidth=getWidth();
		final int destScreen=(getScrollX()+screenWidth/2)/screenWidth;  //过半进下页
		snapToScreen(destScreen);
	}
	/**
	 * 限定可滑动范围不能越界
	 * 第一页(getScrollX()<=0)不能右滑(deltaX<0),最后一页不能左滑(deltaX>0)
	 * @param deltaX
	 * @return
	 */
	private boolean canScrollFurther(int deltaX){
		if(getScrollX()<=0&&deltaX<0){
			return false;
		}
		if(getScrollX()>=(getChildCount()-1)*getWidth()&&deltaX>0){
			return false;
		}
		return true;
	}
	
	public void setOnViewChangeListener(OnViewChangeListener onViewChangeListener){
		this.mOnViewChangeListener=onViewChangeListener;
	}
}
