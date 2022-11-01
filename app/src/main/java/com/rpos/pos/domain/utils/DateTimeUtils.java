package com.rpos.pos.domain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.rpos.pos.domain.utils.Utility.logger;

public class DateTimeUtils {


    public static String getCurrentDate(){
        try {

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            return formatter.format(date);

        }catch (Exception e){
            e.printStackTrace();
            return "01-01-1991";
        }
    }

    public static String getCurrentTime(){
        try{

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
            return formatter.format(date);
           // return "17:30:00";

        }catch (Exception e){
            e.printStackTrace();
            return "00:00:00";
        }
    }

    public static String getCurrentDateTime(){
        try {

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
            return formatter.format(date);

        }catch (Exception e){
            e.printStackTrace();
            return "00-00-0000 00:00:00";
        }
    }

    public static String getCurrentDateTimeForInvoice(){
        try {

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ", Locale.US);
            return formatter.format(date);

        }catch (Exception e){
            e.printStackTrace();
            return "00-00-0000 00:00:00";
        }
    }

    public static String getCurrentDateTimeInvoice(){
        try {

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return formatter.format(date);

        }catch (Exception e){
            e.printStackTrace();
            return "00-00-0000 00:00:00";
        }
    }




    /**
     * To find number of days between two dates
     * */
    public static int getDateDiff(String date1, String date2){
        try {

            logger("date diff b/w : "+date1+" -To- "+date2);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            return findDaysDiff(d1.getTime(),d2.getTime());

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    private static Date getDateFromString(String str_date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        try {
            return sdf.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /*public static String getDateDiffInFullPassedTime(String strDate1, String strDate2) {

        logger("start date:"+strDate1+ "  end date:"+strDate2);

        Date startDate = getDateFromString(strDate1);
        Date endDate = getDateFromString(strDate2);

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        //TODO Here you will get the days
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        //TODO Here you will get the hours
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        //TODO Here you will get the minute
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        //TODO Here you will get the second
        long elapsedSeconds = different / secondsInMilli;

        if(elapsedDays>0){
            long days = elapsedDays;
            long years = days/365;
            logger("year>"+years);
            if(years>0){
                return ""+years+" "+ Constants.YEAR_AGO;
            }

            days = elapsedDays;
            long months = days/30;
            logger("month>"+months);
            if ((months>0)){
                return ""+months+" "+Constants.MONTHS_AGO;
            }

            days = elapsedDays;
            long weeks = days/7;
            logger("weeks>"+weeks);
            if(weeks>0){
                return ""+weeks+" "+Constants.MONTHS_AGO;
            }

            return ""+elapsedDays+" "+ Constants.DAYS_AGO;

        }else if(elapsedHours>0){
            return ""+elapsedHours+" "+ Constants.HOURS_AGO;
        }else if(elapsedMinutes>0){
            return ""+elapsedMinutes+" "+ Constants.MINUTES_AGO;
        }else if(elapsedSeconds>0){
            return ""+elapsedSeconds+" "+ Constants.SECONDS_AGO;
        }else {
            return "now";
        }
    }*/

    private static int findDaysDiff(long unixStartTime,long unixEndTime)
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(unixStartTime);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(unixEndTime);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        return (int) ((calendar2.getTimeInMillis()-calendar1.getTimeInMillis())/(24 * 60 * 60 * 1000));

    }

    public static boolean checkDatePassed(String date){
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date futureDate = sdf.parse(date);

            Date today = sdf.parse(DateTimeUtils.getCurrentDate());

            if (today.getTime() > futureDate.getTime()) {
                return true;
            }

            return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
