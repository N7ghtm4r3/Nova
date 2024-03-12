package com.tecknobit.nova.helpers;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;

public class InputValidator {

    public static final String WRONG_NAME_MESSAGE = "Name is not valid";

    public static final String WRONG_SURNAME_MESSAGE = "Surname is not valid";

    public static final String WRONG_EMAIL_MESSAGE = "Email is not valid";

    public static final String WRONG_PASSWORD_MESSAGE = "Password is not valid";

    public static final String WRONG_MAILING_LIST_MESSAGE = "Mailing list is not valid";

    public static final String WRONG_RELEASE_VERSION_MESSAGE = "The version for the release is not valid";

    public static final String WRONG_RELEASE_NOTES_MESSAGE = "The notes for the release are not valid";

    public static final String WRONG_ASSETS_MESSAGE = "The assets uploaded are not valid";

    public static final String WRONG_REASONS_MESSAGE = "The reasons of the rejection are not valid";

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

    public static boolean isReleaseVersionValid(String releaseVersion) {
        return isInputValid(releaseVersion);
    }

    public static boolean areReleaseNotesValid(String releaseNotes) {
        return isInputValid(releaseNotes);
    }

    public static boolean areRejectionReasonsValid(String reasons) {
        return isInputValid(reasons);
    }

    private static boolean isInputValid(String field) {
        return field != null && !field.isEmpty();
    }

}
