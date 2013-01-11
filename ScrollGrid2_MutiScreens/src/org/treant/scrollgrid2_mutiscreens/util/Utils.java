package org.treant.scrollgrid2_mutiscreens.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Utils {
	
	public static Map<Integer, SoftReference<Bitmap>> bmpCache=new HashMap<Integer, SoftReference<Bitmap>>();
	public static Bitmap getOptBitmapById(final View image, final Resources res, int id, int reqWidth, int reqHeight){
		final BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeResource(res, id, options);
		
		options.inSampleSize=getInSampleSize(options, reqWidth, reqHeight);
		//decode bitmap with the inSampleSize
		options.inJustDecodeBounds=false;
		return loadByReference(options, res, id, new BitmapCallBack(){

			@Override
			public void handleBitmap(Bitmap bmp) {
				// TODO Auto-generated method stub
				image.setBackgroundDrawable(new BitmapDrawable(res, bmp));
			}
			
		});
	}
	
	public static Bitmap loadByReference(final BitmapFactory.Options options,
			final Resources res, final int resId, final BitmapCallBack callBack){
		if(bmpCache.containsKey(resId)){
			SoftReference<Bitmap> softReference=bmpCache.get(resId);
			if(softReference.get()!=null){
				Log.i("Soft","Reference");
				return softReference.get();
			}
		}
		final Bitmap bmp=null;
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				callBack.handleBitmap((Bitmap)msg.obj);
			}
		};
		
		new Thread(){
			public void run() {
				Bitmap b=BitmapFactory.decodeResource(res, resId, options);
				bmpCache.put(resId, new SoftReference<Bitmap>(b));
				Message message=handler.obtainMessage(0, b);
				handler.sendMessage(message);
			};
		}.start();
		return bmp;
	}
	public static int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
		//Raw width and height of the image
		final int width=options.outWidth;
		final int height=options.outHeight;
		int inSampleSize=1;
		if(width>reqWidth||height>reqHeight){
			if(width>height){
				inSampleSize=Math.round((float)height/(float)reqHeight);
			}else{
				inSampleSize=Math.round((float)width/(float)reqWidth);
			}
		}Log.i("inSampleSize", inSampleSize+"-"+width+"-"+height);
		return inSampleSize;
	}

	public interface BitmapCallBack{
		void handleBitmap(Bitmap bmp);
	}
	
}
