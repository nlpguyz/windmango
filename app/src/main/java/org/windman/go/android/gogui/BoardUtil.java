package org.windman.go.android.gogui;

import static net.sf.gogui.go.GoColor.BLACK;
import static net.sf.gogui.go.GoColor.WHITE;

import org.windman.go.R;
import org.windman.go.android.gui.BoardView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;

import net.sf.gogui.go.ConstBoard;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;

public class BoardUtil {
	
	public static void updateFromGoBoard(BoardView boardView, ConstBoard board, GoPoint clickedPoint,
			boolean markLastMove, boolean showMoveNumbers) {
		for (GoPoint p : board) {
			boardView.setColor(p, board.getColor(p));
		}
		
        GoPoint lastMove = null;
        if (board.getLastMove() != null) {
            lastMove = board.getLastMove().getPoint();
        }
        
        if (markLastMove) {
        	boardView.markLastMove(lastMove);
        } else {
        	boardView.markLastMove(null);
        }
        
		boardView.cleanAllField();
		for (int i = 0; i < board.getNumberMoves(); ++i) {
			GoPoint point = board.getMove(i).getPoint();
			if (point != null) {
				if (showMoveNumbers) {
					boardView.setLabel(point, Integer.toString(i + 1));
				}
			}
		}
		
		if (clickedPoint != null) {
			boardView.setCursor(clickedPoint);
		}
		
		boardView.refresh();
		
	}
	
	private static Bitmap BLACK_STONE_BITMAP = null;
	private static Bitmap WHITE_STONE_BITMAP = null;
	public static Bitmap getStone(Context context, GoColor color) {
		if (color == BLACK) {
			if (BLACK_STONE_BITMAP == null || BLACK_STONE_BITMAP.isRecycled()) {
				BLACK_STONE_BITMAP = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_stone);
			}
			return BLACK_STONE_BITMAP;
			
		} else if (color == WHITE) {
			if (WHITE_STONE_BITMAP == null || WHITE_STONE_BITMAP.isRecycled()) {
				WHITE_STONE_BITMAP = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_stone);
			}
			return WHITE_STONE_BITMAP;
		}
		
		return null;
	}
	
}
