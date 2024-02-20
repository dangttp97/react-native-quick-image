package paper.java.com.facebook.react.viewmanagers;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;

public class QuickImageViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & QuickImageViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
  public QuickImageViewManagerDelegate(U viewManager) {
    super(viewManager);
  }

  @Override
  public void setProperty(T view, String propName, @Nullable Object value) {
    switch (propName) {
      case "source":
        mViewManager.setSource(view, (ReadableMap) value);
        break;
      case "defaultSource":
        mViewManager.setDefaultSource(view, value == null ? null : (String) value);
        break;
      case "resizeMode":
        mViewManager.setResizeMode(view, (String) value);
        break;
      case "tintColor":
        mViewManager.setTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
        break;
      default:
        super.setProperty(view, propName, value);
        break;
    }
  }
}
