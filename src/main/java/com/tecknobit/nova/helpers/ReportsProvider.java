package com.tecknobit.nova.helpers;

import com.tecknobit.mantis.Mantis;
import com.tecknobit.nova.records.project.Project;
import com.tecknobit.nova.records.release.Release;
import com.tecknobit.nova.records.release.Release.ReleaseStatus;
import com.tecknobit.nova.records.release.events.*;
import com.tecknobit.nova.records.release.events.AssetUploadingEvent.AssetUploaded;
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

import static com.tecknobit.apimanager.apis.ResourcesUtils.getResourceContent;
import static com.tecknobit.apimanager.trading.TradingTools.roundValue;
import static com.tecknobit.nova.helpers.ResourcesProvider.*;

public class ReportsProvider {

    public static final String PROJECT_LOGO_TAG = "<project_logo>";

    public static final String PROJECT_NAME_TAG = "<project_name>";

    public static final String RELEASE_VERSION_TAG = "<release_version>";

    public static final String RELEASE_STATUS_TAG = "<release_status>";

    public static final String RELEASE_NOTES_TAG = "<release_notes>";

    public static final String RELEASE_EVENTS_TAG = "<release_events>";

    private String reportTemplate;

    {
        try {
            reportTemplate = getResourceContent("report_template.md", this.getClass());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Mantis mantis;

    static {
        try {
            mantis = new Mantis(Locale.getDefault());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String BREAK_LINE = "<br>";

    private Release currentRelease;

    private String releaseId;

    private String reportName;

    public String getReleaseReport(Release release) throws Exception {
        setCurrentRelease(release);
        deleteReleaseReportIfExists();
        File report = new File(reportName);
        if(!report.exists())
            createReport();
        return reportName;
    }

    private void deleteReleaseReportIfExists() throws Exception {
        File reports = new File(RESOURCES_REPORTS_PATH);
        File reportToDelete = null;
        for (File report : Objects.requireNonNull(reports.listFiles())) {
            String checkReportName = report.getName();
            if(checkReportName.contains(releaseId) && !reportName.endsWith(checkReportName)) {
                reportToDelete = report;
                break;
            }
        }
        if(reportToDelete != null)
            if(!reportToDelete.delete())
                throw new Exception();
    }

    private void setCurrentRelease(Release currentRelease) {
        this.currentRelease = currentRelease;
        releaseId = currentRelease.getId();
        reportName = REPORTS_DIRECTORY + "/" + currentRelease.getId() + "_" + currentRelease.getLastEvent() + ".pdf";
    }

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

    private void insertHeader() {
        Project project = currentRelease.getProject();
        reportTemplate = insertLogo(project)
                .replaceAll(PROJECT_NAME_TAG, project.getName())
                .replaceAll(RELEASE_VERSION_TAG, "v. " + currentRelease.getReleaseVersion())
                .replaceAll(RELEASE_STATUS_TAG, releaseStatusBadge(currentRelease.getStatus()))
                .replaceAll(RELEASE_NOTES_TAG, currentRelease.getReleaseNotes());
    }

    private String insertLogo(Project project) {
        return reportTemplate.replaceAll(PROJECT_LOGO_TAG, RESOURCES_PATH + project.getLogoUrl());
    }

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
                                .append(roundValue(((spaceOccupied) / (1024 * 1024)), 2))
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

    private String releaseStatusBadge(ReleaseStatus status) {
        return "<b><span style=\"color:" +
                status.getColor() + ";" +
                "font-size:20px" +
                "\">" +
                status.name() +
                "</span></b>" +
                BREAK_LINE;
    }

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