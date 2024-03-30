package com.tecknobit.novacore;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;

/**
 * The {@code InputValidator} class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class InputValidator {

    /**
     * {@code WRONG_NAME_MESSAGE} error message used when the name inserted is not valid
     */
    public static final String WRONG_NAME_MESSAGE = "Name is not valid";

    /**
     * {@code NAME_MAX_LENGTH} the max valid length for the username
     */
    public static final int NAME_MAX_LENGTH = 20;

    /**
     * {@code PROJECT_NAME_MAX_LENGTH} the max valid length for the name of a project 
     */
    public static final int PROJECT_NAME_MAX_LENGTH = 25;

    /**
     * {@code WRONG_SURNAME_MESSAGE} error message used when the surname inserted is not valid
     */
    public static final String WRONG_SURNAME_MESSAGE = "Surname is not valid";

    /**
     * {@code SURNAME_MAX_LENGTH} the max valid length for the surname 
     */
    public static final int SURNAME_MAX_LENGTH = 30;

    /**
     * {@code WRONG_EMAIL_MESSAGE} error message used when the email inserted is not valid
     */
    public static final String WRONG_EMAIL_MESSAGE = "Email is not valid";

    /**
     * {@code EMAIL_MAX_LENGTH} the max valid length for the email 
     */
    public static final int EMAIL_MAX_LENGTH = 75;

    /**
     * {@code WRONG_PASSWORD_MESSAGE} error message used when the password inserted is not valid
     */
    public static final String WRONG_PASSWORD_MESSAGE = "Password is not valid";

    /**
     * {@code PASSWORD_MIN_LENGTH} the min valid length for the password 
     */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * {@code PASSWORD_MAX_LENGTH} the max valid length for the password
     */
    public static final int PASSWORD_MAX_LENGTH = 32;

    /**
     * {@code WRONG_LANGUAGE_MESSAGE} error message used when the language inserted is not valid
     */
    public static final String WRONG_LANGUAGE_MESSAGE = "Language is not supported";

    /**
     * {@code DEFAULT_LANGUAGE} default language used
     */
    public static final String DEFAULT_LANGUAGE = "ENGLISH";

    /**
     * {@code WRONG_MAILING_LIST_MESSAGE} error message used when the mailing list inserted is not valid
     */
    public static final String WRONG_MAILING_LIST_MESSAGE = "Mailing list is not valid";

    /**
     * {@code WRONG_RELEASE_VERSION_MESSAGE} error message used when the release version inserted is not valid
     */
    public static final String WRONG_RELEASE_VERSION_MESSAGE = "The version for the release is not valid";

    /**
     * {@code RELEASE_VERSION_MAX_LENGTH} the max valid length for the release version
     */
    public static final int RELEASE_VERSION_MAX_LENGTH = 10;

    /**
     * {@code WRONG_RELEASE_NOTES_MESSAGE} error message used when the release notes inserted are not valid
     */
    public static final String WRONG_RELEASE_NOTES_MESSAGE = "The notes for the release are not valid";

    /**
     * {@code RELEASE_NOTES_MAX_LENGTH} the max valid length for the release notes
     */
    public static final int RELEASE_NOTES_MAX_LENGTH = 300;

    /**
     * {@code WRONG_ASSETS_MESSAGE} error message used when the release assets uploaded are not valid
     */
    public static final String WRONG_ASSETS_MESSAGE = "The assets uploaded are not valid";

    /**
     * {@code WRONG_REASONS_MESSAGE} error message used when the rejected reasons inserted are not valid
     */
    public static final String WRONG_REASONS_MESSAGE = "The reasons of the rejection are not valid";

    /**
     * {@code REASONS_MAX_LENGTH} the max valid length for the reasons
     */
    public static final int REASONS_MAX_LENGTH = 300;

    /**
     * {@code WRONG_TAG_COMMENT_MESSAGE} error message used when the tag comment inserted is not valid
     */
    public static final String WRONG_TAG_COMMENT_MESSAGE = "The comment for the tag is not valid";

    /**
     * {@code TAG_COMMENT_MAX_LENGTH} the max valid length for the tag comment
     */
    public static final int TAG_COMMENT_MAX_LENGTH = 300;

    /**
     * {@code emailValidator} helper to validate the emails values
     */
    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    /**
     * {@code urlValidator} helper to validate the urls values
     */
    private static final UrlValidator urlValidator = UrlValidator.getInstance();

    /**
     * {@code LANGUAGES_SUPPORTED} list of the supported languages
     */
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

    /**
     * Constructor to init the {@link InputValidator} class <br>
     *
     * No-any params required
     */
    private InputValidator() {
    }

    /**
     * Method to validate a host
     *
     * @param host: host value to check the validity
     *
     * @return whether the host is valid or not as {@code boolean}
     */
    public static boolean isHostValid(String host) {
        return urlValidator.isValid(host);
    }

    /**
     * Method to validate a server secret
     *
     * @param serverSecret: name value to check the validity
     *
     * @return whether the server secret is valid or not as {@code boolean}
     */
    public static boolean isServerSecretValid(String serverSecret) {
        return isInputValid(serverSecret);
    }

    /**
     * Method to validate a name
     *
     * @param name: name value to check the validity
     *
     * @return whether the name is valid or not as {@code boolean}
     */
    public static boolean isNameValid(String name) {
        return isInputValid(name) && name.length() <= NAME_MAX_LENGTH;
    }

    /**
     * Method to validate a surname
     *
     * @param surname: surname value to check the validity
     *
     * @return whether the surname is valid or not as {@code boolean}
     */
    public static boolean isSurnameValid(String surname) {
        return isInputValid(surname) && surname.length() <= SURNAME_MAX_LENGTH;
    }

    /**
     * Method to validate an email
     *
     * @param email: password value to check the validity
     *
     * @return whether the email is valid or not as {@code boolean}
     */
    public static boolean isEmailValid(String email) {
        return emailValidator.isValid(email) && email.length() <= EMAIL_MAX_LENGTH;
    }

    /**
     * Method to validate a password
     *
     * @param password: password value to check the validity
     *
     * @return whether the password is valid or not as {@code boolean}
     */
    public static boolean isPasswordValid(String password) {
        int passwordLength = password.length();
        return isInputValid(password) && passwordLength >= PASSWORD_MIN_LENGTH && passwordLength <= PASSWORD_MAX_LENGTH;
    }

    /**
     * Method to validate a language
     *
     * @param language: language value to check the validity
     *
     * @return whether the language is valid or not as {@code boolean}
     */
    public static boolean isLanguageValid(String language) {
        return language != null && LANGUAGES_SUPPORTED.contains(language);
    }

    /**
     * Method to validate a project name
     *
     * @param projectName: project name value to check the validity
     *
     * @return whether the project name is valid or not as {@code boolean}
     */
    public static boolean isProjectNameValid(String projectName) {
        return isInputValid(projectName) && projectName.length() <= PROJECT_NAME_MAX_LENGTH;
    }

    /**
     * Method to validate a mailing list
     *
     * @param mailingList: mailing list value to check the validity
     *
     * @return whether the mailing list is valid or not as {@code boolean}
     */
    public static boolean isMailingListValid(String mailingList) {
        mailingList = mailingList.replaceAll(" ", "");
        return isMailingListValid(List.of(mailingList.split(",")));
    }

    /**
     * Method to validate a mailing list
     *
     * @param mailingList: mailing list value to check the validity
     *
     * @return whether the mailing list is valid or not as {@code boolean}
     */
    public static boolean isMailingListValid(List<String> mailingList) {
        if(mailingList != null && !mailingList.isEmpty()) {
            for (String email : mailingList)
                if(!isEmailValid(email))
                    return false;
            return true;
        }
        return false;
    }

    /**
     * Method to validate a release version
     *
     * @param releaseVersion: release version value to check the validity
     *
     * @return whether the release version is valid or not as {@code boolean}
     */
    public static boolean isReleaseVersionValid(String releaseVersion) {
        return isInputValid(releaseVersion) && releaseVersion.length() <= RELEASE_VERSION_MAX_LENGTH;
    }

    /**
     * Method to validate a release notes
     *
     * @param releaseNotes: release notes value to check the validity
     *
     * @return whether the release notes are valid or not as {@code boolean}
     */
    public static boolean areReleaseNotesValid(String releaseNotes) {
        return isInputValid(releaseNotes) && releaseNotes.length() <= RELEASE_NOTES_MAX_LENGTH;
    }

    /**
     * Method to validate a rejected reasons
     *
     * @param reasons: rejected reasons value to check the validity
     *
     * @return whether the rejected reasons are valid or not as {@code boolean}
     */
    public static boolean areRejectionReasonsValid(String reasons) {
        return isInputValid(reasons) && reasons.length() <= REASONS_MAX_LENGTH;
    }

    /**
     * Method to validate a tag comment
     *
     * @param comment: comment value to check the validity
     *
     * @return whether the comment is valid or not as {@code boolean}
     */
    public static boolean isTagCommentValid(String comment) {
        return isInputValid(comment) && comment.length() <= TAG_COMMENT_MAX_LENGTH;
    }

    /**
     * Method to validate an input
     *
     * @param field: field value to check the validity
     *
     * @return whether the field is valid or not as {@code boolean}
     */
    private static boolean isInputValid(String field) {
        return field != null && !field.isEmpty();
    }

}
