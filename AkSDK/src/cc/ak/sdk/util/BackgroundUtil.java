package cc.ak.sdk.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class BackgroundUtil {
	
	public static LayerDrawable createRoundRect(Context context, float[] radii, int fillColor, int borderColor) {
		return createRoundRect(context, radii, 1F, fillColor, borderColor);
	}
	
	public static LayerDrawable createRoundRect(Context context, float[] radii, float borderSize, int fillColor, int borderColor) {
		ShapeDrawable fillBgDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
		fillBgDrawable.getPaint().setColor(fillColor);
		ShapeDrawable borderBgDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
		borderBgDrawable.getPaint().setColor(borderColor);
		int outPadding = ScreenUtil.toDip(context, borderSize);
		borderBgDrawable.setPadding(outPadding, outPadding, outPadding, outPadding);
		Drawable[] bgDrawables = new Drawable[2];
		bgDrawables[0] = borderBgDrawable;
		bgDrawables[1] = fillBgDrawable;
		return new LayerDrawable(bgDrawables);
	}
	
	public static ShapeDrawable createRoundRect(Context context, float[] radii, int fillColor) {
		ShapeDrawable fillBgDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
		fillBgDrawable.getPaint().setColor(fillColor);
		return fillBgDrawable;
	}
	
}