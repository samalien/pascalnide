package com.duy.pascal.compiler.view.graph_lib;

import android.graphics.Canvas;

/**
 * Created by Duy on 02-Mar-17.
 */

public class ArcObject extends GraphObject {
    private int x, y, stAngle, enAngle, radius;

    public ArcObject(int x, int y, int stAngle, int enAngle, int radius) {
        this.x = x;
        this.y = y;
        this.stAngle = stAngle;
        this.enAngle = enAngle;
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas) {
    }
}
