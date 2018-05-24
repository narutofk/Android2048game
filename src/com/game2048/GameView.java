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
		setColumnCount(4);//设置每行最多4个
		setBackgroundColor(0xffbbada0);
		
		
		setOnTouchListener(new View.OnTouchListener() {
			
			private float startX,startY,offsetX,offsetY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();//起始的X坐标
					startY = event.getY();//起始的Y坐标
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX()-startX;//改变的X坐标＝现在的－起始的
					offsetY = event.getY()-startY;//改变的Y坐标＝现在的－起始的
					
					// 若X的绝对值>Y的绝对值，则是左右移动，否则为上下移动，左上角坐标为(0,0)
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
	//将16个方块抽象化为4*4的二维数组，并将这些小方块添加到布局中
	private Card[][] cardsMap = new Card[4][4];
	
	//新建emptyPoint List来存放空的小方块
	private List<Point> emptyPoints = new ArrayList<Point>();
	
	//计算卡片的宽高
	//在GameView中重写onSizeChanged()方法，动态地计算每个方块的宽、高： 
	//在AndroidManifest.xml中修改屏幕布局为垂直： 
	//android:screenOrientation=”portrait”
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//对宽、高求最小值，然后求出每一个小方块的宽度
		int cardWidth = (Math.min(w, h)-10)/4;
		//将小方块添加到布局中
		addCards(cardWidth,cardWidth);
		//开始游戏
		startGame();
	}
	//添加卡片
	private void addCards(int cardWidth,int cardHeight){
		
		Card c;
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				c = new Card(getContext());
				c.setNum(0);
				 //添加视图
				addView(c, cardWidth, cardHeight);				
				cardsMap[x][y] = c;
			}
		}
	}
	//开始游戏
	private void startGame(){
		
		MainActivity.getMainActivity().clearScore();
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				cardsMap[x][y].setNum(0);
			}
		}
		//添加随机数
		addRandomNum();
		addRandomNum();
	}
	//添加随机数,游戏中新生成的数字在这些空的小方块中随机出现
	private void addRandomNum(){
		//清空point
		emptyPoints.clear();
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				//如果这个位置没有值。添加
				if (cardsMap[x][y].getNum()<=0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}
		//随机拿出一个点
		Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
		//随机产生2或4，生成2的概率大于4
		cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);
	}
	
	
	private void swipeLeft(){
		
		boolean merge = false;
		//从上至下的第一行开始
		for (int y = 0; y < 4; y++) {
			//从左至右
			for (int x = 0; x < 4; x++) {
				
				for (int x1 = x+1; x1 < 4; x1++) {
					//当前位置上的值不为0
					if (cardsMap[x1][y].getNum()>0) {
						//当前方块左边的方块为空，则将当前方块的值传到左边，直到左边是不为空的方块为止
						if (cardsMap[x][y].getNum()<=0) {
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);							
							x--;							
							merge = true;
						//左边卡片的值不为空且与当前值当等
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
			//完成后再添加一个随机数字
			addRandomNum();
			//并判断游戏是否结束
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
	//最后我们每次滑动结束都是需要判断游戏是否结束，判断条件则是列表被铺满而且无法进行相加
	private void checkComplete(){
		
		boolean complete = true;
		
		ALL:
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				 //如果这个位置没有值，或者两两相等，则不结束，否则游戏结束
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
                    .setTitle("游戏结束!")
                    .setMessage("您的得分为：" + MainActivity.getMainActivity().score )
                    .setPositiveButton("重来",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    startGame();
                                }
                            })
                    .setNegativeButton("退出",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    MainActivity.getMainActivity().finish();
                                }
                            }).show();
        }
		
	}
	
	
}
