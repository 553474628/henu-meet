package com.slj.meet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.remote.DownloadFileUrlConnectionImpl;
import es.voghdev.pdfviewpager.library.util.FileUtil;
import java.io.File;

public class MyRemotePDFViewPager extends ViewPager implements DownloadFile.Listener {
    protected Context context;

    protected DownloadFile.Listener listener;

    public MyRemotePDFViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.context = paramContext;
        init(paramAttributeSet);
    }

    public MyRemotePDFViewPager(Context paramContext, String paramString, DownloadFile.Listener paramListener) {
        super(paramContext);
        this.context = paramContext;
        this.listener = paramListener;
        init(paramString);
    }

    private void init(AttributeSet paramAttributeSet) {
        if (paramAttributeSet != null) {
            TypedArray typedArray = this.context.obtainStyledAttributes(paramAttributeSet, R.styleable.PDFViewPager);
            String str = typedArray.getString(R.styleable.PDFViewPager_pdfUrl);
            if (str != null && str.length() > 0)
                init(str);
            typedArray.recycle();
        }
    }

    private void init(String paramString) { (new DownloadFileUrlConnectionImpl(this.context, new Handler(), this)).download(paramString, (new File(this.context.getCacheDir(), FileUtil.extractFileNameFromURL(paramString))).getAbsolutePath()); }

    public void onFailure(Exception paramException) { this.listener.onFailure(paramException); }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
        try {
            return super.onInterceptTouchEvent(paramMotionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onProgressUpdate(int paramInt1, int paramInt2) { this.listener.onProgressUpdate(paramInt1, paramInt2); }

    public void onSuccess(String paramString1, String paramString2) { this.listener.onSuccess(paramString1, paramString2); }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        try {
            return super.onTouchEvent(paramMotionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public class NullListener implements DownloadFile.Listener {
        public void onFailure(Exception param1Exception) {}

        public void onProgressUpdate(int param1Int1, int param1Int2) {}

        public void onSuccess(String param1String1, String param1String2) {}
    }
}
