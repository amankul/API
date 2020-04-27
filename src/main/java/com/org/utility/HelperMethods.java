package com.org.utility;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;




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

  public static JSONObject generateRequestBody(String fileName) throws IOException {
    String requestBody = FileUtils.getJsonTextFromFile(fileName);
    JSONObject requestBodyData = new JSONObject(requestBody);

    return requestBodyData;
  }

}
