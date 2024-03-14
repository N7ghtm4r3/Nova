package com.tecknobit.nova.helpers;

import com.tecknobit.mantis.Mantis;
import com.tecknobit.nova.records.project.Project;
import com.tecknobit.nova.records.release.Release;
import com.tecknobit.nova.records.release.events.RejectedReleaseEvent;
import com.tecknobit.nova.records.release.events.RejectedTag;
import com.tecknobit.nova.records.release.events.ReleaseEvent;
import com.tecknobit.nova.records.release.events.ReleaseStandardEvent;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static com.tecknobit.nova.helpers.ResourcesProvider.*;

public class ReportsProvider {

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
        String report = createHeader() + insertReleaseEvents();
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser
                .builder(options)
                .build();
        HtmlRenderer renderer = HtmlRenderer
                .builder(options)
                .build();
        Node document = parser.parse(report);
        String html = renderer.render(document);
        PdfConverterExtension.exportToPdf(RESOURCES_PATH + reportName, html, "", DataHolder.NULL);
    }

    private String createHeader() {
        Project project = currentRelease.getProject();
        return insertLogo(project) +
                "# " + project.getName() + "\n\n" +
                "**<span style=\"font-size:20px;\">v. " + currentRelease.getReleaseVersion() + "</span>**\n\n" +
                currentRelease.getReleaseNotes() + "\n" +
                "<hr>";
    }

    private String insertLogo(Project project) {
        return "<img src=\"file:" + RESOURCES_PATH + project.getLogoUrl() + "\"" +
                " width=\"125\" " +
                " style=\"border-radius:25%;\"/>\n\n";
    }

    private String insertReleaseEvents() {
        StringBuilder report = new StringBuilder();
        for (ReleaseEvent event : currentRelease.getReleaseEvents()) {
            Release.ReleaseStatus status = ((ReleaseStandardEvent) event).getStatus();
            report.append("<span style=\"color:")
                    .append(status.getColor()).append(";\">")
                    .append(status.name())
                    .append("</span>").append(BREAK_LINE);
            report.append(event.getReleaseEventDate()).append(BREAK_LINE);
            if(status != Release.ReleaseStatus.Rejected)
                report.append(getStatusComment(status)).append(BREAK_LINE);
            else {
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
        }
        return report.toString();
    }

    private String getStatusComment(Release.ReleaseStatus status) {
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