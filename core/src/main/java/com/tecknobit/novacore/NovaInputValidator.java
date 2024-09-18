package com.tecknobit.novacore;

import com.tecknobit.equinox.inputs.InputValidator;

import java.util.List;

/**
 * The {@code NovaInputValidator} class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class NovaInputValidator extends InputValidator {

    /**
     * {@code PROJECT_NAME_MAX_LENGTH} the max valid length for the name of a project 
     */
    public static final int PROJECT_NAME_MAX_LENGTH = 25;

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
     * Constructor to init the {@link NovaInputValidator} class <br>
     *
     * No-any params required
     */
    private NovaInputValidator() {
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

}
