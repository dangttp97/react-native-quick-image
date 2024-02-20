package com.phuongdang.quickimage;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class OkHttpProgressResponseBody extends ResponseBody {
  private final String key;
  private final ResponseBody responseBody;
  private final ResponseProgressListener progressListener;
  private BufferedSource bufferedSource;

  OkHttpProgressResponseBody(
    String key,
    ResponseBody responseBody,
    ResponseProgressListener progressListener
  ) {
    this.key = key;
    this.responseBody = responseBody;
    this.progressListener = progressListener;
  }

  @Override
  public MediaType contentType() {
    return responseBody.contentType();
  }

  @Override
  public long contentLength() {
    return responseBody.contentLength();
  }

  @NonNull
  @Override
  public BufferedSource source() {
    if (bufferedSource == null) {
      bufferedSource = Okio.buffer(source(responseBody.source()));
    }
    return bufferedSource;
  }

  private Source source(Source source) {
    return new ForwardingSource(source) {
      long totalBytesRead = 0L;

      @Override
      public long read(@NonNull Buffer sink, long byteCount) throws IOException {
        long bytesRead = super.read(sink, byteCount);
        long fullLength = responseBody.contentLength();
        if (bytesRead == -1) {
          // this source is exhausted
          totalBytesRead = fullLength;
        } else {
          totalBytesRead += bytesRead;
        }
        progressListener.update(key, totalBytesRead, fullLength);
        return bytesRead;
      }
    };
  }
}
