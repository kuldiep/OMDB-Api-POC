package com.android_poc.moviesfornoon.ui;

public class InternetEvent {
    private String eventMessage;
    private int eventType;
    public static final int INTERNET_EVENT_ID = 2;

    public enum INTERNET_EVENT_TYPE {
        AVAILABLE, NOT_AVAILABLE, DETECTED
    }

    public InternetEvent(String eventMessage, int eventType) {
        setEventId(INTERNET_EVENT_ID);
        this.eventMessage = eventMessage;
        this.eventType = eventType;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    private int eventId;

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
