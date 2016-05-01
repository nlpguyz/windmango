package net.sf.gogui.gamefile;

import java.io.File;

import net.sf.gogui.game.GameTree;

public interface DataSource {
	
	public static final String DATA_SOURCE_DIR = "/sdcard/windman/";

	int getPageCount();
	
	GameTree getPageByPosition(int position);
	
	String getBookName();
	
	void init();
	
	boolean hasInit();
	
}
