package com.slj.meet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import es.voghdev.pdfviewpager.library.adapter.BitmapContainer;
import es.voghdev.pdfviewpager.library.adapter.PdfRendererParams;
import es.voghdev.pdfviewpager.library.adapter.SimpleBitmapPool;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MyBasePDFPagerAdapter extends PagerAdapter {
    protected static final int DEFAULT_OFFSCREENSIZE = 1;

    protected static final float DEFAULT_QUALITY = 3.0F;

    protected static final int FIRST_PAGE = 0;

    BitmapContainer bitmapContainer;

    Context context;

    LayoutInflater inflater;

    protected int offScreenSize;

    String pdfPath;

    protected float renderQuality;

    PdfRenderer renderer;

    @SuppressLint("NewApi")
    public MyBasePDFPagerAdapter(Context paramContext, String paramString) {
        this.pdfPath = paramString;
        this.context = paramContext;
        this.renderQuality = 3.0F;
        this.offScreenSize = 1;
        init();
    }

    @SuppressLint("NewApi")
    public MyBasePDFPagerAdapter(Context paramContext, String paramString, int paramInt) {
        this.pdfPath = paramString;
        this.context = paramContext;
        this.renderQuality = 3.0F;
        this.offScreenSize = paramInt;
        init();
    }


    @SuppressLint("NewApi")
    private PdfRendererParams extractPdfParamsFromFirstPage(PdfRenderer paramPdfRenderer, float paramFloat) {
        PdfRenderer.Page page = getPDFPage(paramPdfRenderer, 0);
        PdfRendererParams pdfRendererParams = new PdfRendererParams();
        pdfRendererParams.setRenderQuality(paramFloat);
        pdfRendererParams.setOffScreenSize(this.offScreenSize);
        pdfRendererParams.setWidth((int)(page.getWidth() * paramFloat));
        pdfRendererParams.setHeight((int)(page.getHeight() * paramFloat));
        page.close();
        return pdfRendererParams;
    }

    private boolean isAnAsset(String paramString) { return !paramString.startsWith("/"); }

    public void close() {
        releaseAllBitmaps();
        if (this.renderer != null)
            this.renderer.close();
    }

    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getCount() { return (this.renderer != null) ? this.renderer.getPageCount() : 0; }

    @SuppressLint("NewApi")
    protected PdfRenderer.Page getPDFPage(PdfRenderer paramPdfRenderer, int paramInt) { return paramPdfRenderer.openPage(paramInt); }

    protected ParcelFileDescriptor getSeekableFileDescriptor(String paramString) throws IOException {
        File file = new File(paramString);
        if (file.exists())
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        if (isAnAsset(paramString))
            return ParcelFileDescriptor.open(new File(this.context.getCacheDir(), paramString), ParcelFileDescriptor.MODE_READ_ONLY);
        URI uRI = URI.create(String.format("file://%s", paramString));
        return this.context.getContentResolver().openFileDescriptor(Uri.parse(uRI.toString()), "rw");
    }


    @SuppressLint("NewApi")
    protected void init() {
        try {
            this.renderer = new PdfRenderer(getSeekableFileDescriptor(this.pdfPath));
            this.inflater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            this.bitmapContainer = new SimpleBitmapPool(extractPdfParamsFromFirstPage(this.renderer, this.renderQuality));
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt) {
        View view = this.inflater.inflate(es.voghdev.pdfviewpager.library.R.layout.view_pdf_page, paramViewGroup, false);
        ImageView imageView = (ImageView)view.findViewById(es.voghdev.pdfviewpager.library.R.id.imageView);
        if (this.renderer == null || getCount() < paramInt)
            return view;
        PdfRenderer.Page page = getPDFPage(this.renderer, paramInt);
        Bitmap bitmap = this.bitmapContainer.get(paramInt);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();
        imageView.setImageBitmap(bitmap);
        ((ViewPager)paramViewGroup).addView(view, 0);
        return view;
    }

    public boolean isViewFromObject(View paramView, Object paramObject) { return (paramView == (View)paramObject); }

    protected void releaseAllBitmaps() {
        if (this.bitmapContainer != null)
            this.bitmapContainer.clear();
    }
}
