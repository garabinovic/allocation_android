package com.andrijag.allocation.models;

public class MyEvent {
  private String mId;
  private String mTitle;
  private String mDescription;
  private String mLocation;
  private String mStart;
  private String mEnd;
  private String mClientName;
  private Boolean mCanStart;
  private Boolean mCanStop;

  public Boolean getCanStart() {
    return mCanStart;
  }

  public void setCanStart(Boolean mCanStart) {
    this.mCanStart = mCanStart;
  }

  public Boolean getCanStop() {
    return mCanStop;
  }

  public void setCanStop(Boolean mCanStop) {
    this.mCanStop = mCanStop;
  }

  public String getStart() {
    return mStart;
  }

  public void setStart(String mStart) {
    this.mStart = mStart;
  }

  public String getEnd() {
    return mEnd;
  }

  public void setEnd(String mEnd) {
    this.mEnd = mEnd;
  }

  public String getClientName() {
    return mClientName;
  }

  public void setClientName(String mClientName) {
    this.mClientName = mClientName;
  }

  public String getId() {
    return mId;
  }

  public void setId(String mId) {
    this.mId = mId;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String mTitle) {
    this.mTitle = mTitle;
  }

  public String getDescription() {
    return mDescription;
  }

  public void setDescription(String mDescription) {
    this.mDescription = mDescription;
  }

  public String getLocation() {
    return mLocation;
  }

  public void setLocation(String mLocation) {
    this.mLocation = mLocation;
  }

  public MyEvent() {

  }
}
