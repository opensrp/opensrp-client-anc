package org.smartregister.anc.library.util;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

//import androidx.collection.LruCache;

/**
 * Created by samuelgithengi on 1/19/18.
 */

public class ImageLoaderRequestUtils {
    private static ImageLoaderRequestUtils imageLoaderRequestUtils;
    private final Context context;
    private final ImageLoader imageLoader;
    private RequestQueue requestQueue;


    private ImageLoaderRequestUtils(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public static synchronized ImageLoaderRequestUtils getInstance(Context context) {
        if (imageLoaderRequestUtils == null) {
            imageLoaderRequestUtils = new ImageLoaderRequestUtils(context);
        }
        return imageLoaderRequestUtils;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}