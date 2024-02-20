package com.phuongdang.quickimage;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;
import com.facebook.react.modules.network.OkHttpClientProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

@GlideModule
public class QuickImageOkHttpProgressGlideModule extends LibraryGlideModule {
  private static final DispatchingProgressListener progressListener = new DispatchingProgressListener();

  private static Interceptor createInterceptor(final ResponseProgressListener listener) {
    return new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        final String key = request.url().toString();
        return response
          .newBuilder()
          .body(new OkHttpProgressResponseBody(key, response.body(), listener))
          .build();
      }
    };
  }

  static void forget(String key) {
    progressListener.forget(key);
  }

  static void expect(String key, QuickImageProgressListener listener) {
    progressListener.expect(key, listener);
  }

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry){
    OkHttpClient client = OkHttpClientProvider.getOkHttpClient().newBuilder().addInterceptor(createInterceptor(progressListener)).build();
  }

}


