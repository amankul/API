package com.phunware.utility;

import org.apache.commons.codec.digest.DigestUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import java.util.Calendar;



/**
 * Created by VinayKarumuri on 5/1/17.
 */
public class HelperMethods {

  public static String getDateAsString() {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
  }

  public static String getUUIDAsString() {
    return UUID.randomUUID().toString();
  }

  public static int generateRandomNumber(int max) {
    return (int) (Math.random() * max);
  }

  public static String getDateTime() {
    return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
  }

  public static String addDaystoCurrentDate(int noOfDays){

    DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, noOfDays);
    return dateFormat.format(c.getTime());
  }

  public static String generateSHA256Hash(String stringToConvert){
    return DigestUtils.sha256Hex(stringToConvert);
  }

}
