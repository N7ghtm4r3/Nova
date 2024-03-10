package com.tecknobit.nova.helpers;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;

public class InputValidator {

    public static final String WRONG_NAME_MESSAGE = "Name is not valid";

    public static final String WRONG_SURNAME_MESSAGE = "Surname is not valid";

    public static final String WRONG_EMAIL_MESSAGE = "Email is not valid";

    public static final String WRONG_PASSWORD_MESSAGE = "Password is not valid";

    public static final String WRONG_MAILING_LIST_MESSAGE = "Mailing list is not valid";

    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    private InputValidator() {
    }

    public static boolean isNameValid(String name) {
        return isInputValid(name);
    }

    public static boolean isSurnameValid(String surname) {
        return isInputValid(surname);
    }

    public static boolean isEmailValid(String email) {
        return isInputValid(email) && emailValidator.isValid(email);
    }

    public static boolean isPasswordValid(String password) {
        return isInputValid(password);
    }

    public static boolean isProjectNameValid(String name) {
        return isInputValid(name);
    }

    public static boolean isMailingListValid(List<String> mailingList) {
        if(mailingList != null && !mailingList.isEmpty()) {
            for (String email : mailingList)
                if(!isEmailValid(email))
                    return false;
            return true;
        }
        return false;
    }

    private static boolean isInputValid(String field) {
        return field != null && !field.isEmpty();
    }

}
