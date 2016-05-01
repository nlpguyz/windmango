package org.windman.go.android.boardpainter;

// ConstField.java

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import net.sf.gogui.go.GoColor;

/** Const functions of Field.
    @see Field */
public interface ConstField
{
    void draw(Context context, Canvas canvas, int size, int x, int y);

    GoColor getColor();

    boolean getCursor();

    boolean getCrossHair();

    Color getFieldBackground();

    boolean getMark();

    boolean getMarkCircle();

    boolean getMarkSquare();

    boolean getMarkTriangle();

    boolean getSelect();

    GoColor getGhostStone();

    String getLabel();

    GoColor getTerritory();

    boolean isInfluenceSet();
}
