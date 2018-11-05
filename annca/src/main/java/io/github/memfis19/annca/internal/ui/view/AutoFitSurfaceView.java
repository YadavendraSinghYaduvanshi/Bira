package io.github.memfis19.annca.internal.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.github.memfis19.annca.internal.ui.zoom.ZoomController;

/**
 * Created by memfis on 7/6/16.
 */
@SuppressWarnings("deprecation")
@SuppressLint("ViewConstructor")
public class AutoFitSurfaceView extends SurfaceView implements ZoomController {

    private final static String TAG = "AutoFitSurfaceView";

    private final SurfaceHolder surfaceHolder;

    private int ratioWidth;
    private int ratioHeight;

    private Camera mCamera;
    float mDist = 0;
    Paint paint;
    TextView tv_zoom;

    public AutoFitSurfaceView(@NonNull Context context, SurfaceHolder.Callback callback, Camera camera, boolean showGrid) {
        super(context);

        this.surfaceHolder = getHolder();

        this.surfaceHolder.addCallback(callback);
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        this.mCamera = camera;
        paint = new Paint();
        tv_zoom = MyTextview.zoomTextView;
        if (showGrid) {
            this.setWillNotDraw(false);
        }

    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated fromList the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        ratioWidth = width;
        ratioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (0 == ratioWidth || 0 == ratioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * (ratioWidth / (float) ratioHeight)) {
                setMeasuredDimension(width, (int) (width * (ratioWidth / (float) ratioHeight)));
            } else {
                setMeasuredDimension((int) (height * (ratioWidth / (float) ratioHeight)), height);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();

        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE
                    && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            // zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            // zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);

        //zoom text
        setZoomVisibility(zoom != 0);
        if (zoom > 0) {
            double data = Math.round(zoom * 1.0) / 10.0;
            setZoomData(data);
        }


        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes
                .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void setZoomVisibility(boolean visibility) {

        if (tv_zoom != null)
            if (visibility) {
                tv_zoom.setVisibility(View.VISIBLE);
            } else {
                tv_zoom.setVisibility(View.INVISIBLE);
            }
    }

    @Override
    public void setZoomData(double zoom) {
        if (tv_zoom != null)
            tv_zoom.setText("x " + zoom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //  Find Screen size first
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float screenWidth = metrics.widthPixels;
        float screenHeight = metrics.heightPixels;

        //  Set paint options
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 255, 125, 000));

        canvas.drawLine((screenWidth / 110) * 27, 0, (screenWidth / 110) * 27, screenHeight, paint);
        canvas.drawLine((screenWidth / 110) * 72, 0, (screenWidth / 110) * 72, screenHeight, paint);
        System.out.println("small==" + (screenWidth / 100) * 27 + "  large===" + (screenWidth / 100) * 63);
        canvas.drawLine(0, (screenHeight / 11) * 3, screenWidth, (screenHeight / 11) * 3, paint);
        canvas.drawLine(0, (screenHeight / 11) * 8, screenWidth, (screenHeight / 11) * 8, paint);
    }
}
