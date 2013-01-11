package org.treant.scrollgrid2_mutiscreens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.treant.scrollgrid2_mutiscreens.util.AnimationListenerImpl;
import org.treant.scrollgrid2_mutiscreens.util.Configure;
import org.treant.scrollgrid2_mutiscreens.util.DragAdapter;
import org.treant.scrollgrid2_mutiscreens.util.Utils;
import org.treant.scrollgrid2_mutiscreens.widgets.DragGridView;
import org.treant.scrollgrid2_mutiscreens.widgets.ScrollLauncherLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private DragGridView dragGridView;
	private ScrollLauncherLayout scrollLauncher;
	private ImageView runImage;
	private TextView curPage;
	
	ArrayList<Map<String,Object>> listData = null;
	TranslateAnimation left, right;
	public static final int COLUMN_SIZE=2;
	public static final int PAGE_SIZE = 6;
	ArrayList<ArrayList<Map<String,Object>>> lists = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		initWidgets();
		initData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		runAnimation();
	}
	private void initWidgets() {
		Configure.init(this);
		listData = new ArrayList<Map<String,Object>>();
		lists = new ArrayList<ArrayList<Map<String,Object>>>();
		scrollLauncher = (ScrollLauncherLayout) this
				.findViewById(R.id.scrollLauncher);
		runImage = (ImageView) this.findViewById(R.id.run_image);Log.i("runImage", Configure.screenWidth+""+Configure.screenHeight);
		Utils.getOptBitmapById(runImage, getResources(), 
				R.drawable.bora_french, 2000, Configure.screenHeight);
		curPage = (TextView) this.findViewById(R.id.cur_page);
	}
	
	private void initData(){
		String[] itemsTitle=this.getResources().getStringArray(R.array.itemsText);
		TypedArray itemsIcon=this.getResources().obtainTypedArray(R.array.itemIcon);
		String[] itemsUrl=this.getResources().getStringArray(R.array.itemsURI);
		
		Configure.countPage=(int) FloatMath.ceil(itemsTitle.length/(float)PAGE_SIZE);
		
		Map<String, Object> map=null;
		for(int i=0; i<Configure.countPage*PAGE_SIZE; i++){
			map=new HashMap<String, Object>();
			if(i<itemsTitle.length){
				map.put("title", itemsTitle[i]);
				map.put("icon", itemsIcon.getDrawable(i));
				map.put("url", itemsUrl[i]);
			}else{
				map.put("icon",null);
			}
			listData.add(map);
			lists.add(new ArrayList<Map<String,Object>>());
		}
		
//		Configure.countPage=(int) Math.ceil(listData.size()/(float)PAGE_SIZE);
		if(dragGridView!=null){
			scrollLauncher.removeAllViews();
		}
		for(int i=0;i<Configure.countPage;i++){   //����ÿһҳ��
			//����һ��ҳ���е�ÿһ��,û�����һҳʱѭ�������϶�ΪPAGE_SIZE
			for(int j=PAGE_SIZE*i;j<(PAGE_SIZE*(i+1)>listData.size()?listData.size():(PAGE_SIZE*(i+1)));j++){
	//		//�������Ұ�ʣ��Ŀո���Ҳ�ÿ�Map��������ҳ��,ע�͵������Ǹ�for���
	//		for(int j=PAGE_SIZE*i;j<(PAGE_SIZE*(i+1));j++){
				//�������� �ַ�������ҳ��
				lists.get(i).add(listData.get(j));
			}
			//һ��ҳ��һ��DragGridView  ѭ����ʼ��
			dragGridView=new DragGridView(this);
			dragGridView.setAdapter(new DragAdapter(this, lists.get(i)));
			dragGridView.setNumColumns(COLUMN_SIZE);
			dragGridView.setHorizontalSpacing(0);
			dragGridView.setVerticalSpacing(0);
			dragGridView.setSelector(R.anim.spirit);
			final int temp=i;
			dragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
			//		processItemClick();
					Map<String, Object> map=lists.get(temp).get(position);
					Toast.makeText(MainActivity.this, map.get("title")+"--"+map.get("url"), Toast.LENGTH_SHORT).show();
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)map.get("url"))));
				}
				
			});
			//�����ڲ��� �趨page����
			dragGridView.setPageListener(new DragGridView.G_PageListener(){

				@Override
				public void page(int page) {
					// TODO Auto-generated method stub
					scrollLauncher.snapToScreen(page);
					new Handler().postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Configure.isChangingPage=false;
						}
						
					}, 50);
				}
				
			});
			dragGridView.setOnItemChangeListener(new DragGridView.G_ItemChangeListener() {
				
				@Override
				public void change(int from, int to, int count) {
					// TODO Auto-generated method stub
					Map<String, Object> toMap=lists.get(Configure.currentPage-count).get(from);//���to�����from���ݽ���
					Log.i("error", lists.get(Configure.currentPage).size()+"----"+to);
					Map<String, Object> fromMap=lists.get(Configure.currentPage).get(to);
						
					lists.get(Configure.currentPage).add(to, toMap);//���λ�� <--ҳ��currentPage��toλ
					lists.get(Configure.currentPage).remove(to+1);
					//������ȡ��fromStr֮ǰִ��notifyDataSetChanged()����  ����ȡ�����Ǹ��º��toStr
					//	((DragAdapter)(((DragGridView)scrollLauncher.getChildAt(Configure.currentPage)).getAdapter())).notifyDataSetChanged();//֪ͨAdapter���ݸı�
					
					lists.get(Configure.currentPage-count).add(from,fromMap);//���λ��<-- currentPage-countҳ��fromλ
					lists.get(Configure.currentPage-count).remove(from+1);
						
					((DragAdapter)(((DragGridView)scrollLauncher.getChildAt(Configure.currentPage)).getAdapter())).notifyDataSetChanged();//֪ͨAdapter���ݸı�
					((DragAdapter)(((DragGridView)scrollLauncher.getChildAt(Configure.currentPage-count)).getAdapter())).notifyDataSetChanged();
					
					setCurrentPage(Configure.currentPage);
					
					Toast.makeText(MainActivity.this,"�ӵ�-"+(1+from+PAGE_SIZE*(Configure.currentPage-count))
							+"-�ƶ�����-"+(1+to+PAGE_SIZE*Configure.currentPage)
							+"-��Խ��ҳ��="+count, Toast.LENGTH_LONG).show();
				}
			});
			scrollLauncher.addView(dragGridView);Log.i("ÿҳ��С", "��"+i+"ҳ��"+lists.get(i).size()+"��");
		}
		scrollLauncher.setPageListener(new ScrollLauncherLayout.PageListener() {
			
			@Override
			public void page(int page) {
				// TODO Auto-generated method stub
//				Toast.makeText(MainActivity.this,"��������"+Configure.currentPage+"ҳ", Toast.LENGTH_SHORT).show();
				setCurrentPage(page);
			}
		});
	}
	
	private void setCurrentPage(final int page){
		Animation animation=getPageAnimation(1.0f, 0, 1.0f, 1.0f, 300);
		animation.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				curPage.setText((page+1)+"");
				curPage.startAnimation(getPageAnimation(0, 1.0f, 1.0f, 1.0f, 300));
			}
		});
		curPage.startAnimation(animation);
	}
	
	private Animation getPageAnimation(float fromX, float toX, float fromY, float toY, long durationMillis){
		Animation scaleAnimation=new ScaleAnimation(fromX, toX, fromY, toY,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(durationMillis);
		scaleAnimation.setFillAfter(true);
		return scaleAnimation;
	}	
	/**
	 * execute background scroll cycling animation
	 */
	private void runAnimation() {
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -2f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				0);
		left.setFillAfter(true);
		left.setDuration(20000);
		left.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				runImage.startAnimation(right);
			}
		});
		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, -2f,
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				0);
		
		right.setFillAfter(true);
		right.setDuration(20000);
		right.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				runImage.startAnimation(left);
			}
		});
		runImage.startAnimation(right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
