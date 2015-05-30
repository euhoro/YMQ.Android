package com.ymarq.eu.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ymarq.eu.ymarq.R;

/**
 * Created by eu on 3/19/2015.
 */
//http://stackoverflow.com/questions/11066022/android-overlay-small-check-icon-over-specific-image-in-gridview-or-change-bor
public class MarkableImageView extends ImageView {
    private boolean checked = true;

    public MarkableImageView(Context context) {
        super(context);
    }

public MarkableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        }

public MarkableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        }

public void setChecked(boolean checked) {
        this.checked = checked;
        invalidate();
        }

public boolean isChecked() {
        return checked;
        }

@Override
protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(checked) {
        Bitmap check = BitmapFactory.decodeResource(
                getResources(), R.drawable.ic_envelope);
        int width = check.getWidth();
        int height = check.getHeight();
        int margin = 2;//15
        int x = canvas.getWidth() - width - margin;
        //int y = canvas.getHeight() - height - margin;
        int y = 0;

        //product_item_small2
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPaint(paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(20);
                //canvas.drawText("Some Text", 10, 25, paint);
                //



                canvas.drawBitmap(check, x, y, new Paint());
        }
        }
        }
