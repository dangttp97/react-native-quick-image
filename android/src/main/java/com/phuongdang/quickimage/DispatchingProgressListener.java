package com.phuongdang.quickimage;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class DispatchingProgressListener implements ResponseProgressListener {
  private final Map<String, QuickImageProgressListener> LISTENERS = new WeakHashMap<>();
  private final Map<String, Long>PROGRESSES = new HashMap<>();

  void forget(String key){
    LISTENERS.remove(key);
    PROGRESSES.remove(key);
  }

  void expect(String key, QuickImageProgressListener listener){
    LISTENERS.put(key, listener);
  }

  private boolean needsDispatch(String key, long current, long total, float granularity){
    if(granularity == 0 || current == 0 || total == current){
      return true;
    }

    float percent = 100f * current / total;
    long currentProgress = (long) (percent/granularity);
    Long lastProgress = PROGRESSES.get(key);

    if(lastProgress == null || currentProgress != lastProgress){
      PROGRESSES.put(key, currentProgress);
      return true;
    }
    else{
      return false;
    }
  }

  @Override
  public void update(String key, long bytesRead, long contentLength) {
    final QuickImageProgressListener listener = LISTENERS.get(key);

    if(listener == null){
      return;
    }

    if(contentLength <= bytesRead){
      forget(key);
    }
    if(needsDispatch(key, bytesRead, contentLength, listener.getGranularityPercentage())){
      listener.onProgress(key, bytesRead,contentLength);
    }
  }
}
