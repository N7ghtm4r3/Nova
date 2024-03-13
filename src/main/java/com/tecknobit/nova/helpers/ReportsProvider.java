package com.tecknobit.nova.helpers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.tecknobit.nova.helpers.services.ResourcesManager;
import com.tecknobit.nova.records.release.Release;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

import static com.tecknobit.nova.helpers.ResourcesProvider.*;

public class ReportsProvider implements ResourcesManager {

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

    private void createReport() throws FileNotFoundException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(RESOURCES_PATH + reportName));
        Document layout = new Document(pdfDocument);
        layout.add(new Paragraph().add("Prova documento"));
        pdfDocument.close();
        layout.close();
    }

}
