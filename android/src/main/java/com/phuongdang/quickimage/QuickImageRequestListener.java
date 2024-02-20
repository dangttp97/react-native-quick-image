package com.phuongdang.quickimage;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.phuongdang.quickimage.events.OnErrorEvent;
import com.phuongdang.quickimage.events.OnLoadEndEvent;
import com.phuongdang.quickimage.events.OnLoadEvent;

public class QuickImageRequestListener implements RequestListener<Drawable> {
  static final String REACT_ON_ERROR_EVENT = "onQuickImageError";
  static final String REACT_ON_LOAD_EVENT = "onQuickImageLoad";
  static final String REACT_ON_LOAD_END_EVENT = "onQuickImageLoadEnd";

  private final String key;

  QuickImageRequestListener(String key){
    this.key = key;
  }

  @Override
  public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
    QuickImageOkHttpProgressGlideModule.forget(key);

    if(!(target instanceof ImageViewTarget<Drawable>))
    return false;

    QuickImageViewWithUrl view = (QuickImageViewWithUrl) ((ImageViewTarget) target).getView();
    ThemedReactContext context = (ThemedReactContext) view.getContext();

    int viewId = view.getId();
    EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcher(context, viewId);
    if(eventDispatcher == null){
      return false;
    }
    eventDispatcher.dispatchEvent(new OnErrorEvent(viewId));
    eventDispatcher.dispatchEvent(new OnLoadEndEvent(viewId));

    return false;
  }

  @Override
  public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
    if(!(target instanceof ImageViewTarget<Drawable>))
      return false;

    QuickImageViewWithUrl view = (QuickImageViewWithUrl) ((ImageViewTarget) target).getView();
    ThemedReactContext context = (ThemedReactContext) view.getContext();

    int viewId = view.getId();
    EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, viewId);

    if(eventDispatcher == null)
      return false;

    eventDispatcher.dispatchEvent(new OnLoadEvent(viewId, resource.getIntrinsicWidth(), resource.getIntrinsicHeight()));
    eventDispatcher.dispatchEvent(new OnLoadEndEvent(viewId));

    return false;
  }
}
