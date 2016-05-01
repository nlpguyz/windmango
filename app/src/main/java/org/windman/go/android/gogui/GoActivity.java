package org.windman.go.android.gogui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.sf.gogui.game.ConstGameTree;
import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.Game;
import net.sf.gogui.game.GameTree;
import net.sf.gogui.game.NodeUtil;
import net.sf.gogui.gamefile.GameReader;
import net.sf.gogui.go.ConstPointList;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.Move;
import net.sf.gogui.sgf.SgfError;
import net.sf.gogui.util.ErrorMessage;

import org.windman.go.R;
import org.windman.go.android.boardpainter.ConstField;
import org.windman.go.android.gui.BoardView;

import android.app.Activity;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GoActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        getFragmentManager().beginTransaction().replace(android.R.id.content,
//                new GoFragment(-1)).commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
