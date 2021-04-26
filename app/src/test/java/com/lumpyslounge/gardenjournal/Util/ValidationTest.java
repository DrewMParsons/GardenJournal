package com.lumpyslounge.gardenjournal.Util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationTest
{
    

    /*
    * Test empty field
    * */
    @Test
    public void isInputValid_Empty()
    {
        boolean result;
        boolean expected = false;
        String input = "";

        result = Validation.isInputValid(input);
        assertEquals(expected, result);

    }
     /*
    * Test valid String
    * */
    @Test
    public void isInputValid()
    {
        boolean result;
        boolean expected = true;
        String input = "Adddsase";

        result = Validation.isInputValid(input);
        assertEquals(expected,result);

    }

    /*
    * Test correct email
    * */
    @Test
    public void isEmailValid()
    {
        boolean result;
        boolean expected = true;
        String input = "drew@gmail.com";

        result = Validation.isValid(input);
        assertEquals(expected,result);

    }
    /*
    * Test incorrect email
    * */
    @Test
    public void isEmailValid_Incorrect_1()
    {
        boolean result;
        boolean expected = false;
        String input = "drew@gmail";

        result = Validation.isValid(input);
        assertEquals(expected,result);

    }
    /*
    * Test incorrect email
    * */
    @Test
    public void isEmailValid_Incorrect_2()
    {
        boolean result;
        boolean expected = false;
        String input = "drew@gmail..com";

        result = Validation.isValid(input);
        assertEquals(expected,result);

    }
    /*
     * Test incorrect email
     * */
    @Test
    public void isEmailValid_Incorrect_3()
    {
        boolean result;
        boolean expected = false;
        String input = "drew@@gmail.com";

        result = Validation.isValid(input);
        assertEquals(expected,result);

    }
    /*
     * Test incorrect email
     * */
    @Test
    public void isEmailValid_Incorrect_4()
    {
        boolean result;
        boolean expected = false;
        String input = "drewgmail.com";

        result = Validation.isValid(input);
        assertEquals(expected,result);

    }
}