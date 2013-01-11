package org.treant.scrollgrid2_mutiscreens.util;

import java.util.List;
import java.util.Map;

import org.treant.scrollgrid2_mutiscreens.R;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAdapter extends BaseAdapter {
		
	private Context context;
	private List<Map<String,Object>> list;
	
	public DragAdapter(Context context, List<Map<String,Object>> list){
		this.context=context;
		this.list=list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list!=null?list.size():0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		ViewHolder vh=null;
//		if(convertView==null){
		convertView=LayoutInflater.from(context).inflate(R.layout.layout_item, null);
//			vh=new ViewHolder();
//			vh.text=(TextView)convertView.findViewById(R.id.item_title);
//			vh.image=(ImageView)convertView.findViewById(R.id.item_icon);
//			convertView.setTag(vh);
//		}else{
//			vh=(ViewHolder)convertView.getTag();
//		}
//		vh.text.setText((String)list.get(position).get("title"));
//		vh.image.setImageDrawable((Drawable)list.get(position).get("icon"));
		TextView t=(TextView)convertView.findViewById(R.id.item_title);
		ImageView i=(ImageView)convertView.findViewById(R.id.item_icon);
		t.setText((String)list.get(position).get("title"));
		Drawable drawable=(Drawable)list.get(position).get("icon");
		if(drawable!=null){
			i.setImageDrawable(drawable);
		}else{Log.i("ÒþÉí", "??");
//			((ViewGroup)convertView).removeAllViews();
//			((ViewGroup)convertView).addView(new ImageView(context),
//					new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			convertView.setBackgroundColor(Color.GREEN);
		convertView.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	
	public void exchangePosition(int dragPosition, int dropPosition) {
		// TODO Auto-generated method stub
		Map<String, Object> dragMap=list.get(dragPosition);
		Map<String, Object> dropMap=list.get(dropPosition);
		list.add(dropPosition, dragMap);
		list.remove(dropPosition+1);
		list.add(dragPosition, dropMap);
		list.remove(dragPosition+1);
		notifyDataSetChanged(); Log.i("¸Ä±ä", ""+list.size());
	}

}
