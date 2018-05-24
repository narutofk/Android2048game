package com.game2048;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {
	private TextView label;
	
	public Card(Context context) {
		super(context);
				
		label = new TextView(getContext());//新建一个textview
		label.setTextSize(32);//设置字体大小
		label.setBackgroundColor(0x33ffffff);//背景色
		label.setGravity(Gravity.CENTER);//设置字体居中
		
		//LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
        //LayoutParams.MATCH_PARENT);//满屏幕
		LayoutParams lp = new LayoutParams(-1, -1);
		lp.setMargins(10, 10, 0, 0);//间距10像素
		addView(label, lp);
		
		setNum(0);
	}
	
	
	private int num = 0;
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
		
		if (num<=0) {
			label.setText("");
		}else{
			label.setText(num+"");
		}
	}
	//添加equals()方法判断2个数是否相等
	public boolean equals(Card o) {
		return getNum()==o.getNum();
	}

	
}
