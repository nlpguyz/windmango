package org.windman.go.android.gogui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;

public class GoConfig {
	
	public static final String GO_CONFIG_XML = "go_config";
	public static final String SCREEN_WIDTH = "key_screen_width";
	public static final String SCREEN_HEIGHT = "key_screen_height";
	public static final String BOARD_VISIABLE_WIDTH = "key_board_visiable_width";
	public static final String BOARD_VISIABLE_HEIGHT = "key_board_visiable_height";
	
	public static final String BOOK_CURRENT_PAGE_INDEX = "book_current_page_index";
	
	public static final String GAME_SOUND_STATE = "key_game_sound_state";
	public static final int GAME_SOUND_STATE_OFF = 0;
	public static final int GAME_SOUND_STATE_ON = 1;
	
	
	private SharedPreferences mPreferences = null;
	
	
	private static GoConfig mInstance = null;
	
	public static GoConfig getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new GoConfig(context);
		}
		
		return mInstance;
	}
	
	private GoConfig(Context context) {
		mPreferences = context.getSharedPreferences(GO_CONFIG_XML, Context.MODE_PRIVATE);
	}
	
	public int getGameSoundState() {
		return mPreferences.getInt(GAME_SOUND_STATE, GAME_SOUND_STATE_ON);
	}
	
	public void setGameSoundState(int state) {
		mPreferences.edit().putInt(GAME_SOUND_STATE, state);
	}
	
	public int readCurrentPagePosition() {
		return mPreferences.getInt(BOOK_CURRENT_PAGE_INDEX, 0);
	}
	
	public void saveCurrentPagePosition(int position) {
		mPreferences.edit().putInt(BOOK_CURRENT_PAGE_INDEX, position).commit();
	}
	
	public int getScreenWidth() {
		return mPreferences.getInt(SCREEN_WIDTH, 100);
	}
	
	public void saveScreenConfig(int width, int height) {
		mPreferences.edit().putInt(SCREEN_WIDTH, width).putInt(SCREEN_HEIGHT, height).commit();
		
	}
}
