package com.game2048;

import com.game2048.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public MainActivity() 
	{
		mainActivity = this;
	}
	
	public static MainActivity getMainActivity() {
		return mainActivity;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvScore = (TextView) findViewById(R.id.tvScore);
	}


	
	public boolean onCreateOptionsMenu(Menu menu) {

		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//��շ���
	public void clearScore(){
		score = 0;
		showScore();
	}
	//��ʾ����
	public void showScore(){
		tvScore.setText(score+"");
	}
	//��ӷ���
	public void addScore(int s){
		score+=s;
		showScore();
	}

	int score = 0;
	private TextView tvScore;
	
	private static MainActivity mainActivity = null;
	
	
	
	 
    //�˵������ؼ���Ӧ
    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "�ٰ�һ���˳�����", 1000).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
