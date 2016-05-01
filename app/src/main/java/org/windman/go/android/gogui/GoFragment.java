package org.windman.go.android.gogui;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sf.gogui.game.ConstGameTree;
import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.Game;
import net.sf.gogui.game.GameTree;
import net.sf.gogui.game.Node;
import net.sf.gogui.game.NodeUtil;
import net.sf.gogui.gamefile.DataSource;
import net.sf.gogui.gamefile.GameDataSource;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.Move;

import org.windman.go.R;
import org.windman.go.android.gui.BoardView;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class GoFragment extends Fragment {
	
	private Activity mActivity = null;
	private RelativeLayout mRootView = null;
	
	private Game mGame = null;
	private BoardView mBoardView = null;
	private int mPosition = -1;

	public GoFragment(){
	}
	public GoFragment(int position) {
		mPosition = position;
		Log.i("DetialFragment", "new GoFragment() position=" + position);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity; 
		Log.i("0516", "onAttach() --" + mPosition);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("0516", "onCreate()--" + mPosition);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("0516", "onCreateView()--" + mPosition);
		mRootView = (RelativeLayout) inflater.inflate(R.layout.page_fragment, container, false);
		
		//board view
		mBoardView = (BoardView) mRootView.findViewById(R.id.board);
		mBoardView.setListener(new BoardViewListener());
		mBoardView.setZOrderOnTop(true);
		mBoardView.getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("0516", "onActivityCreated()--" + mPosition);
		initGame();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("0516", "onResume()--" + mPosition);
		if (mBoardView != null) {
			mBoardView.onResume();
		}
		
		onPageChange();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.i("0516", "onPause()--" + mPosition);
		if (mBoardView != null) {
			mBoardView.onPause();
		}
	}

	@Override
	public void onDestroy() {
		Log.i("0516", "onDestroy()--" + mPosition);  
		super.onDestroy();
		if (mBoardView != null) {
			mBoardView.onDestroy();
		}
	}

	private void initGame() {
		mMode = NORMAL_MODE;
		
		int boardSize = 19;//m_prefs.getInt("boardsize", GoPoint.DEFAULT_SIZE);
		GameTree tree = GameDataSource.getInstance(mActivity).getPageByPosition(mPosition);
		mGame = new Game(boardSize);
		mGame.init(tree);
		mBoardView.initWork();
		updateFromGoBoard(null);
	}
	
	public interface Listener {
		void updateBoardBar(View.OnClickListener listener, boolean isTryMode);
		
		void updateComment(String comment);
		
		void updateTitle(String text);
		
		void playSound(int which);
	}
	
	private Listener mListener;
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
	public void onPageChange() {
		if (GoConfig.getInstance(mActivity).readCurrentPagePosition() != mPosition) {
			return;
		}
		
		if (mListener != null) {
			mListener.updateBoardBar(getBoardBarListener(), isTryMode());
			mListener.updateComment(getComment());
			mListener.updateTitle(getTitle());
		}
	}
	
	private String getTitle() {
		StringBuilder builder = new StringBuilder();
		DataSource dataSource = GameDataSource.getInstance(mActivity);
		builder.append(dataSource.getBookName()).append("[").append(mPosition+1).append("/").append(dataSource.getPageCount()).append("]");
		return builder.toString();
	}
	
	public static final int NORMAL_MODE = 0;
	public static final int TRY_MODE = 1;
	private int mMode = NORMAL_MODE;
	
	private boolean isTryMode() {
		return mMode == TRY_MODE;
	}
	
	private void updateMode(int mode) {
		if (mMode == mode) {
			return;
		}
		
		actionBackward(NodeUtil.getDepth(mGame.getCurrentNode()));
		switch (mode) {
		case NORMAL_MODE:
			break;
		case TRY_MODE:
			removeAllTryNode();
			break;
		default:
			break;
		}
		
		mMode = mode;
		if (mListener != null) {
			mListener.updateBoardBar(getBoardBarListener(), isTryMode());
		}
	}
	
	private void removeAllTryNode() {
		Stack<ConstNode> stack = new Stack<ConstNode>();
		stack.push(mGame.getTree().getRootConst());
		while (!stack.isEmpty()) {
			ConstNode node = stack.pop();
			for (int i=0; i<node.getNumberChildren(); i++) {
				ConstNode child = node.getChildConst(i);
				if (child.isTryNode()) {
					if (node instanceof Node && child instanceof Node) {
						((Node)node).removeChild((Node)child);
					}
				} else {
					stack.push(child);
				}
			}
		}
	}
	
	private String getComment() {
		String comment = mGame.getCurrentNode().getComment();
		if (comment != null) {
			return comment;
		}
		
		return null;
	}
	
	private BoardBarListener mBoardBarListener = null;
	public View.OnClickListener getBoardBarListener() {
		if (mBoardBarListener == null) {
			mBoardBarListener = new BoardBarListener();
		}
		return mBoardBarListener; 
	}
	
	private class BoardBarListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(mListener != null) {
				mListener.playSound(GoBookActivity.GAME_SOUND_BUTTON);
			}
			switch(v.getId()) {
			case R.id.try_button:
				updateMode(isTryMode() ? NORMAL_MODE : TRY_MODE);
				break;
			case R.id.gogui_first_button:
			case R.id.redo_button:
				actionBackward(NodeUtil.getDepth(mGame.getCurrentNode()));
				break;
			case R.id.gogui_previous_10_button:
				actionBackward(10);
				break;
			case R.id.gogui_previous_button:
				actionBackward(1);
				break;
			case R.id.gogui_next_button:
				actionForward(1);
				break;
			case R.id.gogui_next_10_button:
				actionForward(10);
				break;
			case R.id.gogui_last_button:
			case R.id.answer_button:
				actionBackward(NodeUtil.getDepth(mGame.getCurrentNode()));
				actionForward(NodeUtil.getNodesLeft(mGame.getCurrentNode()));
				break;
			case R.id.back_button:
				actionBackward(isTryMode() ? 1 : 2);
				break;
			default:
				break;
			}
		}
		
	}
	
	private void actionGotoNode(ConstNode node) {
		mGame.gotoNode(node);
		updateFromGoBoard(null);
	}
	
	private void actionBackward(int n) {
		actionGotoNode(NodeUtil.backward(mGame.getCurrentNode(), n));
	}
	
	private void actionForward(int n) {
		actionGotoNode(NodeUtil.forward(mGame.getCurrentNode(), n));
	}
	
	private class BoardViewListener implements BoardView.Listener {

		@Override
		public void fieldClicked(GoPoint p) {
			boolean valid;
            if (p == null || mGame.getBoard().getColor(p) != GoColor.EMPTY) {
            	valid = false;
            } else if (mGame.getBoard().isSuicide(mGame.getToMove(), p)) {
            	valid = false;
            } else if (mGame.getBoard().isKo(p)) {
            	valid = false;
            } else {
            	Move move = Move.get(mGame.getToMove(), p);
	            valid = humanMoved(move);
	            if (valid) {
	            	generateMove();
	            }
            }
            
			if(mListener != null) {
				mListener.playSound(valid ? GoBookActivity.GAME_SOUND_GO : GoBookActivity.GAME_SOUND_WRONG);
			}
			
			updateFromGoBoard(p);
		}

		@Override
		public Rect getUsedRect() {
			int boardSize = mGame.getBoard().getSize();
			Rect result = new Rect(boardSize, boardSize, -1, -1);
			
			List<GoPoint> allGoPoint = new ArrayList<GoPoint>();
			
			ConstGameTree tree = mGame.getTree();
			if (mGame.getTree() == null || tree.getRootConst() == null || !tree.getRootConst().hasChildren()) {
				return null;
			}
			
			//iterator tree
			Stack<ConstNode> stack = new Stack<ConstNode>();
			stack.push(tree.getRootConst());
			while (!stack.isEmpty()) {
				ConstNode node = stack.pop();
				for (int i=0; i<node.getNumberChildren(); i++) {
					ConstNode child = node.getChildConst(i);
					if (child.isTryNode()) {
						//node.removeChild(child);
					} else {
						stack.push(child);
					}
				}
				
				Move move = node.getMove();
				if (move != null) {
					GoPoint point = move.getPoint();
					if (point != null) {
						allGoPoint.add(point);
					}
				}
			}
			
			//iterator setup point
			for(GoPoint point : tree.getRootConst().getSetup(GoColor.BLACK)) {
				allGoPoint.add(point);
			}
			
			for(GoPoint point : tree.getRootConst().getSetup(GoColor.WHITE)) {
				allGoPoint.add(point);
			}
			
			for(GoPoint point : allGoPoint) {
				int x = point.getX();
				int y = point.getY();
				if (x < result.left) {
					result.left = x;
				}
				
				if (y < result.top) {
					result.top = y;
				}
				
				if (x > result.right) {
					result.right = x;
				}
				
				if (y > result.bottom) {
					result.bottom = y;
				}
			}
			
			return result;
		}
		
	}
	
	private boolean humanMoved(Move move) {
		if (isTryMode()) {
			mGame.play(move, isTryMode());
			return true;
		} else {
			ConstNode matchNode = NodeUtil.findNextByMove(mGame.getCurrentNode(), move);
			if (matchNode != null) {
				mGame.gotoNode(matchNode);
				return true;
			}
		}
		
		return false;
	}
	
	private void generateMove() {
		mGame.gotoNode(NodeUtil.forward(mGame.getCurrentNode(), 1));
	}
	
	private void updateFromGoBoard(GoPoint clickedPoint) {
		BoardUtil.updateFromGoBoard(mBoardView, mGame.getBoard(), clickedPoint, true, true);
		if (mListener != null && GoConfig.getInstance(mActivity).readCurrentPagePosition() == mPosition) {
			mListener.updateComment(getComment());
		}
	}
	
}
