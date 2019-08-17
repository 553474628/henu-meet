package com.slj.meet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.pdf.PdfRenderer;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import es.voghdev.pdfviewpager.library.adapter.PdfScale;
import es.voghdev.pdfviewpager.library.util.EmptyClickListener;
import java.lang.ref.WeakReference;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MyPDFPagerAdapter extends MyBasePDFPagerAdapter implements PhotoViewAttacher.OnMatrixChangedListener {
    private static final float DEFAULT_SCALE = 1.0F;

    SparseArray<WeakReference<PhotoViewAttacher>> attachers = new SparseArray();

    View.OnClickListener pageClickListener = new EmptyClickListener();

    PdfScale scale = new PdfScale();

    public MyPDFPagerAdapter(Context paramContext, String paramString) { super(paramContext, paramString); }

    public void close() {
        super.close();
        if (this.attachers != null) {
            this.attachers.clear();
            this.attachers = null;
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
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.setScale(3.0F, this.scale.getCenterX(), this.scale.getCenterY(), true);
        photoViewAttacher.setOnMatrixChangeListener(this);
        this.attachers.put(paramInt, new WeakReference(photoViewAttacher));
        imageView.setImageBitmap(bitmap);
        photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            public void onPhotoTap(View param1View, float param1Float1, float param1Float2) { MyPDFPagerAdapter.this.pageClickListener.onClick(param1View); }
        });
        photoViewAttacher.update();
        ((ViewPager)paramViewGroup).addView(view, 0);
        return view;
    }

    public void onMatrixChanged(RectF paramRectF) {
        if (this.scale.getScale() != 1.0F) {
            this.scale.setCenterX(paramRectF.centerX());
            this.scale.setCenterY(paramRectF.centerY());
        }
    }

    public static class Builder {
        float centerX = 0.0F;

        float centerY = 0.0F;

        Context context;

        int offScreenSize = 1;

        View.OnClickListener pageClickListener = new EmptyClickListener();

        String pdfPath = "";

        float renderQuality = 3.0F;

        float scale = 1.0F;

        public Builder(Context param1Context) { this.context = param1Context; }

        public MyPDFPagerAdapter create() {
            MyPDFPagerAdapter myPDFPagerAdapter = new MyPDFPagerAdapter(this.context, this.pdfPath);
            myPDFPagerAdapter.scale.setScale(this.scale);
            myPDFPagerAdapter.scale.setCenterX(this.centerX);
            myPDFPagerAdapter.scale.setCenterY(this.centerY);
            myPDFPagerAdapter.offScreenSize = this.offScreenSize;
            myPDFPagerAdapter.renderQuality = this.renderQuality;
            myPDFPagerAdapter.pageClickListener = this.pageClickListener;
            return myPDFPagerAdapter;
        }

        public Builder setCenterX(float param1Float) {
            this.centerX = param1Float;
            return this;
        }

        public Builder setCenterY(float param1Float) {
            this.centerY = param1Float;
            return this;
        }

        public Builder setOffScreenSize(int param1Int) {
            this.offScreenSize = param1Int;
            return this;
        }

        public Builder setOnPageClickListener(View.OnClickListener param1OnClickListener) {
            if (param1OnClickListener != null)
                this.pageClickListener = param1OnClickListener;
            return this;
        }

        public Builder setPdfPath(String param1String) {
            this.pdfPath = param1String;
            return this;
        }

        public Builder setRenderQuality(float param1Float) {
            this.renderQuality = param1Float;
            return this;
        }

        public Builder setScale(float param1Float) {
            this.scale = param1Float;
            return this;
        }

        public Builder setScale(PdfScale param1PdfScale) {
            this.scale = param1PdfScale.getScale();
            this.centerX = param1PdfScale.getCenterX();
            this.centerY = param1PdfScale.getCenterY();
            return this;
        }
    }
}
