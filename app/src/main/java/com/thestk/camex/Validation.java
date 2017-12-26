package com.thestk.camex;


import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

public class Validation {
    public static final int dataLoadSize = 9;
    private static String passErg, passEqualErr, phoneErr, REQUIRED, EmailErrMsg, lallonErr;
    private static Context context;

    public Validation(Context context) {
        this.context = context;
        // Error Messages
        REQUIRED = context.getResources().getString(R.string.required);
        EmailErrMsg = context.getResources().getString(R.string.erremail);
    }


    // private static final String EmailRegistry = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    // return true if the input field is valid, based on the parameter passed
    public static boolean isEmailAddress(EditText editText) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (!hasText(editText))
            return false;

        if (Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            return true;
        }

        editText.setError(context.getString(R.string.erremail));
        return false;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED);
            editText.requestFocus();
            return false;
        }

        return true;
    }


}

