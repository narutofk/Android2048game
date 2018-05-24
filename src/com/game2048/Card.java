package com.game2048;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {
	private TextView label;
	
	public Card(Context context) {
		super(context);
				
		label = new TextView(getContext());//�½�һ��textview
		label.setTextSize(32);//���������С
		label.setBackgroundColor(0x33ffffff);//����ɫ
		label.setGravity(Gravity.CENTER);//�����������
		
		//LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
        //LayoutParams.MATCH_PARENT);//����Ļ
		LayoutParams lp = new LayoutParams(-1, -1);
		lp.setMargins(10, 10, 0, 0);//���10����
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
	//���equals()�����ж�2�����Ƿ����
	public boolean equals(Card o) {
		return getNum()==o.getNum();
	}

	
}
