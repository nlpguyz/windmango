package org.windman.go.android.boardpainter;

import org.windman.go.android.gogui.BoardUtil;

import net.sf.gogui.go.GoColor;
import static net.sf.gogui.go.GoColor.BLACK;
import static net.sf.gogui.go.GoColor.WHITE;
import static net.sf.gogui.go.GoColor.EMPTY;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

// Field.java

/** State of a field on the board. */
public class Field implements ConstField {
	private GoColor mColor = EMPTY;
	private boolean mLastMoveMarker;
	private String mLabel = "";
	private boolean mCursor;
	
	public Field() {
	}

	@Override
	public void draw(Context context, Canvas canvas, int size, int x, int y) {
		if (mColor != EMPTY) {
            drawStone(context, canvas, size, x, y);
            
	        if (mLabel != null && ! mLabel.equals("")) {
	            drawLabel(canvas, size, x, y);
	        } else if (mLastMoveMarker) {
	            drawLastMoveMarker(canvas, size, x, y);
	        }
		}
		
		if (mCursor) {
			drawCursor(canvas, size, x, y);
		}
	}
	
	private void drawStone(Context context, Canvas canvas, int size, int x, int y) {
		Paint paint = new Paint();
		if (mColor == BLACK) {
			paint.setColor(Color.BLACK);
		} else if (mColor == WHITE) {
			paint.setColor(Color.WHITE);
		}
		
		int r = (int)(size/2);
		
		if (true) {
			Bitmap bitmap = BoardUtil.getStone(context, mColor);
			Rect dst = new Rect(x-r, y-r, x+r, y+r);
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			canvas.drawBitmap(bitmap, src, dst, paint);
		} else {
			canvas.drawOval(new RectF(x-r, y-r, x+r, y+r), paint);
		}
	}
	
	private void drawLastMoveMarker(Canvas canvas, int size, int x, int y) {
		Paint paint = new Paint();
		paint.setColor(Color.LTGRAY);
		float r = size * 0.2F;
		canvas.drawOval(new RectF(x-r, y-r, x+r, y+r), paint);
	}
	
	private void drawLabel(Canvas canvas, int size, int x, int y) {
		if (mLabel == null || mLabel.length() == 0) {
			return;
		}
		
		Paint paint = new Paint();
		paint.setTextAlign(Align.CENTER);
		float fontSize = size/2;
		paint.setTextSize(fontSize);
		if (mColor == BLACK) {
			paint.setColor(Color.WHITE);
		} else if (mColor == WHITE) {
			paint.setColor(Color.BLACK);
		}
		
		canvas.drawText(mLabel, x, y+fontSize/3, paint);
	}
	
	private void drawCursor(Canvas canvas, int size, int x, int y) {
		Paint paint = new Paint();
		//paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.RED);
		int r = (int)(size/2.0);
		int cr = 20;
		Rect rs = new Rect(x-r, y-r, x+r, y+r);
		canvas.save();
		canvas.clipRect(rs.left-cr, rs.top+r/2, rs.right+cr, rs.bottom-r/2);
		canvas.clipRect(rs.left+r/2, rs.top-cr, rs.right-r/2, rs.bottom+cr, Region.Op.UNION);
		canvas.clipRect(rs,  Region.Op.XOR);
		canvas.clipRect(new Rect(rs.left+r/6, rs.top+r/6, rs.right-r/6, rs.bottom-r/6),  Region.Op.DIFFERENCE);
		canvas.drawRect(rs,  paint);
		canvas.restore();
		
	}
	
	public void setColor(GoColor color) {
		mColor = color;
	}
	
	@Override
	public GoColor getColor() {
		return mColor;
	}
	
    public void setLastMoveMarker(boolean lastMoveMarker)
    {
    	mLastMoveMarker = lastMoveMarker;
    }

	@Override
	public String getLabel() {
		return mLabel;
	}
    
	public void setLabel(String s) {
		mLabel = s;
	}
	
	public void setCursor(boolean cursor) {
		mCursor = cursor;
	}
    
	@Override
	public boolean getCursor() {
		return mCursor;
	}

	@Override
	public boolean getCrossHair() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Color getFieldBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getMark() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMarkCircle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMarkSquare() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMarkTriangle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getSelect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GoColor getGhostStone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GoColor getTerritory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInfluenceSet() {
		// TODO Auto-generated method stub
		return false;
	}

}
