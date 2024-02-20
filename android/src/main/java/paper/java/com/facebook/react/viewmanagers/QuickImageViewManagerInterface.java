package paper.java.com.facebook.react.viewmanagers;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;

public interface QuickImageViewManagerInterface<T extends View> {
  void setSource(T view, @Nullable ReadableMap value);
  void setDefaultSource(T view, @Nullable String value);
  void setResizeMode(T view, @Nullable String value);
  void setTintColor(T view, @Nullable Integer value);
}
