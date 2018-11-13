package com.example.admin.healthtrack.models;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskItem {
    public Date startDateUtc;
    public Date endDateUtc;
    public String title;
    public int intervalInDays;
    public boolean isRecurring;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public TaskItem(){

    }

    public Date getStartDateUtc() {
        return startDateUtc;
    }
    public boolean isGoingToday(Calendar today){
        Log.d("mLog", "today: "+today.toString());
        Calendar c = Calendar.getInstance();
        c.setTime(startDateUtc);
        Log.d("mLog", "taskStart: "+c.toString());
        long difference = daysBetween(today, c);
        Log.d("mLog", "difference: "+ difference);
        if(difference%getIntervalInDays() == 0){
            Log.d("mLog", "isToday: ");
            return true;
        }
        return false;
    }
    public String getStringTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(startDateUtc);
    }
    public static long daysBetween(Calendar startDate, Calendar endDate) {
        //Make sure we don't change the parameter passed
        Calendar newStart = Calendar.getInstance();
        newStart.setTimeInMillis(startDate.getTimeInMillis());
        newStart.set(Calendar.HOUR_OF_DAY, 0);
        newStart.set(Calendar.MINUTE, 0);
        newStart.set(Calendar.SECOND, 0);
        newStart.set(Calendar.MILLISECOND, 0);

        Calendar newEnd = Calendar.getInstance();
        newEnd.setTimeInMillis(endDate.getTimeInMillis());
        newEnd.set(Calendar.HOUR_OF_DAY, 0);
        newEnd.set(Calendar.MINUTE, 0);
        newEnd.set(Calendar.SECOND, 0);
        newEnd.set(Calendar.MILLISECOND, 0);

        long end = newEnd.getTimeInMillis();
        long start = newStart.getTimeInMillis();
        Log.d("mLog", "daysBetween: end - start "+end+"-"+start);
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }
    public Date getEndDateUtc() {
        return endDateUtc;
    }

    public void setEndDateUtc(Date endDateUtc) {
        this.endDateUtc = endDateUtc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIntervalInDays() {
        return intervalInDays;
    }

    public void setIntervalInDays(int intervalInDays) {
        this.intervalInDays = intervalInDays;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    @Override
    public String toString() {
        return "TaskItem{" +
                "startDateUtc=" + startDateUtc +
                ", endDateUtc=" + endDateUtc +
                ", title='" + title + '\'' +
                ", intervalInDays=" + intervalInDays +
                ", isRecurring=" + isRecurring +
                '}';
    }
}
