package com.tda.gairoutes.ui.gfx.map;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;

import com.tda.gairoutes.general.AppAdapter;

import org.osmdroid.bonuspack.overlays.Polyline;

/**
 * Created by Alexey on 9/13/2015.
 */
public class PathUtil {

    public static final float PATH_WIDTH = 7f;
    public static final float PATH_SPACING = PATH_WIDTH * 3f;
    public static final float PATH_OFFSET = 0f;

    private PathUtil() {}

    public static Polyline getRoutePath() {
        Polyline routePath = new Polyline(AppAdapter.context());
        routePath.setColor(Color.RED);
        routePath.getPaint().setPathEffect(new PathDashPathEffect(makePathDash(PATH_WIDTH), PATH_SPACING, PATH_OFFSET,
                PathDashPathEffect.Style.ROTATE));
        return routePath;
    }

    private static Path makePathDash(float width) {
        Path p = new Path();
        p.moveTo(-width/2, 0);
        p.lineTo(0, -width/2);
        p.lineTo(-width, -width/2);
        p.lineTo(-width*2, 0);
        p.lineTo(-width, width / 2);
        p.lineTo(0, width / 2);
        return p;
    }

    private static Path makePathLongDash(float width) {
        Path p = new Path();
        p.moveTo(0, width/4);
        p.lineTo(-width, width/4);
        p.lineTo(-width, width/2);
        p.lineTo(-width*3/2, 0);
        p.lineTo(-width, -width/2);
        p.lineTo(-width, -width/4);
        p.lineTo(0, -width/4);
        return p;
    }
}
