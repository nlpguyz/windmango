package org.windman.go.android.boardpainter;

import net.sf.gogui.go.BoardConstants;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;

import org.windman.go.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class BoardPainter {

	private Context mContext = null;
	private int mSize = GoPoint.DEFAULT_SIZE;
	private int mWidth = 100;
	private BoardConstants mConstants = null;

    private int mFieldSize;

    private int mFieldOffset;
    
    public BoardPainter(Context context, int width, int size) {
    	mContext = context;
    	
        mWidth = width;
        mSize = size;
        if (mConstants == null || mConstants.getSize() != mSize) {
        	mConstants = BoardConstants.get(mSize);
        }
        mFieldOffset = 20;//Math.round(mWidth / (mSize*2));
        mFieldSize = Math.round((mWidth - mFieldOffset * 2) / mSize);
    }
    
    public Bitmap draw(ConstField[][] field) {
        Log.i("BoardPainter", "draw() mFieldSize=" + mFieldSize + ", mFieldOffset=" + mFieldOffset + ", mSize=" + mSize + ", mWidth=" + mWidth);
        
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mWidth, Bitmap.Config.ARGB_8888);
        Canvas tmpcanvas = new Canvas(bitmap);
        //drawBackground(tmpcanvas);
        drawGrid(tmpcanvas);
        //drawGridLable(tmpcanvas, mSize, mWidth);
        drawFields(mContext, tmpcanvas, field);
        
        return bitmap;
    }
    
    private void drawBackground(Canvas canvas) {
    	Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wood);
    	Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, mWidth, mWidth, false);
    	canvas.drawBitmap(scaleBitmap, 0, 0, null);
    	if (scaleBitmap != null && !scaleBitmap.isRecycled()) {
    		scaleBitmap.recycle();
    	}
    	
    	if (bitmap != null && !bitmap.isRecycled()) {
    		bitmap.recycle();
    	}
 
    	
    }
    
    private void drawGridLable(Canvas canvas, int size, int width) {
    	Paint paint = new Paint();
    	paint.setColor(Color.BLACK);
    	
    	
    	for (int i = 0; i < mSize; ++i) {
    		float x, y;
    		//left
    		x = mFieldOffset - mFieldSize/2;
    		y = mFieldOffset + mFieldSize/2 + i * mFieldSize;
    		canvas.drawText((mSize-i)+"", x,  y, paint);
    		
    		//top
    		x = mFieldOffset + mFieldSize/2 + i * mFieldSize;
    		y = mFieldOffset;
    		canvas.drawText((char)('A'+i) + "", x,  y, paint);
    		
    		//right
    		x = mFieldOffset + mSize*mFieldSize;
    		y = mFieldOffset + mFieldSize/2 + i * mFieldSize;
    		canvas.drawText((mSize-i)+"", x,  y, paint);
    		
    		//botom
    		x = mFieldOffset + mFieldSize/2 + i * mFieldSize;
    		y = mFieldOffset + mSize*mFieldSize + mFieldSize/2;
    		canvas.drawText((char)('A'+i) + "", x,  y, paint);
    	}
    }
    
    private void drawGrid(Canvas canvas) {
    	
    	Paint paint = new Paint();
        for (int y = 0; y < mSize; ++y)
        {
            if (y == 0 || y == mSize - 1)
            	paint.setColor(Color.BLACK);
            else
            	paint.setColor(Color.GRAY);//Color.rgb(80, 80, 80)
            Point left = getCenter(0, y);
            Point right = getCenter(mSize - 1, y);
            canvas.drawLine(left.x, left.y, right.x, right.y, paint);
        }
        
        for (int x = 0; x < mSize; ++x)
        {
            if (x == 0 || x == mSize - 1)
            	paint.setColor(Color.BLACK);
            else
            	paint.setColor(Color.GRAY);//Color.rgb(80, 80, 80)
            Point top = getCenter(x, 0);
            Point bottom = getCenter(x, mSize - 1);
            canvas.drawLine(top.x, top.y, bottom.x, bottom.y, paint);
        }
        
        int r;
        if (mFieldSize <= 7)
            return;
        else if (mFieldSize <= 33)
            r = 1;
        else if (mFieldSize <= 60)
            r = 2;
        else
            r = 3;
        for (int x = 0; x < mSize; ++x)
            if (mConstants.isHandicapLine(x))
                for (int y = 0; y < mSize; ++y)
                    if (mConstants.isHandicapLine(y))
                    {
                        Point point = getCenter(x, y);
                        canvas.drawOval(new RectF(point.x - r, point.y - r, point.x + r +1, point.y + r + 1), paint);
                    }
    }
    
    private void drawFields(Context context, Canvas canvas, ConstField[][] field) {
        assert field.length == mSize;
        for (int x = 0; x < mSize; ++x)
        {
            assert field[x].length == mSize;
            for (int y = 0; y < mSize; ++y)
            {
                Point location = getCenter(x, y);
                field[x][y].draw(context, canvas, mFieldSize, location.x, location.y);
            }
        }
    }
    
	public Point getCenter(int x, int y) {
		Point point = getLocation(x, y);
		point.x += mFieldSize / 2;
		point.y += mFieldSize / 2;
		return point;
	}

	public int getFieldSize() {
		return mFieldSize;
	}

	public int getFieldOffset() {
		return mFieldOffset;
	}

	public Point getLocation(int x, int y) {
		Point point = new Point();
		point.x = mFieldOffset + x * mFieldSize;
		point.y = mFieldOffset + (mSize - y - 1) * mFieldSize;
		return point;
	}

	public GoPoint getPoint(Point point) {
		if (mFieldSize == 0)
			return null;
		
		int x = ((int) point.x - mFieldOffset) / mFieldSize;
		int y = ((int) point.y - mFieldOffset) / mFieldSize;
		y = mSize - y - 1;
		if (x >= 0 && x < mSize && y >= 0 && y < mSize) {
			return GoPoint.get(x, y);
		}
		return null;
	}
    
}
