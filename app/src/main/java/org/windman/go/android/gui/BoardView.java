package org.windman.go.android.gui;

import org.windman.go.android.boardpainter.BoardPainter;
import org.windman.go.android.boardpainter.ConstField;
import org.windman.go.android.boardpainter.Field;
import org.windman.go.android.gogui.BoardUtil;
import org.windman.go.android.gogui.GoBookActivity;
import org.windman.go.android.gogui.GoConfig;

import net.sf.gogui.go.BoardConstants;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.graphics.PorterDuff;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback, ConstGuiBoard, OnTouchListener {

	private BoardPainter mPainter = null;
	private int mSize;
	private int mScreenWidth;
    private BoardConstants mConstants;
    private Field mField[][];
    private Listener mListener;
    
    private static final int SCALE_BITMAP_VALUE = 2;
    private static final int TR_BITMAP_VALUE = -400;
    
    private GoPoint mLastMove;
	
    /** Callback for clicks on a field. */
    public interface Listener
    {
        /** Callback for click on a field.
            This callback is triggered with mouse clicks or the enter key
            if the cursor is shown.
            @param p The point clicked.
            @param modifiedSelect Modified select. True if the click was a
            double click or with the right mouse button or if a modifier key
            (Ctrl, Alt, Meta) was pressed while clicking, as long as it was
            not a (platform-dependent) popup menu trigger. */
        void fieldClicked(GoPoint p);
        
        Rect getUsedRect();
    }
    
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSize(19);
		setOnTouchListener(this);
		getHolder().addCallback(this);
		onCreate();
	}
	
	public BoardView(Context context){
		super(context);
		initSize(19);
		setOnTouchListener(this);
		getHolder().addCallback(this);
		onCreate();
	}
	
    @Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
    
    private void doDraw(Canvas canvas) {
    	canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); 
        Bitmap bitmap = mPainter.draw(mField);
        mTranslatePoint = getTranslatePoint(mVisiableRect, bitmap.getWidth());
        canvas.drawBitmap(bitmap, mTranslatePoint.x, mTranslatePoint.y, null);
        if (bitmap != null && !bitmap.isRecycled()) {
        	bitmap.recycle();
        	bitmap = null;
        }
    }
    
	public void refresh(){
		refresh(true);
	}
	
	private void refresh(boolean run){
        synchronized (mDrawingThread) {
            mDrawingThread.mRunning = run;
            mDrawingThread.notify();
        }
	}
	
	private DrawingThread mDrawingThread;
	
	public void onCreate() {
        // This is the thread that will be drawing to our surface.
        mDrawingThread = new DrawingThread();
        mDrawingThread.start();
    }
    
	public void onPause() {
        // Make sure the drawing thread is not running while we are paused.
		refresh(false);
    }

    public void onResume() {
        // Let the drawing thread resume running.
    	refresh(true);
    }
    
    public void onDestroy() {
        // Make sure the drawing thread goes away.
        synchronized (mDrawingThread) {
            mDrawingThread.mQuit = true;
            mDrawingThread.notify();
        }
    }
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

    public void surfaceCreated(SurfaceHolder holder) {
        // Tell the drawing thread that a surface is available.
        synchronized (mDrawingThread) {
            mDrawingThread.mSurface = holder;
            mDrawingThread.notify();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // We need to tell the drawing thread to stop, and block until
        // it has done so.
        synchronized (mDrawingThread) {
            mDrawingThread.mSurface = holder;
            mDrawingThread.notify();
            while (mDrawingThread.mActive) {
                try {
                    mDrawingThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	class DrawingThread extends Thread {
		SurfaceHolder mSurface;
        boolean mRunning;
        boolean mActive;
        boolean mQuit;

		public DrawingThread() { }

		@Override
		public void run() {
			while (true) {
                synchronized (this) {
                    while (mSurface == null || !mRunning) {
                        if (mActive) {
                            mActive = false;
                            notify();
                        }
                        if (mQuit) {
                            return;
                        }
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    
                    if (!mActive) {
                        mActive = true;
                        notify();
                    }
                    
                    //do draw...
                    Canvas canvas = mSurface.lockCanvas();
                    if (canvas == null) {
                        Log.i("WindowSurface", "Failure locking canvas");
                        continue;
                    }
                    doDraw(canvas);
                    mSurface.unlockCanvasAndPost(canvas);
                    
                    mRunning = false;
                }
			}
		}
		
	}
    
	private Rect mVisiableRect = null;
    public void initWork() {
        if (mListener != null) {
        	mVisiableRect = mListener.getUsedRect();
        }
        
        Log.i("0515_1", "initWork() mVisiableRect=" + mVisiableRect);
        mScaleSize = getScaleSize(mSize, mVisiableRect);
        mPainter = new BoardPainter(getContext(), (int)(mScreenWidth*getScaleValue(mScaleSize)), 19);
    }

    private enum LookHere {
    	GO_LEFT_TOP,
    	GO_LEFT_BOTTOM,
    	GO_RIGHT_TOP,
    	GO_RIGHT_BOTTOM, 
    	GO_MIDDLE
    }
    
    private enum SCALE_SIZE {
    	SIZE_X1,
    	SIZE_X1_5,
    	SIZE_X2
    }
    
    private static final float SCALE_X1 = 1.0F;
    private static final float SCALE_X1_5 = 1.5F;
    private static final float SCALE_X2 = 2.0F;
    
    private SCALE_SIZE mScaleSize = SCALE_SIZE.SIZE_X1;
    private Point mTranslatePoint = new Point();
    
    private LookHere lookWhere(Rect r) {
    	if (r == null) {
    		return LookHere.GO_MIDDLE;
    	}
    	int dxLeft = r.left - 0;
    	int dxRight = mSize - r.right - 1;
    	int dxTop = mSize - r.top - 1;
    	int dxBottom = r.bottom - 0;
    	
    	Log.i("BoardView", "dxLeft=" + dxLeft + ", dxRight=" + dxRight + ", dxTop=" + dxTop + ", dxBottom=" + dxBottom);
    	if (dxLeft == dxRight || dxTop == dxBottom) {
    		return LookHere.GO_MIDDLE;
    	}
    	
    	if (dxLeft < dxRight) {
    		return dxTop < dxBottom ? LookHere.GO_LEFT_TOP : LookHere.GO_LEFT_BOTTOM;
    	} else {
    		return dxTop < dxBottom ? LookHere.GO_RIGHT_TOP : LookHere.GO_RIGHT_BOTTOM;
    	}
    }
    
    private SCALE_SIZE getScaleSize(int boardSize, Rect r) {
    	if (r == null) {
    		return SCALE_SIZE.SIZE_X1;
    	}
    	LookHere here = lookWhere(r);
    	
    	int w = r.right - r.left + 1;
    	int h = r.bottom - r.top + 1;
    	int max;
    	
    	if (w >= h) {
    		if (here == LookHere.GO_LEFT_TOP || here == LookHere.GO_LEFT_BOTTOM) {
    			max = r.left + w;
    		} else {
    			max = (boardSize - r.right - 1) + w;
    		}
    	} else {
    		if (here == LookHere.GO_LEFT_TOP || here == LookHere.GO_RIGHT_TOP) {
    			max = (boardSize - r.bottom - 1) + h;
    		} else {
    			max = r.top + h;
    		}
    	}
    	
    	if (max <= ((int)(boardSize/SCALE_X2))) {
    		return SCALE_SIZE.SIZE_X2;
    	} else if (max <= ((int)(boardSize/SCALE_X1_5))) {
    		return SCALE_SIZE.SIZE_X1_5;
    	}
    	
        return SCALE_SIZE.SIZE_X1;
    }
    
    private float getScaleValue(SCALE_SIZE size) {
    	float result;
    	switch (size) {
    	case SIZE_X1_5:
    		result = SCALE_X1_5;
    		break;
    		
    	case SIZE_X2:
    		result = SCALE_X2;
    		break;
    		
    	case SIZE_X1:
    	default:
    		result = SCALE_X1;
    		break;
    	}
    	
    	return result;
    }
    
    private Point getTranslatePoint(Rect r, int bitmapWidth) {
    	Point point = new Point();
    	
    	if (mScaleSize == SCALE_SIZE.SIZE_X1) {
    		return point;
    	}
    	
    	LookHere here = lookWhere(r);
    	if (here == LookHere.GO_RIGHT_BOTTOM || here == LookHere.GO_RIGHT_TOP) {
    		point.x -= bitmapWidth - mScreenWidth;
    	}
    	
    	if (here == LookHere.GO_RIGHT_BOTTOM || here == LookHere.GO_LEFT_BOTTOM) {
    		point.y -= bitmapWidth - mScreenWidth;
    	}
    	
    	Log.i("BoardPainter", "here=" + here + "point.x=" + point.x + ", point.y =" + point.y );
    	return point;
    }
    
	/** Change the board size.
    @param size The new board size. */
	public void initSize(int size) {
        assert size > 0 && size <= GoPoint.MAX_SIZE;
        mSize = size;
        mConstants = BoardConstants.get(size);
        mField = new Field[size][size];
		for (int y = size - 1; y >= 0; --y) {
			for (int x = 0; x < size; ++x) {
				mField[x][y] = new Field();
			}
		}
		
        mScreenWidth = GoConfig.getInstance(getContext()).getScreenWidth();
		initWork();
	}
	
	private Field getField(GoPoint p) {
		assert p != null;
		return mField[p.getX()][p.getY()];
	}
	
	public void setColor(GoPoint point, GoColor color) {
		if (point == null || color == null) {
			return;
		}
		getField(point).setColor(color);
	}
	
	public void cleanAllField() {
		for (int y = mSize - 1; y >= 0; --y) {
			for (int x = 0; x < mSize; ++x) {
				mField[x][y].setLabel("");
				mField[x][y].setCursor(false);
			}
		}
	}
	

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		final int action = event.getAction();
		if (action == MotionEvent.ACTION_UP) {
			final int x = (int) event.getX() - mTranslatePoint.x;
			final int y = (int) event.getY() - mTranslatePoint.y;
			Log.i("BoardView", "onTouchEvent()...action=" + action + ", " + x
					+ "," + y);
			fieldClicked(mPainter.getPoint(new Point(x, y)));
		}
		return true;
	}
	
    public void setListener(Listener listener)
    {
    	mListener = listener;
    }
    
	private void fieldClicked(GoPoint p) {
		if (mListener != null) {
			mListener.fieldClicked(p);
			refresh();
		}
	}
	
	/**
	 * Mark point of last move on the board. The last move marker will be
	 * removed, if the parameter is null.
	 */
	public void markLastMove(GoPoint point) {
		clearLastMove();
		mLastMove = point;
		if (mLastMove != null) {
			Field field = getField(mLastMove);
			field.setLastMoveMarker(true);
			postInvalidate();
			mLastMove = point;
		}
	}
	
	private void clearLastMove() {
		if (mLastMove != null) {
			Field field = getField(mLastMove);
			field.setLastMoveMarker(false);
			postInvalidate();
			mLastMove = null;
		}
	}
	
	/**
	 * Set label.
	 * 
	 * @param point
	 *            The point.
	 * @param label
	 *            The label. Should not be longer than 3 characters to avoid
	 *            clipping. null to remove label.
	 */
	public void setLabel(GoPoint point, String label) {
		Field field = getField(point);
		if ((field.getLabel() == null && label != null)
				|| (field.getLabel() != null && !field.getLabel().equals(label))) {
			field.setLabel(label);
			postInvalidate();
		}
	}
	
	public void setCursor(GoPoint p) {
		getField(p).setCursor(true);
	}
	
	@Override
	public int getBoardSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ConstField getFieldConst(GoPoint p) {
		return getField(p);
	}

	@Override
	public String getLabel(GoPoint point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getMark(GoPoint point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMarkCircle(GoPoint point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMarkSquare(GoPoint point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMarkTriangle(GoPoint point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getSelect(GoPoint point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getShowCursor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getShowGrid() {
		// TODO Auto-generated method stub
		return false;
	}

}
