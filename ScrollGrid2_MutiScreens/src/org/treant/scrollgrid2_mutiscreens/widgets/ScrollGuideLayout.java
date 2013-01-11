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
	
	private VelocityTracker mVelocityTracker;   //�ٶȸ���
	private static final int SNAP_VELOCITY=600;  //��ҳ��ֵ
	
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
			if(!mScroller.isFinished()){  //���ACTION_DOWNʱ,����scrolling  ��ֹͣ����
				mScroller.abortAnimation();
			}
			mLastMotionX=x;  //���
			break;
		case MotionEvent.ACTION_MOVE:
			//������ʱ scrollλ��ֵ����,delta>0,�����mLastMotionx>�յ�x
			int deltaX=(int)(mLastMotionX-x);
			if(canScrollFurther(deltaX)){ //�޶����Զ���Χ  ��һҳ��������   
				if(mVelocityTracker!=null){
					mVelocityTracker.addMovement(event);//������switch֮ǰ����addMovement����ÿ��case��Ҫaddһ��
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
			if(velocityX>SNAP_VELOCITY&&mCurScreen>0){     //�ٶ�Ϊ�����һ�     �������ٶ���ֵ  ����  ���ǵ�һҳ
				Log.i("Left", "Snap Left");
				snapToScreen(mCurScreen-1);
			}else if(velocityX<-SNAP_VELOCITY&&mCurScreen<(getChildCount()-1)){
				Log.i("Right", "Snap Right");
				snapToScreen(mCurScreen+1);
			}else{
				//�ٶ�û������ֵ  ���ݻ�������ʱACTION_UPλ���ж��Զ�����
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
		whichScreen=Math.max(0, Math.min(whichScreen, getChildCount()-1));  //��һ���޶� λ����0~~~getChildCount()-1
		if(getScrollX()!=whichScreen*getWidth()){
			final int deltaX=whichScreen*getWidth()-getScrollX();
			mScroller.startScroll(getScrollX(), 0, deltaX, 0, Math.abs(deltaX*3));
			mCurScreen=whichScreen;
			invalidate();//If the view is visible, onDraw(android.graphics.Canvas) will be called at some point in the future.
			//�ڷ�ҳ�����лص��ӿ� --->������ʵ�����������д������
			if(mOnViewChangeListener!=null){
				mOnViewChangeListener.onViewChange(mCurScreen);
			}
		}
	}
	
	private void snaoToDestination(){
		final int screenWidth=getWidth();
		final int destScreen=(getScrollX()+screenWidth/2)/screenWidth;  //�������ҳ
		snapToScreen(destScreen);
	}
	/**
	 * �޶��ɻ�����Χ����Խ��
	 * ��һҳ(getScrollX()<=0)�����һ�(deltaX<0),���һҳ������(deltaX>0)
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
