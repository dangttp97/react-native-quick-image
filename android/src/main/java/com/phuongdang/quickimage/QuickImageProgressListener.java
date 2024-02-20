package com.phuongdang.quickimage;

public interface QuickImageProgressListener {
  void onProgress(String key, long bytesRead, long expectedLength);
  float getGranularityPercentage();
}
