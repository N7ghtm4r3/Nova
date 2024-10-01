package com.tecknobit.nova.helpers;

import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.apimanager.trading.TradingTools;
import com.tecknobit.mantis.Mantis;
import com.tecknobit.nova.helpers.resources.NovaResourcesManager;
import com.tecknobit.novacore.records.project.Project;
import com.tecknobit.novacore.records.release.Release;
import com.tecknobit.novacore.records.release.Release.ReleaseStatus;
import com.tecknobit.novacore.records.release.events.*;
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The {@code ReportsProvider} class is useful to create and provide the reports for the releases
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class ReportsProvider implements NovaResourcesManager {

    /**
     * {@code PROJECT_LOGO_TAG} the tag to indicate where is the project logo and where place the real value
     */
    public static final String PROJECT_LOGO_TAG = "<project_logo>";

    /**
     * {@code PROJECT_NAME_TAG} the tag to indicate where is the project name and where place the real value
     */
    public static final String PROJECT_NAME_TAG = "<project_name>";

    /**
     * {@code RELEASE_VERSION_TAG} the tag to indicate where is the release version and where place the real value
     */
    public static final String RELEASE_VERSION_TAG = "<release_version>";

    /**
     * {@code RELEASE_CREATION_DATE_TAG} the tag to indicate where is the release creation date and where place the
     * real value
     */
    public static final String RELEASE_CREATION_DATE_TAG = "<release_creation_date>";

    /**
     * {@code RELEASE_STATUS_TAG} the tag to indicate where is the release status and where place the real value
     */
    public static final String RELEASE_STATUS_TAG = "<release_status>";

    /**
     * {@code RELEASE_NOTES_TAG} the tag to indicate where are the release notes and where place the real value
     */
    public static final String RELEASE_NOTES_TAG = "<release_notes>";

    /**
     * {@code RELEASE_EVENTS_TAG} the tag to indicate where are the release events and where place the real value
     */
    public static final String RELEASE_EVENTS_TAG = "<release_events>";

    /**
     * {@code SLASH} the slash character
     */
    private static final String SLASH = "/";

    /**
     * {@code UNDERSCORE} the underscore character
     */
    private static final String UNDERSCORE = "_";

    /**
     * {@code VERSION_REGEX} the regex to clear the version of a release
     */
    private static final String VERSION_REGEX = "v\\. ";

    /**
     * {@code PDF_EXTENSION} extension to apply to the reports files
     */
    private static final String PDF_EXTENSION = ".pdf";

    /**
     * {@code timeFormatter} the formatter used to format the timestamp values
     */
    private static final TimeFormatter timeFormatter = TimeFormatter.getInstance("dd_MM_YYYY");

    /**
     * {@code reportTemplate} the report template to use and where place the real instead of the tags
     */
    private String reportTemplate;

    {
        try {
            reportTemplate = ResourcesUtils.getResourceContent("report_template.md", this.getClass());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code mantis} helper to manage the multi-language of the report
     */
    private static final Mantis mantis;

    static {
        try {
            mantis = new Mantis(Locale.getDefault());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code BREAK_LINE} constant of the break line html tag
     */
    private static final String BREAK_LINE = "<br>";

    /**
     * {@code currentRelease} the release on which create its report
     */
    private Release currentRelease;

    /**
     * {@code reportName} the report name
     */
    private String reportName;

    /**
     * Method to create the report for a release
     *
     * @param release: the release on which create its report
     *
     * @return the pathname of the report created
     *
     * @apiNote if an existing report already exists, but is different from the current release status, will be deleted
     * and replaced with a new one
     * @throws Exception when an error occurred
     */
    public String getReleaseReport(Release release) throws Exception {
        setCurrentRelease(release);
        deleteReleaseReportIfExists();
        File report = new File(reportName);
        if(!report.exists())
            createReport();
        return reportName;
    }

    /**
     * Method to delete a report if an existing report already exists and is different from the current release status
     *
     * @throws Exception when an error occurred
     */
    private void deleteReleaseReportIfExists() throws Exception {
        File reports = new File(RESOURCES_REPORTS_PATH);
        File reportToDelete = null;
        for (File report : Objects.requireNonNull(reports.listFiles())) {
            String checkReportName = report.getName();
            if(checkReportName.contains(removeVersionPrefix()) && !reportName.endsWith(checkReportName)) {
                reportToDelete = report;
                break;
            }
        }
        if(reportToDelete != null)
            if(!reportToDelete.delete())
                throw new Exception();
    }

    /**
     * Method to set the current release details
     *
     * @param currentRelease: the current release details to set the {@link #currentRelease} instance
     */
    private void setCurrentRelease(Release currentRelease) {
        this.currentRelease = currentRelease;
        reportName = formatReportName();
    }

    /**
     * Method to format the name for the report <br>
     *
     * No-any params required
     * @return name formatted as {@link String}
     */
    private String formatReportName() {
        String reportName = REPORTS_DIRECTORY + SLASH + currentRelease.getProject().getName() + UNDERSCORE;
        reportName += removeVersionPrefix() + UNDERSCORE;
        reportName += currentRelease.getStatus() + UNDERSCORE;
        reportName += timeFormatter.formatAsString(currentRelease.getLastEvent());
        return reportName + PDF_EXTENSION;
    }

    /**
     * Method to clear the version of a {@link Release} using the {@link #VERSION_REGEX}
     *
     * No-any params required
     * @return version formatted as {@link String}
     */
    private String removeVersionPrefix() {
        return currentRelease.getReleaseVersion().replaceFirst(VERSION_REGEX, "");
    }

    /**
     * Method to create the release report <br>
     * No-any params required
     */
    private void createReport() {
        insertHeader();
        insertReleaseEvents();
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser
                .builder(options)
                .build();
        HtmlRenderer renderer = HtmlRenderer
                .builder(options)
                .build();
        Node document = parser.parse(reportTemplate);
        String html = renderer.render(document);
        PdfConverterExtension.exportToPdf(RESOURCES_PATH + reportName, html, "", DataHolder.NULL);
    }

    /**
     * Method to insert the header of the report, so insert, replacing the related tags, the project logo, project name,
     * release version, the creation date of the release, the current status of the release and the notes of
     * the release.<br>
     * No-any params required
     */
    private void insertHeader() {
        Project project = currentRelease.getProject();
        reportTemplate = insertLogo(project)
                .replaceAll(PROJECT_NAME_TAG, project.getName())
                .replaceAll(RELEASE_VERSION_TAG, currentRelease.getReleaseVersion())
                .replaceAll(RELEASE_CREATION_DATE_TAG, mantis.getResource("creation_date_key")
                        + " " + currentRelease.getCreationDate())
                .replaceAll(RELEASE_STATUS_TAG, releaseStatusBadge(currentRelease.getStatus()))
                .replaceAll(RELEASE_NOTES_TAG, currentRelease.getReleaseNotes());
    }

    /**
     * Method to insert the project logo replacing the related tag
     * @param project: the project from fetch the logo
     * @return the {@link #reportTemplate} changed with the project logo inserted
     */
    private String insertLogo(Project project) {
        return reportTemplate.replaceAll(PROJECT_LOGO_TAG, RESOURCES_PATH + project.getLogoUrl());
    }

    /**
     * Method to insert and create the structure for the events of the release replacing the related tag<br>
     * No-any params required
     */
    private void insertReleaseEvents() {
        StringBuilder report = new StringBuilder();
        for (ReleaseEvent event : currentRelease.getReleaseEvents()) {
            report.append("<div class=\"container\">");
            report.append("<div class=\"content\">");
            ReleaseStatus status = ((ReleaseStandardEvent) event).getStatus();
            report.append(releaseStatusBadge(status));
            report.append(event.getReleaseEventDate()).append(BREAK_LINE);
            switch (status) {
                case Rejected -> {
                    RejectedReleaseEvent rejectedReleaseEvent = (RejectedReleaseEvent) event;
                    report.append(rejectedReleaseEvent.getReasons()).append(BREAK_LINE);
                    for (RejectedTag rejectedTag : rejectedReleaseEvent.getTags()) {
                        ReleaseEvent.ReleaseTag tag = rejectedTag.getTag();
                        report.append("<span style=\"color:")
                                .append(tag.getColor()).append(";\">")
                                .append(tag.name())
                                .append("</span>").append(BREAK_LINE);
                        String comment = rejectedTag.getComment();
                        if(comment != null)
                            report.append(comment).append(BREAK_LINE);
                    }
                }
                case Verifying -> {
                    report.append(getStatusComment(status)).append(BREAK_LINE);
                    AssetUploadingEvent assetUploadingEvent = ((AssetUploadingEvent) event);
                    List<AssetUploaded> assets = assetUploadingEvent.getAssetsUploaded();
                    for(int j = 0; j < assets.size(); j++) {
                        AssetUploaded asset = assets.get(j);
                        double spaceOccupied = new File(RESOURCES_PATH + asset.getUrl()).length();
                        report.append("- Asset #")
                                .append(j + 1)
                                .append(" ")
                                .append(TradingTools.roundValue(((spaceOccupied) / (1024 * 1024)), 2))
                                .append(" MB")
                                .append(BREAK_LINE);
                    }
                }
                default -> report.append(getStatusComment(status)).append(BREAK_LINE);
            }
            report.append("</div>");
            report.append("<hr>");
            report.append("</div>");
        }
        reportTemplate = reportTemplate
                .replaceAll(RELEASE_EVENTS_TAG, report.toString());
    }

    /**
     * Method to create the release status badge
     *
     * @param status: the status from create the related badge
     * @return release status badge created as {@link String}
     */
    private String releaseStatusBadge(ReleaseStatus status) {
        return "<b><span style=\"color:" +
                status.getColor() + ";" +
                "font-size:20px" +
                "\">" +
                status.name() +
                "</span></b>" +
                BREAK_LINE;
    }

    /**
     * Method to get the comment related to the release status
     *
     * @param status: the status from get the related comment
     * @return the comment related to the release status as {@link String}
     */
    private String getStatusComment(ReleaseStatus status) {
        return switch (status) {
            case Verifying -> mantis.getResource("new_asset_has_been_uploaded_key");
            case Approved -> mantis.getResource("asset_has_been_approved_key");
            case Alpha -> mantis.getResource("alpha_timeline_message_key");
            case Beta -> mantis.getResource("beta_timeline_message_key");
            case Latest -> mantis.getResource("latest_timeline_message_key");
            default -> null;
        };
    }

}