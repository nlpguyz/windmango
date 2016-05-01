package org.windman.go.android.gogui;

import java.util.HashMap;
import java.util.Map;

import net.sf.gogui.gamefile.DataSource;
import net.sf.gogui.gamefile.GameDataSource;

import org.windman.go.MainActivity;
import org.windman.go.R;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GoBookActivity extends AppCompatActivity implements GoFragment.Listener {

	private ViewPager mBookViewPager = null;
	private int mCurrentPosition = 0;
	private Map<Integer, GoFragment> mFragmentMap = new HashMap<Integer, GoFragment> ();
	
	private DataSource mDataSource = null;
	
	private TextView mCommentTextView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDataSource = GameDataSource.getInstance(this);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Log.i("GoFragment", "screenWidth=" + dm.widthPixels + ", screenHeigh" + dm.heightPixels);
		GoConfig.getInstance(this).saveScreenConfig(dm.widthPixels, dm.heightPixels);
		
		setContentView(R.layout.activity_main);
		
		mBookViewPager = new ViewPager(this);
        mBookViewPager.setId(R.id.book_viewpager);
		int screenWidth = GoConfig.getInstance(this).getScreenWidth();
		LinearLayout rootLayout = (LinearLayout)findViewById(R.id.board);
		rootLayout.addView(mBookViewPager, screenWidth, screenWidth);
		
		mBookViewPager.setBackgroundResource(R.drawable.wood);
		mBookViewPager.setAdapter(new BookPagerAdapter(getSupportFragmentManager()));
		mBookViewPager.setOnPageChangeListener(new BookPageChangeListener());
		
		mCommentTextView = (TextView) findViewById(R.id.node_comment);
				
		initGameSound();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
    	
		mCurrentPosition = GoConfig.getInstance(this).readCurrentPagePosition();
		mBookViewPager.setCurrentItem(mCurrentPosition);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void updateBoardBar(OnClickListener listener, boolean isTryMode) {
//		findViewById(R.id.gogui_first_button).setOnClickListener(listener);
//		findViewById(R.id.gogui_previous_10_button).setOnClickListener(listener);
//		findViewById(R.id.gogui_previous_button).setOnClickListener(listener);
//		findViewById(R.id.gogui_next_button).setOnClickListener(listener);
//		findViewById(R.id.gogui_next_10_button).setOnClickListener(listener);
//		findViewById(R.id.gogui_last_button).setOnClickListener(listener);
//		findViewById(R.id.test_sgf).setOnClickListener(listener);
		
		Button redoButton = (Button)findViewById(R.id.redo_button);
		redoButton.setOnClickListener(listener);
		
		Button backButton = (Button)findViewById(R.id.back_button);
		backButton.setOnClickListener(listener);
		
		Button tryButton = (Button)findViewById(R.id.try_button);
		tryButton.setOnClickListener(listener);
		
		Button answerButton = (Button)findViewById(R.id.answer_button);
		answerButton.setOnClickListener(listener);		
		
		if (isTryMode) {
			redoButton.setEnabled(false);
			backButton.setEnabled(true);
			tryButton.setEnabled(true);
			answerButton.setEnabled(false);
			tryButton.setText(getString(R.string.work));
		} else {
			redoButton.setEnabled(true);
			backButton.setEnabled(true);
			tryButton.setEnabled(true);
			answerButton.setEnabled(true);
			tryButton.setText(getString(R.string.try_work));
		}
	}

	@Override
	public void updateComment(String comment) {
		if (mCommentTextView != null) {
			mCommentTextView.setText(comment);
		}
	}

	@Override
	public void updateTitle(String title) {
		setTitle(title);
	}
	
	@Override
	public void playSound(int which) {
		if (mGameSoundState == GoConfig.GAME_SOUND_STATE_OFF) {
			return;
		}
		
		int resId = mSoundPool.play(mSoundHashMap.get(which), 1, 1, 0, 0, 1);
		if (resId == 0) {
			Toast.makeText(this, which + " is play fail.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private int mGameSoundState = GoConfig.GAME_SOUND_STATE_ON;
	private SoundPool mSoundPool = null;
	private Map<Integer, Integer> mSoundHashMap = new HashMap<Integer, Integer>();
	public static final int GAME_SOUND_GO = 0;
	public static final int GAME_SOUND_WRONG = 1;
	public static final int GAME_SOUND_BUTTON = 2;
	
	private void initGameSound() {
		mSoundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
		mSoundHashMap.put(GAME_SOUND_GO, mSoundPool.load(this, R.raw.go, 0));
		mSoundHashMap.put(GAME_SOUND_WRONG, mSoundPool.load(this, R.raw.wrong, 0));
		mSoundHashMap.put(GAME_SOUND_BUTTON, mSoundPool.load(this, R.raw.button, 0));
		
		mGameSoundState = GoConfig.getInstance(this).getGameSoundState();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mSoundPool != null) {
			mSoundPool.release();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_menu, menu);
		MenuItem goPageItem = menu.findItem(R.id.action_go_to_page);
		goPageItem.setVisible(false);
		return true;
	}
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_switch_sound:
            	mGameSoundState = (mGameSoundState == GoConfig.GAME_SOUND_STATE_OFF) ? GoConfig.GAME_SOUND_STATE_ON : GoConfig.GAME_SOUND_STATE_OFF;
            	GoConfig.getInstance(this).setGameSoundState(mGameSoundState);
            	if (mGameSoundState == GoConfig.GAME_SOUND_STATE_OFF) {
            		item.setTitle(getString(R.string.action_switch_sound_on));
            	} else {
            		item.setTitle(getString(R.string.action_switch_sound_off));
            	}
                break;
                
            case R.id.action_go_to_page:
                break;
                
            case android.R.id.home:
            	finish();
            	startActivity(new Intent(this, MainActivity.class));
                break;
                
            default:
            	break;
        }
        
        return true;
    }
	
	private class BookPagerAdapter extends FragmentStatePagerAdapter {
		public BookPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.i("DetialFragment", "destroyItem() position=" + position);
			super.destroyItem(container, position, object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.i("DetialFragment", "instantiateItem() position=" + position);
			return super.instantiateItem(container, position);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
		
		@Override
		public Fragment getItem(int position) {
			Log.i("DetialFragment", "getItem() position=" + position);
			GoFragment fragment = null;
			if (mFragmentMap.containsKey(position)) {
				fragment = mFragmentMap.get(position);
			}else{
				fragment = new GoFragment(position);
				fragment.setListener(GoBookActivity.this);
				mFragmentMap.put(position, fragment);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return GameDataSource.getInstance(GoBookActivity.this).getPageCount();
		}
	};
	
	private class BookPageChangeListener implements OnPageChangeListener {
        /**
         * Used during page migration, to remember the next position {@link #onPageSelected(int)}
         * specified.
         */
        private int mNextPosition = -1;

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            if (mCurrentPosition == position) {
            }

            mNextPosition = position;
        }

        public void onPageScrollStateChanged(int state) {
            switch (state) {
	            case ViewPager.SCROLL_STATE_IDLE: {
	            	mCurrentPosition = mNextPosition;
	            	GoConfig.getInstance(getApplicationContext()).saveCurrentPagePosition(mCurrentPosition);
	            	
	            	if (mCurrentPosition >= 0) {
	            		GoFragment fragment = mFragmentMap.get(mCurrentPosition);
	            		if (fragment != null) {
	            			fragment.onPageChange();
	            		}
	            	}
	                break;
	            }
	            case ViewPager.SCROLL_STATE_DRAGGING:
	            case ViewPager.SCROLL_STATE_SETTLING:
	            default:
	                break;
            }
        }
    }
	
}
