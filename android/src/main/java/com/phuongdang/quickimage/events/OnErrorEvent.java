package com.phuongdang.quickimage.events;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class OnErrorEvent extends Event {
  public static final String EVENT_NAME = "onQuickImageError";

  public OnErrorEvent(int viewId){
    super(viewId);
  }

  @Override
  public String getEventName(){
    return EVENT_NAME;
  }

  @Override
  public short getCoalescingKey() {
    return 0;
  }

  @Override
  public void dispatch(RCTEventEmitter rctEventEmitter) {
    rctEventEmitter.receiveEvent(getViewTag(), getEventName(), Arguments.createMap());
  }
}
