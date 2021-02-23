package org.wso2.promoter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.dto.emailtemplates.TemplateDTO;
import org.wso2.dto.emailtemplates.TemplateTypeDTO;
import org.wso2.util.FilesHelper;
import org.wso2.util.EncoderHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used for import and export email templates configurations
 * of Identity Server.
 */
public class EmailTemplateConfigImporterExporter extends AbstractConfigImporterExporter<TemplateDTO> {

    public EmailTemplateConfigImporterExporter(String patchEndpointPath, String getCategoryPath, String getItemPath) {
        super.patchEndpointPath = patchEndpointPath;
        super.getCategoryPath = getCategoryPath;
        super.getItemPath = getItemPath;
        super.itemClass = TemplateDTO.class;
    }

    @Override
    protected void processInputFiles(String plainDestinationCredentials, String patchURL, File[] files)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : files) {
            String templateTypeId = file.getName().split("-")[1].split("\\.")[0];
            List<TemplateTypeDTO> templateTypes = FilesHelper.readYaml(file, TemplateTypeDTO.class);
            for (TemplateTypeDTO templateType : templateTypes) {
                Request patchRequest = Request.Patch(patchURL.replace(":templateTypeId", templateTypeId));
                patchRequest.setHeader("Authorization", "Basic "
                        + EncoderHelper.getEncodedCredentials(plainDestinationCredentials));
                StringEntity content = new StringEntity(objectMapper.writeValueAsString(templateType.getTemplates()));
                content.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                patchRequest.body(content);
                HttpResponse patchResponse = patchRequest.execute().returnResponse();
                if (patchResponse.getStatusLine().getStatusCode() == 200) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Update successful");
                }
            }
        }
    }
}
