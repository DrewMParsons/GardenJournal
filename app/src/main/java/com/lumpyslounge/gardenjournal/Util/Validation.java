package com.lumpyslounge.gardenjournal.Util;


import android.util.Patterns;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;



public class Validation
{
    public static boolean isInputValid(@Nullable String textField) {
        return textField != null && !textField.isEmpty();
    }

    public static boolean isEmailValid(@Nullable String email)
    {
        if(email == null){
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isValid(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }
    }





}
