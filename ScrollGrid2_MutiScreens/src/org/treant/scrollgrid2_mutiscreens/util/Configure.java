package org.treant.scrollgrid2_mutiscreens.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class Configure {
	public static int screenWidth = 0;
	public static int screenHeight=0;
	public static float screenDensity=0;
	/**
	 * �Ƿ������ڻ�ҳ״̬����
	 */
	public static boolean isChangingPage=false;
	public static boolean isMoving=false;
	public static int currentPage=0;
	public static int countPage=0;
	/**
	 * ��ҳ�϶�itemʱ�ķֱ����ж�
	 */
	public static int boundaryInterceptTimes=6;
	
	public static final String PREFERENCES_GUIDE="preferences.guide";
	
	public static void init(Activity activity){
		if(screenWidth==0||screenHeight==0||screenDensity==0){
			DisplayMetrics dm=new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenWidth=dm.widthPixels;
			screenHeight=dm.heightPixels;
			screenDensity=dm.density;
		}
		currentPage=0;
		countPage=0;
	}
}
