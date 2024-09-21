package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.apimanager.apis.QRCodeHelper;
import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.mantis.Mantis;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.novacore.records.project.JoiningQRCode;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.inputs.InputValidator.HOST_ADDRESS_KEY;
import static com.tecknobit.novacore.records.project.JoiningQRCode.JOINING_QRCODES_KEY;
import static com.tecknobit.novacore.records.project.JoiningQRCode.JOIN_CODE_KEY;
import static org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText.NAME_KEY;

/**
 * The {@code JoiningQRCodeWebPageProvider} class is useful to provide the dedicated web page of a {@link JoiningQRCode}
 * and manage the invalid accesses
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 *
 */
@Controller
@RequestMapping(BASE_EQUINOX_ENDPOINT + JOINING_QRCODES_KEY)
public class JoiningQRCodeWebPageProvider {

    /**
     * {@code mantis} the translations manager
     */
    protected final Mantis mantis;

    {
        try {
            mantis = new Mantis(Locale.ENGLISH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code INVALID_QR_CODE_PAGE} the page when the QRCode requested is not valid
     */
    private static final String INVALID_QR_CODE_PAGE = "invalid_code";

    /**
     * {@code MAIN_TEXT} the main text Thymeleaf tag
     */
    private static final String MAIN_TEXT = "main_text";

    /**
     * {@code SUB_TEXT} the sub text Thymeleaf tag
     */
    private static final String SUB_TEXT = "sub_text";

    /**
     * {@code codesHelper} helper to manage the {@link JoiningQRCode} database operations
     */
    @Autowired
    private ProjectsHelper codesHelper;

    /**
     * Method to get a web dedicated page for a {@link JoiningQRCode}
     *
     * @param model: the model used by Thymeleaf to format the web page
     * @param request: the http request useful to get whether language the user is using to format properly the page language
     * @param QRCodeId: the qrcode requested
     *
     * @return the correct webpage title to display as {@link String}
     */
    @GetMapping(
            path = "/{" + IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/joiningQRCodes/{id}", method = GET)
    public String loadCustomLinkWebPage(
            Model model,
            HttpServletRequest request,
            @PathVariable(IDENTIFIER_KEY) String QRCodeId
    ) throws Exception {
        mantis.changeCurrentLocale(request.getLocale());
        JoiningQRCode joiningQRCode = codesHelper.getJoiningQrcode(QRCodeId);
        if(joiningQRCode == null || !joiningQRCode.isValid())
            return codeNotExistsOrExpired(joiningQRCode, model);
        return qrcodePage(joiningQRCode, model);
    }

    /**
     * Method to format and return the {@link #INVALID_QR_CODE_PAGE} due the link expiration or the not existing of
     * that link
     *
     * @param joiningQRCode: the custom link requested
     * @param model: the model used by Thymeleaf to format the web page
     * @return the title of the invalid page as {@link String}
     */
    private String codeNotExistsOrExpired(JoiningQRCode joiningQRCode, Model model) {
        model.addAttribute(MAIN_TEXT, mantis.getResource("invalid_code_key"));
        model.addAttribute(SUB_TEXT, mantis.getResource("invalid_code_subtext_key"));
        if(joiningQRCode != null)
            codesHelper.deleteJoiningQrcode(joiningQRCode.getId());
        return INVALID_QR_CODE_PAGE;
    }

    /**
     * Method to format and return the {@link JoiningQRCode#JOIN_CODE_KEY} related page
     *
     * @param joiningQRCode: the custom link requested
     * @param model: the model used by Thymeleaf to format the web page
     *
     * @return the title of the correct page as {@link String}
     **/
    private String qrcodePage(JoiningQRCode joiningQRCode, Model model) throws Exception {
        model.addAttribute(JOIN_CODE_KEY, joiningQRCode);
        model.addAttribute(NAME_KEY, joiningQRCode.getProject().getName());
        QRCodeHelper qrCodeHelper = new QRCodeHelper();
        JSONObject data = new JSONObject()
                .put(HOST_ADDRESS_KEY, "activeLocalSession.hostAddress")
                .put(IDENTIFIER_KEY, joiningQRCode.getId());
        File QRCode = qrCodeHelper.createQRCode(data, "temp.png", 200);
        String URIScheme = APIRequest.createDataURIScheme(QRCode);
        qrCodeHelper.deleteQRCode(QRCode);
        System.out.println(URIScheme);
        model.addAttribute(MAIN_TEXT, URIScheme);

        /*boolean hasUniqueAccess = customLink.hasUniqueAccess();
        model.addAttribute(UNIQUE_ACCESS_KEY, hasUniqueAccess);
        if(hasUniqueAccess)
            model.addAttribute(LINK_UNIQUE_ACCESS_WARN_TEXT, mantis.getResource("link_unique_access_warn_key"));
        if(customLink.mustValidateFields()) {
            model.addAttribute(MAIN_TEXT, mantis.getResource("fill_the_below_form_key"));
            model.addAttribute(VALIDATE_BUTTON_TEXT, mantis.getResource("validate_key"));
        }
        model.addAttribute(RESOURCES_TITLE_TEXT, mantis.getResource("copy_the_resources_key"));
        if(!isPreviewMode && hasUniqueAccess)
            codesHelper.deleteLink(customLink.getId());*/

        return JOIN_CODE_KEY;
    }

}
