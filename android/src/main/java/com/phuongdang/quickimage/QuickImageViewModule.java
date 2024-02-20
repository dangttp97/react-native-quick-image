package com.phuongdang.quickimage;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class QuickImageViewModule extends ReactContextBaseJavaModule {
  private static final  String REACT_CLASS = "QuickImageView";

  QuickImageViewModule(ReactApplicationContext context){
    super(context);
  }

  @NonNull
  @Override
  public String getName(){
    return REACT_CLASS;
  }

  @ReactMethod
  public void preload(final ReadableArray sources){
    final Activity activity = getCurrentActivity();

    if(activity == null)
      return;

    activity.runOnUiThread(() -> {
        for (int i = 0; i < sources.size(); i++){
          final ReadableMap source = sources.getMap(i);
          final QuickImageSource imageSource = QuickImageViewConverter.getImageSource(activity, source);
          Glide.with(activity.getApplicationContext())
            .load(imageSource.isBase64Resource() ? imageSource.getSource():imageSource.isResource() ? imageSource.getUri() : imageSource.getGlideUrl())
            .apply(QuickImageViewConverter.getOptions(activity, imageSource, source))
            .preload();
      }
    });
  }

  @ReactMethod
  public void clearMemoryCache(final Promise promise){
    final  Activity activity = getCurrentActivity();
    if(activity == null){
      promise.resolve(null);
      return;
    }

    activity.runOnUiThread(() -> {
        Glide.get(activity.getApplicationContext()).clearMemory();
        promise.resolve(null);
    });
  }

  @ReactMethod
  public void clearDiskCache(Promise promise){
    final Activity activity = getCurrentActivity();

    if(activity == null){
      promise.resolve(null);
      return;
    }

    Glide.get(activity.getApplicationContext()).clearDiskCache();
    promise.resolve(null);
  }
}
