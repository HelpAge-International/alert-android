package org.alertpreparedness.platform.alert.utils;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;

/**
 * ==============================
 * Created by Fei
 * Dated: 17/11/2016
 * Email: fei@rolleragency.co.uk
 * Copyright Roller Agency
 * ==============================
 */

public class ColorUtils {

    public static ColorFilter getMatrixFilterForSolid(int color) {
        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;
        float[] matrix = {0,0,0,0,red,
                0,0,0,0,green,
                0,0,0,0,blue,
                0,0,0,1,0};
        return new ColorMatrixColorFilter(matrix);
    }

    public static int transparentColour(int colour) {
        int r = (Color.red(colour) << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        int g = (Color.green(colour) << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        int b = (Color.blue(colour) & 0x000000FF); //Mask out anything not blue.
        return 0x01000000 | r | g | b;
    }

}
