package com.addevelopment.whatsappclonefull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateChecker {

    public String dateChecker(String messageDate) throws ParseException {

        SimpleDateFormat fullformate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        SimpleDateFormat dateformate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeformate = new SimpleDateFormat("hh:mm a");

        //message date
        Date messagetime = fullformate.parse(messageDate);

        //Todays date
        Date today = new Date();

        //yesterday date
        Date yesterday = subtractDays(today,1);

        //before Yesturday
        Date beforeYesturday = subtractDays(today,2);


        if(messagetime.after(yesterday)){
            String message = timeformate.format(messagetime);
            return message;
        }
        else if(messagetime.equals(yesterday) || messagetime.after(beforeYesturday)){
            //String message = timeformate.format(messagetime);
            return "Yesterday";
        }
        else{
            String message = dateformate.format(messagetime);
            return message;
        }
    }



    public static Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

}
