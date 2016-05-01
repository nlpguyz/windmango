package net.sf.gogui.gamefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.windman.go.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import net.sf.gogui.game.GameTree;

public class GameDataSource implements DataSource {

	private String mBookName = null;
	private List<GameTree> mGameTreeList = new ArrayList<GameTree>();
	private static DataSource mDataSource = null;
	private Context mContext = null;
	private static boolean mHasInit = false;
	
	public static DataSource getInstance(Context context) {
		if (mDataSource == null) {
			mDataSource = new GameDataSource(context);
			mHasInit = false;
		}
		return mDataSource;
	}
	
	private GameDataSource(Context context) {
		mContext = context;
	}
	
	public boolean hasInit() {
		return mHasInit;
	}
	
	public void init() {
		if (mHasInit) {
			sendInitFinishMessage(true);
			return;
		}
		
		try {
			new Thread(new ReadDataThread(mContext.getAssets().open("data/data_sgf"))).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class ReadDataThread implements Runnable {

		private InputStream mInputStream = null;
		public ReadDataThread(InputStream is) {
			mInputStream = is;
		}
		@Override
		public void run() {
			try {
				initBySourceFile(mInputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			boolean isSuccess = (mGameTreeList != null) && (mGameTreeList.size() > 0);
			mHasInit = isSuccess;
			sendInitFinishMessage(isSuccess);
		}
		
	}
	
	private void sendInitFinishMessage(boolean isSuccess) {
		Intent intent = new Intent(MainActivity.INIT_DATA_SOURCE_FINISH);
		intent.putExtra(MainActivity.EXTRA_KEY_INIT_STATE, isSuccess ? MainActivity.INIT_SUCCESS : MainActivity.INIT_FAIL);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}
	
	private boolean initBySourceFile(InputStream is) throws Exception {
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is, "GB2312"));
		mBookName = bufferedreader.readLine();
		
		String str = null;
		while((str = bufferedreader.readLine()) != null) {
			if (str != null && str.length() > 0) {
				GameReader reader = new GameReader(new StringReader(str), str.length());
				if (reader != null) {
					mGameTreeList.add(reader.getTree());
				}
			}
		}
		
		if (bufferedreader != null) {
			bufferedreader.close();
		}
		
		return false;
	}
	
	private boolean initBySourceDir(String dir) throws Exception{
		File rootDir = new File(dir);
		if( rootDir.exists() && rootDir.isDirectory() ) {
			File[] fileList = rootDir.listFiles();
			for (int i=0; i<fileList.length; i++) {//
				File file = fileList[i];
				if (!file.getName().endsWith(".sgf")) {
					continue;
				}
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
				GameReader reader = new GameReader(bufferedreader, file.length());
				if (bufferedreader != null) {
					try {
						bufferedreader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (reader != null) {
					mGameTreeList.add(reader.getTree());
				}
			}
		}
		return false;
	}
	
	@Override
	public int getPageCount() {
		return mGameTreeList.size();
	}

	@Override
	public GameTree getPageByPosition(int position) {
		if (position < 0 || position >= mGameTreeList.size()) {
			return null;
		}
		return mGameTreeList.get(position);
	}

	@Override
	public String getBookName() {
		return mBookName;
	}

}
