package com.phuongdang.quickimage;

import static com.phuongdang.quickimage.QuickImageRequestListener.REACT_ON_ERROR_EVENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;

import com.bumptech.glide.request.Request;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.phuongdang.quickimage.events.OnErrorEvent;
import com.phuongdang.quickimage.events.OnLoadStartEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class QuickImageViewWithUrl extends AppCompatImageView {
  private boolean mNeedsReload = false;
  private ReadableMap mSource = null;
  private Drawable mDefaultSource = null;
  public GlideUrl glideUrl;

  public QuickImageViewWithUrl(Context context) {
    super(context);
  }

  public void setSource(@Nullable ReadableMap source){
    mNeedsReload = true;
    mSource = source;
  }

  public void setDefaultSource(@Nullable Drawable source){
    mNeedsReload = true;
    mDefaultSource = source;
  }

  private boolean isNullOrEmpty(final String url) {
    return url == null || url.trim().isEmpty();
  }

  public void clearView(@Nullable RequestManager requestManager) {
    if (requestManager != null && getTag() != null && getTag() instanceof Request) {
      requestManager.clear(this);
    }
  }

  @SuppressLint("CheckResult")
  public void onAfterUpdate(
    @Nonnull QuickImageViewManager manager,
    @Nullable RequestManager requestManager,
    @Nonnull Map<String, List<QuickImageViewWithUrl>> viewsForUrlsMap) {
    if (!mNeedsReload)
      return;

    if ((mSource == null ||
      !mSource.hasKey("uri") ||
      isNullOrEmpty(mSource.getString("uri"))) &&
      mDefaultSource == null) {

      // Cancel existing requests.
      clearView(requestManager);

      if (glideUrl != null) {
        QuickImageOkHttpProgressGlideModule.forget(glideUrl.toStringUrl());
      }

      // Clear the image.
      setImageDrawable(null);
      return;
    }

    //final GlideUrl glideUrl = FastImageViewConverter.getGlideUrl(view.getContext(), mSource);
    final QuickImageSource imageSource = QuickImageViewConverter.getImageSource(getContext(), mSource);

    if (imageSource != null && imageSource.getUri().toString().length() == 0) {
      ThemedReactContext context = (ThemedReactContext) getContext();
      int viewId = getId();
      EventDispatcher eventDispatcher =
        UIManagerHelper.getEventDispatcherForReactTag(context, viewId);
      if (eventDispatcher == null) {
        return;
      }
      eventDispatcher.dispatchEvent(new OnErrorEvent(viewId));

      // Cancel existing requests.
      clearView(requestManager);

      if (glideUrl != null) {
        QuickImageOkHttpProgressGlideModule.forget(glideUrl.toStringUrl());
      }
      // Clear the image.
      setImageDrawable(null);
      return;
    }

    // `imageSource` may be null and we still continue, if `defaultSource` is not null
    final GlideUrl glideUrl = imageSource == null ? null : imageSource.getGlideUrl();

    // Cancel existing request.
    this.glideUrl = glideUrl;
    clearView(requestManager);

    String key = glideUrl == null ? null : glideUrl.toStringUrl();

    if (glideUrl != null) {
      QuickImageOkHttpProgressGlideModule.expect(key, manager);
      List<QuickImageViewWithUrl> viewsForKey = viewsForUrlsMap.get(key);
      if (viewsForKey != null && !viewsForKey.contains(this)) {
        viewsForKey.add(this);
      } else if (viewsForKey == null) {
        List<QuickImageViewWithUrl> newViewsForKeys = new ArrayList<>(Collections.singletonList(this));
        viewsForUrlsMap.put(key, newViewsForKeys);
      }
    }

    ThemedReactContext context = (ThemedReactContext) getContext();
    if (imageSource != null) {
      // This is an orphan even without a load/loadend when only loading a placeholder
      int viewId = getId();
      EventDispatcher eventDispatcher =
        UIManagerHelper.getEventDispatcherForReactTag(context, viewId);
      if (eventDispatcher == null) {
        return;
      }
      eventDispatcher.dispatchEvent(new OnLoadStartEvent(viewId, 0, 0));
    }

    if (requestManager != null) {
      RequestBuilder<Drawable> builder =
        requestManager
          // This will make this work for remote and local images. e.g.
          //    - file:///
          //    - content://
          //    - res:/
          //    - android.resource://
          //    - data:image/png;base64
          .load(imageSource == null ? null : imageSource.getSourceForLoad())
          .apply(QuickImageViewConverter
            .getOptions(context, imageSource, mSource)
            .placeholder(mDefaultSource) // show until loaded
            .fallback(mDefaultSource)); // null will not be treated as error

      if (key != null)
        builder.listener(new QuickImageRequestListener(key));

      builder.into(this);
    }
  }
}
