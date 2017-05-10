package com.phunware.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VinayKarumuri on 5/1/17.
 */
public class HelperMethods {

  public static String getDateAsString() {

    return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
  }

}
