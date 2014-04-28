package com.bumptech.glide.loader.bitmap;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import com.bumptech.glide.resize.bitmap_recycle.BitmapPool;
import com.bumptech.glide.resize.load.DecodeFormat;
import com.bumptech.glide.resize.load.Downsampler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.mock;

public class DownsamplerTest extends AndroidTestCase {
    private File tempFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File cacheDir = getContext().getCacheDir();
        cacheDir.mkdir();
        tempFile = new File(cacheDir, "temp");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        tempFile.delete();
    }

    public void testAlwaysArgb8888() throws FileNotFoundException {
        Bitmap rgb565 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        compressBitmap(rgb565, Bitmap.CompressFormat.JPEG);
        Downsampler downsampler = Downsampler.AT_LEAST;
        InputStream is = new FileInputStream(tempFile);
        Bitmap result = downsampler.decode(is, mock(BitmapPool.class), 100, 100, DecodeFormat.ALWAYS_ARGB_8888);
        assertEquals(Bitmap.Config.ARGB_8888, result.getConfig());
    }

    public void testPreferRgb565() throws FileNotFoundException {
        Bitmap rgb565 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        compressBitmap(rgb565, Bitmap.CompressFormat.JPEG);
        Downsampler downsampler = Downsampler.AT_LEAST;
        InputStream is = new FileInputStream(tempFile);
        Bitmap result = downsampler.decode(is, mock(BitmapPool.class), 100, 100, DecodeFormat.PREFER_RGB_565);
        assertEquals(Bitmap.Config.RGB_565, result.getConfig());
    }

    private void compressBitmap(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(tempFile));
            bitmap.compress(compressFormat, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { }
            }
        }

    }
}
