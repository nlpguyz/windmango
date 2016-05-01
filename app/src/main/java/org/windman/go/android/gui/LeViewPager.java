package org.windman.go.android.gui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Transformation;

public class LeViewPager extends ViewPager {
	private static final String TAG = "LeViewPager";
	private Camera mCamera = new Camera();
	private Context mContext ;
	private String mRandomEffect ;
	private String mPrefChoiceEffectKey ;
	
	public LeViewPager(Context context) {
		super(context) ;
		mContext = context ;
		setStaticTransformationsEnabled(true) ;
		Log.i(TAG, "LeViewPager new LeViewPager()");
	}

	public LeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs) ;
		mContext = context ;
		setStaticTransformationsEnabled(true) ;
		Log.i(TAG, "LeViewPager new LeViewPager()");
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}
	
	public ActionListener mActionListener = null;
	public void setActionListener(ActionListener listener){
		mActionListener = listener;
	}
	
	public interface ActionListener{
		boolean doTouch(MotionEvent event);
	}
	
    private float mLastMotionX;
    private float mLastMotionY;
    private static final float MOVE_DISTANCE = 40.0f ;
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event) {
//		String value = PersonalSettings.getEffect();
//		if (PersonalSettings.EFFECT_BOOK.equals(value)) {
////			float x = event.getX();
////			float y = event.getY();
////			if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
////				mLastMotionX = x;
////				mLastMotionY = y;
////				
////			}else if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
////				final float xDiff = Math.abs(x - mLastMotionX);
////				final float yDiff = Math.abs(y - mLastMotionY);
////				if( xDiff > MOVE_DISTANCE && xDiff > yDiff ){
////					return true;
////				}
////			}
//			return true;
//			
//		}else{
//			return super.onInterceptTouchEvent(event);
//		}
//	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		String value = PersonalSettings.getEffect();
//		if (PersonalSettings.EFFECT_BOOK.equals(value)) {
//			Log.i("touch", "LeViewPager onTouchEvent()--0 mActionListener=" + mActionListener);
//			if (mActionListener != null) {
//				return mActionListener.doTouch(event);
//			}
//			return false;
//			
//		} else {
//			return super.onTouchEvent(event);
//		}
//		
//	}

	public void setPrefChoiceEffectKey(String key) {
		mPrefChoiceEffectKey = key ;
	}
	
	public void setRandomEffectValue(String randomEffect) {
		mRandomEffect = randomEffect ;
	}
	
	@Override
    public boolean getChildStaticTransformation(View child, Transformation t) {
//		String effectType = PersonalSettings.getEffect();
//		
//		if( TextUtils.isEmpty(effectType) ) {
//			return false ;
//		} else {
//			effectType = effectType.trim() ;
//		}
//		
//		if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_RANDOM) ) {
//			if( TextUtils.isEmpty(mRandomEffect) ) {
//				mRandomEffect = PersonalSettings.getRandomEffect() ;
//			}
//			
//			effectType = mRandomEffect ;
//		}
//		
//		if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_SCALE) ) {
//			return getScaleTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_ROTATE) ) {
//			return getZRotateTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_BULLDOZE) ) {
//			return getBullDozeTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_BOUNCE) ) {
//			return getBounceTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_ROLL) ) {
//			return getRollTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_WILD) ) {
//			return getWildTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_CUBE) ) {
//			return getCubeTransformation(child, t);
//			
//		} else if( effectType.equalsIgnoreCase(PersonalSettings.EFFECT_WAVE) ) {
//			return getWaveTransformation(child, t);
//		}
		
		return false ;
	}
	
	   private boolean getScaleTransformation(View child, Transformation t) {
	    final int screenLeftX = 0;
	    final int screenRightX = getWidth();
	    final int childLeft = child.getLeft() - getScrollX() ;
	    final int childWidth = child.getWidth();
	    final int childRight = childLeft + childWidth  ;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    if(childLeft < screenLeftX && childRight > screenLeftX) {
		    final float ratio = ((float)childRight - screenLeftX) / childWidth;
		    t.setAlpha(ratio);	
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
		    
	    } else if(childRight > screenRightX && childLeft < screenRightX) {
		    final float ratio = 1.0f - ((float)childRight - screenRightX) / childWidth;
		    t.setAlpha(ratio);
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
		    
	    } else if (getScrollX() < 0) { // we are moving between 1 and the last screen
		    int left = getScrollX() + childWidth;
		    final float ratio = 1.0f - (float)left / childWidth;
		    t.setAlpha(ratio);	
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
		    
	    } else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;	
		    final float ratio =1.0f -  (float)left / childWidth;
		    t.setAlpha(ratio);
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
	    }

	    return false;
    }
    
    private boolean getZRotateTransformation(View child, Transformation t) {
	    final int screenLeftX = 0;
	    final int screenRightX = getWidth();
	    final int childLeft = child.getLeft() - getScrollX() ;
	    final int childWidth = child.getWidth();
	    final int childHeight = child.getHeight();
	    final int childRight = childLeft + childWidth  ;
	    final int toDegrees = 90;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    if(childLeft < screenLeftX && childRight > screenLeftX) {
		    final float ratio = ((float)childRight - screenLeftX) / childWidth;
		    float degrees = toDegrees * (1 - ratio);

		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;
	    } else if(childRight > screenRightX && childLeft < screenRightX) {
		    final float ratio = 1.0f - ((float)childRight - screenRightX) / childWidth;
		    float degrees = toDegrees * (ratio - 1);

		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;
	    } else if (getScrollX() < 0) { // we are moving between 1 and the last screen

		    int left = getScrollX() + child.getWidth();
		    final float ratio = ((float)left) / childWidth;
		    float degrees = toDegrees * ratio;

		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;

	    } else if (getScrollX() > child.getLeft()) { // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    final float ratio = 1.0f - (float)left/ childWidth;
		    float degrees = toDegrees * (ratio - 1);
		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;
	    }
	    
	    return false;
    }
    
    private boolean getBounceTransformation(View child, Transformation t) {
    	int screenLeftX = 0;
	    int screenRightX = getWidth();
	    int childLeft = child.getLeft() - getScrollX();
	    int childWidth = child.getWidth();
	    int childHeight = child.getHeight();
	    int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    
	    if (childLeft < screenLeftX && childRight > screenLeftX) {
	    	t.getMatrix().postTranslate(0, (float) childLeft * childHeight / childWidth);
	    	return true;
	    	
	    } else if (childRight > screenRightX && childLeft < screenRightX) {
	    	t.getMatrix().postTranslate(0, -(float) childLeft * childHeight / childWidth);
	    	return true;
	    	
	    } else if (getScrollX() < 0) { // we are moving between 1 and the last screen
	    	int left = getScrollX() + child.getWidth();
	    	t.getMatrix().postTranslate(0, -(float) left* childHeight / childWidth);
	    	return true;
	    	
	    } else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
	    	int left = childWidth - getScrollX() + leftEdge;
	    	t.getMatrix().postTranslate(0, -(float) left* childHeight / childWidth);
	    	return true;
	    }
	    
	    return true;
    }
    
    private boolean getBullDozeTransformation(View child , Transformation t) {
    	Log.i("hjk_pager_test", "getBullDozeTransformation>>>");
	    final int screenLeftX = 0;
	    final int screenRightX = getWidth();

	    final int childLeft = child.getLeft() - getScrollX()+1;
	    final int childWidth = child.getWidth();
	    final int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    if (childLeft < screenLeftX && childRight > screenLeftX) {
		    t.getMatrix().setScale((float) (childRight - screenLeftX) / childWidth, 1, -childLeft, 0);
		    return true;
		    
	    } else if (childRight > screenRightX && childLeft < screenRightX) {
		    t.getMatrix().setScale((float) (screenRightX - childLeft) / childWidth, 1);
		    return true;
		    
	    } else if (getScrollX() < 0) { // we are moving between 1 and the last screen
		    int left = getScrollX() + childWidth;
		    int right = left + childWidth;
		    t.getMatrix().setScale((float) (right - screenLeftX) / childWidth, 1, Math.abs(left), 0);
		    return true;
		    
	    } else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    t.getMatrix().setScale((float) (screenRightX - left) / childWidth, 1);
		    return true;
	    }

	    return true;
    }
    
    private boolean getRollTransformation(View child, Transformation t) {
	  final int screenLeftX = 0;
	  final int screenRightX = getWidth();

	  final int childLeft = child.getLeft() - getScrollX();
	  final int childWidth = child.getWidth();
	  final int childHeight = child.getHeight();
	  final int childRight = childLeft + childWidth;
	  View lastchild = getChildAt(getChildCount() - 1);
	  int leftEdge = lastchild.getLeft();

	  if ((childLeft < screenLeftX && childRight > screenLeftX) || 
			  (childRight > screenRightX && childLeft < screenRightX)) {
		  t.getMatrix().setRotate((float) childLeft / childWidth * 100, childWidth / 2f, childHeight / 2f);
		  
	  } else if (getScrollX() < 0) { // we are moving between 1 and the last screen
		  int left = getScrollX() + childWidth;
		  t.getMatrix().setRotate((float) -left/ childWidth * 100, childWidth / 2f, childHeight / 2f);
		  return true;
		  
	  } else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
		  int left = childWidth - getScrollX() + leftEdge;
		  t.getMatrix().setRotate((float) left/ childWidth * 100, childWidth / 2f, childHeight / 2f);
		  return true;
	  }
	  
	  return true;
    }
    
    private boolean getWildTransformation(View child, Transformation t) {
	    final int screenLeftX = 0;
	    final int screenRightX = getWidth();

	    final int childLeft = child.getLeft() - getScrollX();
	    final int childWidth = child.getWidth();
	    final int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    if ((childLeft < screenLeftX && childRight > screenLeftX) ||
			    (childRight > screenRightX && childLeft < screenRightX)) {
		    t.getMatrix().setRotate(-(float) childLeft / childWidth * 45, childWidth / 2f, 0);
		    
		    	    } else if (getScrollX() < 0) { // we are moving between 1 and the last screen
		    int left = getScrollX() + childWidth;
		    t.getMatrix().setRotate((float) left/ childWidth * 45, childWidth / 2f, 0);
		    return true;
		    
	    } else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    t.getMatrix().setRotate((float) -left/ childWidth * 45, childWidth / 2f, 0);
		    return true;
	    }

	    return true;
    }
	
	private boolean getCubeTransformation(View child, Transformation t) {
		final int screenLeftX = 0;
		final int screenRightX = getWidth();
		
		final int childLeft = child.getLeft() - getScrollX();
		final int childWidth = child.getWidth();
		final int childHeight = child.getHeight();
		final int childRight = childLeft + childWidth;
		final int degreeY = 90;
		View lastchild = getChildAt(getChildCount() - 1);
		int leftEdge = lastchild.getLeft();
		if (childWidth == 0) {
        	return false;
        }
		
		if (childLeft < screenLeftX && childRight > screenLeftX) {
			Matrix matrix = t.getMatrix();
			Camera camera = new Camera();
			camera.save();
			camera.rotateY(-degreeY * childLeft / childWidth-3);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-childWidth, -(float) childHeight / 2);
			matrix.postTranslate(childWidth, (float) childHeight / 2);
			return true;
			
		} else if (childRight > screenRightX && childLeft < screenRightX) {
			Matrix matrix = t.getMatrix();
			Camera camera = new Camera();
			camera.save();
			camera.rotateY(-degreeY * childLeft / childWidth);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(0, -(float) childHeight / 2);
			matrix.postTranslate(0, (float) childHeight / 2);
			return true;
			
		} else if (getScrollX() < 0) { // we are moving between 1 and the last screen
			int left = getScrollX() + childWidth;
			Matrix matrix = t.getMatrix();
			Camera camera = new Camera();
			camera.save();
			camera.rotateY(degreeY * left/ childWidth);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-childWidth, -(float) childHeight / 2);
			matrix.postTranslate(childWidth, (float) childHeight / 2);
			return true;
			
		} else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
			int left = childWidth - getScrollX() + leftEdge;
			Matrix matrix = t.getMatrix();
			Camera camera = new Camera();
			camera.save();
			camera.rotateY(-degreeY * left/ childWidth);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(0, -(float) childHeight / 2);
			matrix.postTranslate(0, (float) childHeight / 2);
			return true;
		}
		
		return true;
	}
	
	private boolean getWaveTransformation(View child, Transformation t) {
		final int screenLeftX = 0;
		final int screenRightX = getWidth();

		final int childLeft = child.getLeft() - getScrollX();
		final int childWidth = child.getWidth();
		final int childHeight = child.getHeight();
		final int childRight = childLeft + childWidth;
		View lastchild = getChildAt(getChildCount() - 1);
		int leftEdge = lastchild.getLeft();

		if (childLeft < screenLeftX && childRight > screenLeftX) {
			Camera camera = new Camera();
			camera.save();
			camera.translate(0, -((float) (childHeight * 0.5) * (screenLeftX - childLeft) / (float) childHeight), -childLeft);
			camera.getMatrix(t.getMatrix());
			camera.restore();
			return true;
			
		} else if (childRight > screenRightX && childLeft < screenRightX) {
			Camera camera = new Camera();
			camera.save();
			camera.translate(0, -((float) (childHeight * 0.5) * (childLeft - screenLeftX) / (float) childHeight), childLeft);
			camera.getMatrix(t.getMatrix());
			camera.restore();
			return true;
			
		} else if (getScrollX() < 0) { // we are moving between 1 and the last screen
			int left = getScrollX() + childWidth;
			Camera camera = new Camera();
			camera.save();
			camera.translate(0, -((float) (childHeight * 0.5) * (screenLeftX  + left) / (float) childHeight), left);
			camera.getMatrix(t.getMatrix());
			camera.restore();
			return true;
			
		} else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
			int left = childWidth - getScrollX() + leftEdge;
			Camera camera = new Camera();
			camera.save();
			camera.translate(0, -((float) (childHeight * 0.5) * (left - screenLeftX) / (float) childHeight), left);
			camera.getMatrix(t.getMatrix());
			camera.restore();
			return true;
		}
		
		return true;
    }
}
