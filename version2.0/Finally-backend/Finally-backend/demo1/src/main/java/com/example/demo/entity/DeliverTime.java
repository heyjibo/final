package com.example.demo.entity;

import java.time.Duration;

public class DeliverTime {
    private Duration time;
    private int state;


    public Duration getTime() {
        return time;
    }
    public void setTime(Duration time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
}
