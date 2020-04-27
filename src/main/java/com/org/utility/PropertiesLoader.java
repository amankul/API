package com.org.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

    private Properties pro;
    private FileInputStream fi=null;

    public PropertiesLoader(String path){
        pro= new Properties();
        try {
            fi= new FileInputStream(path);
            pro.load(fi);
        }  catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public String getProperty(String propertyName){

        return pro.getProperty(propertyName);
    }

}
