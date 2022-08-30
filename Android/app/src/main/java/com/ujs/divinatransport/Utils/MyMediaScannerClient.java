package com.ujs.divinatransport.Utils;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;

public class MyMediaScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

    private final String mPath;
    public MediaScannerConnection mConnection;
    private final OnMediaScanned mFileScannedCallback;
    private final Handler mHandler;

    public MyMediaScannerClient(String path, Handler handler, OnMediaScanned fileScannedCallback) {
        mPath = path;
        mFileScannedCallback = fileScannedCallback;
        mHandler = handler;
    }

    @Override
    public void onMediaScannerConnected() {
        mConnection.scanFile(mPath, null);
    }

    @Override
    public void onScanCompleted(String path, final Uri uri) {

        mConnection.disconnect();
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mFileScannedCallback.mediaScanned(uri);
            }
        });
    }
}