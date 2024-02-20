package com.phuongdang.quickimage;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PorterDuff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.phuongdang.quickimage.events.OnErrorEvent;
import com.phuongdang.quickimage.events.OnLoadEndEvent;
import com.phuongdang.quickimage.events.OnLoadEvent;
import com.phuongdang.quickimage.events.OnLoadStartEvent;
import com.phuongdang.quickimage.events.OnProgressEvent;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import paper.java.com.facebook.react.viewmanagers.QuickImageViewManagerDelegate;
import paper.java.com.facebook.react.viewmanagers.QuickImageViewManagerInterface;

public class QuickImageViewManager extends SimpleViewManager<QuickImageViewWithUrl> implements QuickImageProgressListener, QuickImageViewManagerInterface<QuickImageViewWithUrl> {
  public static final String REACT_CLASS = "QuickImageView";
  static final String REACT_ON_LOAD_START_EVENT = "onQuickImageLoadStart";
  static final String REACT_ON_PROGRESS_EVENT = "onQuickImageProgress";
  static final String REACT_ON_LOAD_EVENT = "onQuickImageLoad";
  static final String REACT_ON_ERROR_EVENT = "onQuickImageError";
  static final String REACT_ON_LOAD_END_EVENT = "onQuickImageLoadEnd";
  private static final Map<String, List<QuickImageViewWithUrl>> VIEWS_FOR_URLS = new WeakHashMap<>();

  @Nullable
  private RequestManager requestManager = null;
  private final ViewManagerDelegate<QuickImageViewWithUrl> mDelegate;

  @Nullable
  @Override
  protected ViewManagerDelegate<QuickImageViewWithUrl> getDelegate() {
    return mDelegate;
  }

  public QuickImageViewManager(){
    mDelegate = new QuickImageViewManagerDelegate<>(this);
  }

  private static Activity getActivityFromContext(final Context context) {
    if (context instanceof Activity) {
      return (Activity) context;
    }

    if (context instanceof ThemedReactContext) {
      final Context baseContext = ((ThemedReactContext) context).getBaseContext();
      if (baseContext instanceof Activity) {
        return (Activity) baseContext;
      }

      if (baseContext instanceof ContextWrapper) {
        ContextWrapper contextWrapper = (ContextWrapper) baseContext;
        final Context wrapperBaseContext = contextWrapper.getBaseContext();
        if (wrapperBaseContext instanceof Activity) {
          return (Activity) wrapperBaseContext;
        }
      }
    }

    return null;
  }

  private static boolean isActivityDestroyed(Activity activity) {
    return activity.isDestroyed() || activity.isFinishing();
  }

  private static boolean isValidContextForGlide(final Context context) {
    Activity activity = getActivityFromContext(context);

    if (activity == null) {
      return false;
    }

    return !isActivityDestroyed(activity);
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  @Override
  protected QuickImageViewWithUrl createViewInstance(@NonNull ThemedReactContext reactContext) {
    if (isValidContextForGlide(reactContext)) {
      requestManager = Glide.with(reactContext);
    }

    return new QuickImageViewWithUrl(reactContext);
  }

  @Override
  @ReactProp(name = "source")
  public void setSource(QuickImageViewWithUrl view, @Nullable ReadableMap source) {
    view.setSource(source);
  }

  @Override
  @ReactProp(name = "defaultSource")
  public void setDefaultSource(QuickImageViewWithUrl view, @Nullable String source) {
    view.setDefaultSource(
      ResourceDrawableIdHelper.getInstance()
        .getResourceDrawable(view.getContext(), source));
  }

  @Override
  @ReactProp(name = "tintColor", customType = "Color")
  public void setTintColor(QuickImageViewWithUrl view, @Nullable Integer color) {
    if (color == null) {
      view.clearColorFilter();
    } else {
      view.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
  }

  @Override
  @ReactProp(name = "resizeMode")
  public void setResizeMode(QuickImageViewWithUrl view, String resizeMode) {
    final QuickImageViewWithUrl.ScaleType scaleType = QuickImageViewConverter.getScaleType(resizeMode);
    view.setScaleType(scaleType);
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    String registrationName = "registrationName";

    return MapBuilder.<String, Object>builder()
      .put(OnLoadStartEvent.EVENT_NAME, MapBuilder.of(registrationName, REACT_ON_LOAD_START_EVENT))
      .put(OnProgressEvent.EVENT_NAME, MapBuilder.of(registrationName, REACT_ON_PROGRESS_EVENT))
      .put(OnLoadEvent.EVENT_NAME, MapBuilder.of(registrationName, REACT_ON_LOAD_EVENT))
      .put(OnErrorEvent.EVENT_NAME, MapBuilder.of(registrationName, REACT_ON_ERROR_EVENT))
      .put(OnLoadEndEvent.EVENT_NAME, MapBuilder.of(registrationName, REACT_ON_LOAD_END_EVENT))
      .build();
  }

  @Override
  public void onProgress(String key, long bytesRead, long expectedLength) {
    List<QuickImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
    if (viewsForKey != null) {
      for (QuickImageViewWithUrl view : viewsForKey) {
        ThemedReactContext context = (ThemedReactContext) view.getContext();
        int viewId = view.getId();
        EventDispatcher eventDispatcher =
          UIManagerHelper.getEventDispatcherForReactTag(context, viewId);
        if (eventDispatcher == null) {
          return;
        }
        eventDispatcher.dispatchEvent(new OnProgressEvent(viewId, bytesRead, expectedLength));
      }
    }
  }

  @Override
  public float getGranularityPercentage() {
    return 0.5f;
  }

  @Override
  protected void onAfterUpdateTransaction(@NonNull QuickImageViewWithUrl view) {
    super.onAfterUpdateTransaction(view);
    view.onAfterUpdate(this, requestManager, VIEWS_FOR_URLS);
  }
}
