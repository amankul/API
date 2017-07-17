package com.phunware.me_api.constants;


/**
 * Created by VinayKarumuri on 5/11/17.
 */
public class MeAPI_Constants {


  //GLOBAL CONSTANTS
  public static final String SERVICE_END_POINT_STAGE = "https://me-admin-api-stage.phunware.com";
  public static final String SERVICE_ENT_POINT_PROD = "https://me-admin-api.phunware.com";

  public static String DEVICE_API_SERVICE_END_POINT_STAGE = "https://me-api-stage.phunware.com";


  // Location Resource-EndPoint
  public static final String LOCATION_END_POINT = "/v3/location/";
  public static final String LOCATION_END_POINT_1="/v3/location";
  public static final String TAG_END_POINT="/v3/tag";
  public static final String LOCATIONS_DOWNLOAD_END_POINT = "/v3/location-file-download";

  // Beacon Resource-EndPoint

  public static final String BEACON_END_POINT = "/v3/beacon/";
  public static final String BEACON_END_POINT_1="/v3/beacon";
  public static final String BEACON_UUID_ALIAS_END_POINT= "/v3/beaconuuidalias";
  public static final String BEACON_UUID_ALIAS_END_POINT_1= "/v3/beaconuuidalias/";
  public static final String BEACON_TAGS_END_POINT= "/v3/beacontag";


  // Attributes End Point

  public static final String ATTRIBUTE_METADATA_END_POINT = "/v3/attribute-metadata/";
  public static final String ATTRIBUTE_METADATA_END_POINT_1 = "/v3/attribute-metadata";


  // Profiles End Point

  public static final String PROFILES_END_POINT = "/v3/profile/";
  public static final String PROFILES_END_POINT_1 = "/v3/profile";


  // Campaigns End Point

  public static final String CAMPAIGNS_END_POINT = "/v3/campaign/";
  public static final String CAMPAIGNS_END_POINT_1 = "/v3/campaign";
  // in the below URL, <id> needs to be replaced accordingly in the test.
  public static final String CAMPAIGN_STATUS_END_POINT = "/v3/campaign/<id>/status";


  // Device API
  public static final String DEVICE_API_EVENTS_END_POINT = "/v3/events";
  public static final String DEVICE_ATTRIBUTE_API_END_POINT = "/v3/attribute";
  public static final String STATIC_ID = "/v3/static-id";


}
