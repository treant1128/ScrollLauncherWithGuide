package org.treant.scrollgrid2_mutiscreens.widgets;

import org.treant.scrollgrid2_mutiscreens.util.Configure;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
/**
 * ģ��Launcher�е�WorkSpace�������һ���
 * @author Administrator  http://blog.csdn.net/Yao_GUET date: 2011-05-04
 *
 */
public class ScrollLauncherLayout extends ViewGroup{
	
	private Scroller mScroller;
	private int mCurScreen;
	private int mDefaultScreen;
	private VelocityTracker mVelocityTracker;
	private static final int TOUCH_STATE_REST=0;
	private static final int TOUCH_STATE_SCROLLING=1;
	private static final int SNAP_VELOCITY = 600;
	private int mTouchState=TOUCH_STATE_REST;
	/**
	 * �����ƶ��¼�����̾��룬���С���������Ͳ������ƶ��ؼ�����viewpager����������������ж��û��Ƿ�ҳ
	 */
	private int mTouchSlop;
	private float mLastMotionX;
	private PageListener pageListener;
	public interface PageListener{
		public abstract void page(int page);
	}
	public void setPageListener(PageListener pageListener){
		this.pageListener=pageListener;
	}
	/**
	 * û�д˷����Ͳ�����XML�����ô�ViewGroup
	 * @param context
	 * @param attrs
	 */
	public ScrollLauncherLayout(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	public ScrollLauncherLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mScroller=new Scroller(context);
		mCurScreen =mDefaultScreen;
		//ViewConfiguration:Contains methods to standard constants used in the UI for timeouts, sizes, and distances
		//Configuration���еķ�����������get***�������ڻ�ȡ��UI��صı�׼����ֵ
		ViewConfiguration viewConfiguration=ViewConfiguration.get(getContext());
		
		mTouchSlop=viewConfiguration.getScaledTouchSlop();//����ܹ��������ƻ����ľ���
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft=0;
		int childCount=getChildCount();
		for(int i=0;i<childCount;i++){
			View childView=getChildAt(i);
			if(childView.getVisibility()!=View.GONE){
				int childWidth=childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft+childWidth, childView.getMeasuredHeight());
				childLeft+=childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width=View.MeasureSpec.getSize(widthMeasureSpec);
		int widthMode=View.MeasureSpec.getMode(widthMeasureSpec);
		if(widthMode!=View.MeasureSpec.EXACTLY){
			throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode");
		}
		//wrap_content ����ȥ����AT_MOST �̶���ֵ��fill_parent �����ģʽ��EXACTLY
		int heightMode=View.MeasureSpec.getMode(heightMeasureSpec);
		if(heightMode!=View.MeasureSpec.EXACTLY){
			throw new IllegalStateException("ScrollLayout only can run as EXACTLY mode");
		}
		// The children are given the same width and height as the ScrollLayout
		int count=getChildCount();
		for(int i=0;i<count;i++){
			//The actual measurement work of a view is performed in onMeasure(int, int), called by this method.
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen*width, 0);//This will cause a call to onScrollChanged(int, int, int, int) and the view will be invalidated.
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(Configure.isMoving){
			return false;// intercept and dispatch event
		}
		int action=ev.getAction();
		if(action==MotionEvent.ACTION_MOVE && mTouchState!=TOUCH_STATE_REST){
			return true;   // �����¼�  ����onTouchEvent����
		}
		float x=ev.getX();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			mLastMotionX=x;
			mTouchState=mScroller.isFinished()?TOUCH_STATE_REST:TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_MOVE:
			int xDiff=(int) Math.abs(mLastMotionX-x);
			if(xDiff>mTouchSlop){//�����ƶ������ж��Ƿ񴥷�scroll״̬
				mTouchState=TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState=TOUCH_STATE_REST;
			break;
		}
		return mTouchState!=TOUCH_STATE_REST; //�������REST��������Scrolling���򷵻�true  
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(mVelocityTracker==null){
			//Be sure to call recycle() when done. only maintain an active object while tracking a movement, 
			//so that the VelocityTracker can be re-used elsewhere.
			mVelocityTracker=VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);     //�ڴ˴�addMovement ��׽һ��TouchEvent ʡȥ��ÿ��case��add
		int action=event.getAction();
		float x=event.getX();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(!mScroller.isFinished()){Log.i("ACTION_DOWN","�ƶ��б�ɲ��"+x);
				mScroller.abortAnimation();  //���ڹ���ʱDOWN�� ֹͣ
			}
			mLastMotionX=x;   //��ס���
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX=(int)(mLastMotionX-x);//
			mLastMotionX=x;//����û��ͨ��snapȥ��ϵͳ����Ķ�Ҫ��ʱ����mLastMotionX
			scrollBy(deltaX, 0);  
			break;
		case MotionEvent.ACTION_UP:  //����ACTION_UP���ٽ��ٶ��ж����ƵĿ��ٻ���
			VelocityTracker velocityTracker=mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, Float.MAX_VALUE);//1 pixel per millisecond /// 1000 pixels per second
		    // must first call computeCurrentVelocity(int) before calling this function.
			int velocityX=(int) velocityTracker.getXVelocity();
			if(velocityX>SNAP_VELOCITY&&mCurScreen>0){//(�ٶȴ���+600( ���һ�������Ļ����))&&���ǵ�һҳ
				// fling enough to move left
				snapToScreen(mCurScreen-1);
				--Configure.currentPage;
				pageListener.page(Configure.currentPage);
			}else if(velocityX<-SNAP_VELOCITY&&mCurScreen<getChildCount()-1){//(�ٶ�С��-600( ���󻬶�����Ļ����))&&�������һҳ
				snapToScreen(mCurScreen+1);
				Configure.currentPage++;
				pageListener.page(Configure.currentPage);
			}else{
				//û�п��ٻ���   ���ݾ�̬λ���ж��ǻ������ǹ�λ    ����ƫ��ֵ�ж�Ŀ�������ĸ���  
				snapToDestination();Log.i("���ݾ�̬λ���ж�", "���ݾ�̬λ���ж�");
				Configure.currentPage=mCurScreen;                   pageListener.page(Configure.currentPage);
			}
			//Return a VelocityTracker object back to be re-used by others. You must not touch the object after calling this function.
			if(mVelocityTracker!=null){
				mVelocityTracker.recycle();
				mVelocityTracker=null;
			}
			//����mTouchState״̬
			mTouchState=TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState=TOUCH_STATE_REST;
			break;
		}
		return true;//True if the event was handled
	}
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
	//	super.computeScroll();
		// computeScrollOffset()--the system execute the offset for you-> call this method you want to know the new location , 
		// if it returns true , the animation is not yet finished ,the loc will be altered to provide the new location;
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	/**
	 *  ��Ļ�ƶ�
	 * @param whichScreen
	 */
	public void snapToScreen(int whichScreen){Log.i("Snapִ��","ִ����snap");
		whichScreen=Math.max(0, Math.min(whichScreen, getChildCount()-1));//0��getChildCount())-1�ֱ�����һ/���һҳ���
	//	whichScreen=Math.min(Math.max(0, whichScreen), getChildCount()-1);//���һ��
		/**                        getScrollX()
		 * Return the scrolled left position of this view. This is the left edge of the displayed part of your view. 
		 * You do not need to draw any pixels farther left, since those are outside of the frame of your view on screen.
		 */
		Log.i("DasiDingGou","getScrollX()="+getScrollX()+"---whichScreen="+whichScreen+"---getWidth()="+getWidth());
		if(getScrollX()!=whichScreen*getWidth()){
			int delta=whichScreen*getWidth()-getScrollX();
			//Start scrolling by providing a starting point and the distance to travel
			mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta)*5);//Math.abs(delta)*2��������ʱ���λ�Ƴ�����  ϵ��������ָ��
			mCurScreen=whichScreen;
			invalidate();Log.i("mCurScreen", "mCurScreen="+mCurScreen);
		}	
	}
	
	private void snapToDestination(){
		int screenWidth=getWidth();
		//���赱ǰ����ƫ��ֵgetScrollX()����ÿ����Ļһ��Ŀ�ȣ�����ÿ����Ļ�Ŀ��
		int whichScreen=(getScrollX()+screenWidth/2)/screenWidth; Log.i("snapToDestination", whichScreen+"--"+screenWidth);
		snapToScreen(whichScreen);
	}
}
