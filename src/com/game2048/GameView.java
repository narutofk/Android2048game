package com.game2048;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

public class GameView extends GridLayout {

	public GameView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		
		initGameView();
	}

	public GameView(Context context) 
	{
		super(context);
		
		initGameView();
	}

	public GameView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		initGameView();
	}
	
	private void initGameView(){
		setColumnCount(4);//����ÿ�����4��
		setBackgroundColor(0xffbbada0);
		
		
		setOnTouchListener(new View.OnTouchListener() {
			
			private float startX,startY,offsetX,offsetY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();//��ʼ��X����
					startY = event.getY();//��ʼ��Y����
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX()-startX;//�ı��X���ꣽ���ڵģ���ʼ��
					offsetY = event.getY()-startY;//�ı��Y���ꣽ���ڵģ���ʼ��
					
					// ��X�ľ���ֵ>Y�ľ���ֵ�����������ƶ�������Ϊ�����ƶ������Ͻ�����Ϊ(0,0)
					if (Math.abs(offsetX)>Math.abs(offsetY)) {
						if (offsetX<-5) {
							swipeLeft();
						}else if (offsetX>5) {
							swipeRight();
						}
					}else{
						if (offsetY<-5) {
							swipeUp();
						}else if (offsetY>5) {
							swipeDown();
						}
					}
					
					break;
				}
				return true;
			}
		});
	}
	//��16���������Ϊ4*4�Ķ�ά���飬������ЩС������ӵ�������
	private Card[][] cardsMap = new Card[4][4];
	
	//�½�emptyPoint List����ſյ�С����
	private List<Point> emptyPoints = new ArrayList<Point>();
	
	//���㿨Ƭ�Ŀ��
	//��GameView����дonSizeChanged()��������̬�ؼ���ÿ������Ŀ��ߣ� 
	//��AndroidManifest.xml���޸���Ļ����Ϊ��ֱ�� 
	//android:screenOrientation=��portrait��
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//�Կ�������Сֵ��Ȼ�����ÿһ��С����Ŀ��
		int cardWidth = (Math.min(w, h)-10)/4;
		//��С������ӵ�������
		addCards(cardWidth,cardWidth);
		//��ʼ��Ϸ
		startGame();
	}
	//��ӿ�Ƭ
	private void addCards(int cardWidth,int cardHeight){
		
		Card c;
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				c = new Card(getContext());
				c.setNum(0);
				 //�����ͼ
				addView(c, cardWidth, cardHeight);				
				cardsMap[x][y] = c;
			}
		}
	}
	//��ʼ��Ϸ
	private void startGame(){
		
		MainActivity.getMainActivity().clearScore();
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				cardsMap[x][y].setNum(0);
			}
		}
		//��������
		addRandomNum();
		addRandomNum();
	}
	//��������,��Ϸ�������ɵ���������Щ�յ�С�������������
	private void addRandomNum(){
		//���point
		emptyPoints.clear();
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				//������λ��û��ֵ�����
				if (cardsMap[x][y].getNum()<=0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}
		//����ó�һ����
		Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
		//�������2��4������2�ĸ��ʴ���4
		cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);
	}
	
	
	private void swipeLeft(){
		
		boolean merge = false;
		//�������µĵ�һ�п�ʼ
		for (int y = 0; y < 4; y++) {
			//��������
			for (int x = 0; x < 4; x++) {
				
				for (int x1 = x+1; x1 < 4; x1++) {
					//��ǰλ���ϵ�ֵ��Ϊ0
					if (cardsMap[x1][y].getNum()>0) {
						//��ǰ������ߵķ���Ϊ�գ��򽫵�ǰ�����ֵ������ߣ�ֱ������ǲ�Ϊ�յķ���Ϊֹ
						if (cardsMap[x][y].getNum()<=0) {
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);							
							x--;							
							merge = true;
						//��߿�Ƭ��ֵ��Ϊ�����뵱ǰֵ����
						}else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (merge) {
			//��ɺ������һ���������
			addRandomNum();
			//���ж���Ϸ�Ƿ����
			checkComplete();
		}
	}
	private void swipeRight(){
		
		boolean merge = false;
		
		for (int y = 0; y < 4; y++) {
			for (int x = 3; x >=0; x--) {
				
				for (int x1 = x-1; x1 >=0; x1--) {
					if (cardsMap[x1][y].getNum()>0) {
						
						if (cardsMap[x][y].getNum()<=0) {
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);
							
							x++;
							merge = true;
						}else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeUp(){
		
		boolean merge = false;
		
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				
				for (int y1 = y+1; y1 < 4; y1++) {
					if (cardsMap[x][y1].getNum()>0) {
						
						if (cardsMap[x][y].getNum()<=0) {
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);
							
							y--;
							
							merge = true;
						}else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						
						break;
						
					}
				}
			}
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeDown(){
		
		boolean merge = false;
		
		for (int x = 0; x < 4; x++) {
			for (int y = 3; y >=0; y--) {
				
				for (int y1 = y-1; y1 >=0; y1--) {
					if (cardsMap[x][y1].getNum()>0) {
						
						if (cardsMap[x][y].getNum()<=0) {
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);
							
							y++;
							merge = true;
						}else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	//�������ÿ�λ�������������Ҫ�ж���Ϸ�Ƿ�������ж����������б����������޷��������
	private void checkComplete(){
		
		boolean complete = true;
		
		ALL:
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				 //������λ��û��ֵ������������ȣ��򲻽�����������Ϸ����
				if (cardsMap[x][y].getNum()==0||
						(x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
						(x<3&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
						(y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
						(y<3&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {
					
					complete = false;
					break ALL;
				}
			}
		}
		
		if (complete) {
            new AlertDialog.Builder(getContext())
                    .setTitle("��Ϸ����!")
                    .setMessage("���ĵ÷�Ϊ��" + MainActivity.getMainActivity().score )
                    .setPositiveButton("����",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    startGame();
                                }
                            })
                    .setNegativeButton("�˳�",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    MainActivity.getMainActivity().finish();
                                }
                            }).show();
        }
		
	}
	
	
}
