package com.org.map.jwt.api.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class Asserts {


    @Test
    static public void hatest(){


            Assert.assertTrue(false);
            System.out.println("Assertion Failed in hard");             // hard assert throws exception

            Assert.assertFalse(1<0);
            System.out.println("Assertion passed in hard");            // next steps not executed but continues next @test

    }

    @Test
    static public void satest(){

        SoftAssert sa= new SoftAssert();

            sa.assertTrue(false);
            System.out.println("Assertion Failed in soft");        // exception not thrown

            sa.assertFalse(1<0);
            System.out.println("Assertion passed in soft");

                                               // collate all exceptions and fail test - fail + pass = fail


    }


    }

