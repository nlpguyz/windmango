package org.windman.go.android.gui;

// ConstGuiBoard.java


import org.windman.go.android.boardpainter.ConstField;

import net.sf.gogui.go.GoPoint;

/** Const functions of gui.GuiBoard.
    @see GuiBoard */
public interface ConstGuiBoard
{
    int getBoardSize();

    ConstField getFieldConst(GoPoint p);

    //Dimension getFieldSize();

    String getLabel(GoPoint point);

    //Point getLocationOnScreen(GoPoint point);

    boolean getMark(GoPoint point);

    boolean getMarkCircle(GoPoint point);

    boolean getMarkSquare(GoPoint point);

    boolean getMarkTriangle(GoPoint point);

    boolean getSelect(GoPoint point);

    boolean getShowCursor();

    boolean getShowGrid();

    int getWidth();
}
