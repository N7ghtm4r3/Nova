package com.tecknobit.novacore;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;

public class InputValidator {

    public static final String WRONG_NAME_MESSAGE = "Name is not valid";

    public static final int NAME_MAX_LENGTH = 20;

    public static final int PROJECT_NAME_MAX_LENGTH = 25;

    public static final String WRONG_SURNAME_MESSAGE = "Surname is not valid";

    public static final int SURNAME_MAX_LENGTH = 30;

    public static final String WRONG_EMAIL_MESSAGE = "Email is not valid";

    public static final int EMAIL_MAX_LENGTH = 75;

    public static final String WRONG_PASSWORD_MESSAGE = "Password is not valid";

    public static final int PASSWORD_MIN_LENGTH = 8;

    public static final int PASSWORD_MAX_LENGTH = 32;

    public static final String WRONG_LANGUAGE_MESSAGE = "Language is not supported";

    public static final String DEFAULT_LANGUAGE = "ENGLISH";

    public static final String WRONG_MAILING_LIST_MESSAGE = "Mailing list is not valid";

    public static final String WRONG_RELEASE_VERSION_MESSAGE = "The version for the release is not valid";

    public static final int RELEASE_VERSION_MAX_LENGTH = 10;

    public static final String WRONG_RELEASE_NOTES_MESSAGE = "The notes for the release are not valid";

    public static final int RELEASE_NOTES_MAX_LENGTH = 300;

    public static final String WRONG_ASSETS_MESSAGE = "The assets uploaded are not valid";

    public static final String WRONG_REASONS_MESSAGE = "The reasons of the rejection are not valid";

    public static final int REASONS_MAX_LENGTH = 300;

    public static final String WRONG_TAG_COMMENT_MESSAGE = "The comment for the tag is not valid";

    public static final int TAG_COMMENT_MAX_LENGTH = 300;

    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    private static final UrlValidator urlValidator = UrlValidator.getInstance();

    public static final List<String> LANGUAGES_SUPPORTED = List.of(
            "RUSSIAN",
            "ENGLISH",
            "ARABIC",
            "CHINESE",
            "CZECH",
            "DANISH",
            "DUTCH",
            "FRENCH",
            "GERMAN",
            "GREEK",
            "HINDI",
            "ITALIAN",
            "JAPANESE",
            "KOREAN",
            "PORTUGUESE",
            "SLOVAK",
            "SPANISH",
            "SWEDISH",
            "TURKISH",
            "UKRAINIAN"
    );

    private InputValidator() {
    }

    public static boolean isHostValid(String host) {
        return urlValidator.isValid(host);
    }

    public static boolean isServerSecretValid(String serverSecret) {
        return isInputValid(serverSecret);
    }

    public static boolean isNameValid(String name) {
        return isInputValid(name) && name.length() <= NAME_MAX_LENGTH;
    }

    public static boolean isSurnameValid(String surname) {
        return isInputValid(surname) && surname.length() <= SURNAME_MAX_LENGTH;
    }

    public static boolean isEmailValid(String email) {
        return emailValidator.isValid(email) && email.length() <= EMAIL_MAX_LENGTH;
    }

    public static boolean isPasswordValid(String password) {
        int passwordLength = password.length();
        return isInputValid(password) && passwordLength >= PASSWORD_MIN_LENGTH && passwordLength <= PASSWORD_MAX_LENGTH;
    }

    public static boolean isLanguageValid(String language) {
        return language != null && LANGUAGES_SUPPORTED.contains(language);
    }

    public static boolean isProjectNameValid(String name) {
        return isInputValid(name) && name.length() <= PROJECT_NAME_MAX_LENGTH;
    }

    public static boolean isMailingListValid(String mailingList) {
        mailingList = mailingList.replaceAll(" ", "");
        return isMailingListValid(List.of(mailingList.split(",")));
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
        return isInputValid(releaseVersion) && releaseVersion.length() <= RELEASE_VERSION_MAX_LENGTH;
    }

    public static boolean areReleaseNotesValid(String releaseNotes) {
        return isInputValid(releaseNotes) && releaseNotes.length() <= RELEASE_NOTES_MAX_LENGTH;
    }

    public static boolean areRejectionReasonsValid(String reasons) {
        return isInputValid(reasons) && reasons.length() <= REASONS_MAX_LENGTH;
    }

    public static boolean isTagCommentValid(String comment) {
        return isInputValid(comment) && comment.length() <= TAG_COMMENT_MAX_LENGTH;
    }

    private static boolean isInputValid(String field) {
        return field != null && !field.isEmpty();
    }

}
