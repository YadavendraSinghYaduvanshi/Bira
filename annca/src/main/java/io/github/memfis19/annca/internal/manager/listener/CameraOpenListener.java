package io.github.memfis19.annca.internal.manager.listener;

import android.hardware.Camera;

import io.github.memfis19.annca.internal.utils.Size;

/**
 * Created by memfis on 8/14/16.
 */
public interface CameraOpenListener<CameraId, SurfaceListener> {
    void onCameraOpened(CameraId openedCameraId, Size previewSize, SurfaceListener surfaceListener, Camera camera);

    void onCameraReady();

    void onCameraOpenError();
}
