package com.phuongdang.quickimage;

public interface ResponseProgressListener{
  void update(String key, long bytesRead, long contentLength);
}
