package com.phunware.m3.constants;

/**
 * Created by knguyen on 7/13/16.
 */
public class GlobalConstants {

    public static String environment = "M3_Stage";

    public static String environment_M3_Stage;
    public static String m3_Profiles_Path;
    public static String m3_Attributes_Path;


    public GlobalConstants(){

        if (environment.equalsIgnoreCase("M3_Stage")) {
            environment_M3_Stage = System.getProperty("user.dir") + "/src/main/java/com/phunware/m3/properties/Environment_M3_Stage.properties";
            m3_Profiles_Path = System.getProperty("user.dir") + "/src/main/java/com/phunware/m3/properties/M3_Profiles_Stage.properties";
            m3_Attributes_Path = System.getProperty("user.dir") + "/src/main/java/com/phunware/m3/properties/M3_Attributes_Stage.properties";
        }
    }

}
