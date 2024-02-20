package com.phuongdang.quickimage;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.react.views.imagehelper.ImageSource;

import javax.annotation.Nullable;

import com.bumptech.glide.load.model.Headers;

public class QuickImageSource extends ImageSource {
  private static final String DATA_SCHEME = "data";
  private static final String LOCAL_RESOURCE_SCHEME = "res";
  private static final String ANDROID_RESOURCE_SCHEME = "android.resource";
  private static final String ANDROID_CONTENT_SCHEME = "content";
  private static final String LOCAL_FILE_SCHEME = "file";
  private final Headers mHeaders;
  private Uri mUri;

  public static boolean isBase64Uri(Uri uri) {
    return DATA_SCHEME.equals(uri.getScheme());
  }

  public static boolean isLocalResourceUri(Uri uri) {
    return LOCAL_RESOURCE_SCHEME.equals(uri.getScheme());
  }

  public static boolean isResourceUri(Uri uri) {
    return ANDROID_RESOURCE_SCHEME.equals(uri.getScheme());
  }

  public static boolean isContentUri(Uri uri) {
    return ANDROID_CONTENT_SCHEME.equals(uri.getScheme());
  }

  public static boolean isLocalFileUri(Uri uri) {
    return LOCAL_FILE_SCHEME.equals(uri.getScheme());
  }

  public boolean isBase64Resource() {
    return mUri != null && QuickImageSource.isBase64Uri(mUri);
  }

  public boolean isResource() {
    return mUri != null && QuickImageSource.isResourceUri(mUri);
  }

  public boolean isLocalFile() {
    return mUri != null && QuickImageSource.isLocalFileUri(mUri);
  }

  public boolean isContentUri() {
    return mUri != null && QuickImageSource.isContentUri(mUri);
  }

  public Object getSourceForLoad() {
    if (isContentUri()) {
      return getSource();
    }
    if (isBase64Resource()) {
      return getSource();
    }
    if (isResource()) {
      return getUri();
    }
    if (isLocalFile()) {
      return getUri().toString();
    }
    return getGlideUrl();
  }

  @Override
  public Uri getUri() {
    return mUri;
  }

  public Headers getHeaders() {
    return mHeaders;
  }

  public GlideUrl getGlideUrl() {
    return new GlideUrl(getUri().toString(), getHeaders());
  }

  public QuickImageSource(Context context, String source){
    this(context, source, null);
  }

  public QuickImageSource(Context context, String source, @Nullable Headers headers){
    this(context, source, 0.0d, 0.0d, headers);
  }

  public QuickImageSource(Context context, String source, double width, double height, @Nullable Headers headers){
    super(context, source, width, height);
    mHeaders = headers == null ? Headers.DEFAULT : headers;
    mUri = super.getUri();

    if(isResource() && TextUtils.isEmpty(mUri.toString())){
      throw new Resources.NotFoundException("'Local resources not found. Resource: '" + getSource() + "'.");
    }

    if(isLocalResourceUri(mUri)){
      mUri = Uri.parse(mUri.toString().replace("res:/", ANDROID_RESOURCE_SCHEME + "://" + context.getPackageName() + "/"));
    }
  }
}
