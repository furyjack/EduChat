package com.example.admin.educhat;

import android.app.Application;

import java.util.Timer;
import java.util.TimerTask;


public class baseclass extends Application {
    boolean wasinbackground;
    Timer transitiontimer;
    int Thresholdtime=2000;
    TimerTask mActivityTransitionTimerTask;
    public void startActivityTransitionTimer() {
        this.transitiontimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                baseclass.this.wasinbackground = true;
            }
        };

        this.transitiontimer.schedule(mActivityTransitionTimerTask,
                Thresholdtime);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.transitiontimer != null) {
            this.transitiontimer.cancel();
        }

        this.wasinbackground = false;
    }




}
