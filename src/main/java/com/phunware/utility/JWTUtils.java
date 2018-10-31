package com.phunware.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 * Created by VinayKarumuri on 9/7/18.
 */

public class JWTUtils {

  private static final String JWT_SECRET_KEY_STAGE_ENV = "g7sXZBzouwh9mUttvHJoG8Wk6DH9XmAa";
  private static final String JWT_SECRET_KEY_PROD_ENV = "js44TTpeB2EBwbGnaNhJ92QyvRDacyNQ";
  private static final String[] EMPTY_ARRAY = new String[0];
  public static String jwtToken = "";
  public static String expiredJWTToken = "";
  private static Logger log = Logger.getLogger(JWTUtils.class);
  private static final String SESSION_ID = "fa4fbd8fdf7b58782bf66b4b0c850db53db55c4b";
  private static String emailId = "qaautomation@gmail.com";
  private static String firstName = "QA";
  private static String lastName = "Automation";
  private static final int STAGE_ADMIN_USER_ID = 2633;
  //Note this is not a admin user for prod environment. it is normal user which has access to org id 96 (QA test)
  private static final int PROD_USER_ID = 959;
  private static String orgName = "test";



  /*
   * SESSION ID is a constant. For the purpose of automation, we are using a new user.
   * Claims order should not be changed
   * JWT token generated will only work for the org specified.
   * Valid is current date
   * Expiration is current date + 1
   * For the purposes of API automation, Organization name can be constant.
   * This works because server will not validate the org name. so we are leveraging that here.
   * @param env
   * @param orgId
   * @return expiredJWTToken
   */
  public static String getJWTForAdmin(String env, int orgId) {

    String JWT_SECRET_KEY = (env =="STAGE") ? JWT_SECRET_KEY_STAGE_ENV : JWT_SECRET_KEY_PROD_ENV;
    int USER_ID = (env =="STAGE") ? STAGE_ADMIN_USER_ID : PROD_USER_ID ;
    log.info("env " + env);
    log.info("User_Id:" + USER_ID);
    log.info("org id " + orgId);

    try {
      jwtToken =
          Jwts.builder()
              .setHeaderParam("typ", "JWT")
              .claim("id", USER_ID)
              .claim("email", emailId)
              .claim("first_name", firstName)
              .claim("last_name", lastName)
              .claim("session_id", SESSION_ID)
              .claim("org_id", orgId)
              .claim("org_name", orgName)
              .claim("orgs", EMPTY_ARRAY)
              .setIssuedAt(Date.from(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)))
              .setExpiration(Date.from(Instant.ofEpochSecond((System.currentTimeMillis() / 1000) + 60 * 60 * 24 )))
              .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes("UTF-8")), SignatureAlgorithm.HS256)
              .compact();
    }catch (Exception e){
      log.error("Error in Generating JWT Token in " + env);
      log.error(e.getMessage());
    }
    log.info(jwtToken);
    return jwtToken;
  }

  /**
   *
   * We are passing start date as 24 hrs from current time and end date as (current time - 1) second
   * Returns an Expired JWT Token.
   *
   * @param env
   * @param orgId
   * @return expiredJWTToken
   *
   */


  public static String getExpiredJWTForAdmin(String env, int orgId) {

    String JWT_SECRET_KEY = (env =="STAGE") ? JWT_SECRET_KEY_STAGE_ENV : JWT_SECRET_KEY_PROD_ENV;
    int USER_ID = (env =="STAGE") ? STAGE_ADMIN_USER_ID : PROD_USER_ID ;
    log.info("org id" + orgId);

    try {
      expiredJWTToken =
          Jwts.builder()
              .setHeaderParam("typ", "JWT")
              .claim("id", USER_ID)
              .claim("email", emailId)
              .claim("first_name", firstName)
              .claim("last_name", lastName)
              .claim("session_id", SESSION_ID)
              .claim("org_id", orgId)
              .claim("org_name", orgName)
              .claim("orgs", EMPTY_ARRAY)
              .setIssuedAt(Date.from(Instant.ofEpochSecond(System.currentTimeMillis() / 1000 - 60 * 60 * 24 )))
              .setExpiration(Date.from(Instant.ofEpochSecond((System.currentTimeMillis() / 1000) - 60 )))
              .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes("UTF-8")), SignatureAlgorithm.HS256)
              .compact();
    }catch (Exception e){
      log.error("Error in Generating JWT Token in " + env);
    }
    return expiredJWTToken;
  }


}