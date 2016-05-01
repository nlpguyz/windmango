package org.windman.go;


import net.sf.gogui.gamefile.GameDataSource;

import org.windman.go.android.gogui.AboutAuthorActivity;
import org.windman.go.android.gogui.GoBookActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	public static final String INIT_DATA_SOURCE_FINISH = "org.windman.go.INIT_DATA_SOURCE_FINISH";
	public static final String EXTRA_KEY_INIT_STATE = "EXTRA_KEY_INIT_STATE";
	public static final int INIT_SUCCESS = 0;
	public static final int INIT_FAIL = 1;
	
	private BroadcastReceiver mInitDataSourceReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (INIT_DATA_SOURCE_FINISH.equals(intent.getAction())) {
				if (INIT_SUCCESS == intent.getIntExtra(EXTRA_KEY_INIT_STATE, -1)) {
					updateLoadState(STATE_LOAD_FINISH);
					String bookName = GameDataSource.getInstance(MainActivity.this).getBookName();
					mBookNameTextView.setText(bookName);
				} else {
					Toast.makeText(context, MainActivity.this.getString(R.string.load_fails), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitting_init_layout);
		
		mLoadingView = findViewById(R.id.waitting_init_data_source_finish_layout);
		mLoadFinishView = findViewById(R.id.load_finish_layout);
		mBookNameTextView = (TextView)findViewById(R.id.book_name);
		mLoadFinishView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, GoBookActivity.class));
			}
		});
		updateLoadState(STATE_LOADING);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mInitDataSourceReceiver, new IntentFilter(INIT_DATA_SOURCE_FINISH));
		GameDataSource.getInstance(this).init();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mInitDataSourceReceiver != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(mInitDataSourceReceiver);
		}
	}
	
	private View mLoadFinishView = null;
	private View mLoadingView = null;
	private TextView mBookNameTextView = null;
	private static final int STATE_LOADING = 0;
	private static final int STATE_LOAD_FINISH= 1;

	private void updateLoadState(int state) {
		mLoadingView.setVisibility(View.GONE);
		mLoadFinishView.setVisibility(View.GONE);
		
		switch (state) {
		case STATE_LOADING:
			mLoadingView.setVisibility(View.VISIBLE);
			break;
		case STATE_LOAD_FINISH:
			mLoadFinishView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_about_author:
            	startActivity(new Intent(this, AboutAuthorActivity.class));
                break;
                
            default:
            	break;
        }
        
        return true;
    }

}
