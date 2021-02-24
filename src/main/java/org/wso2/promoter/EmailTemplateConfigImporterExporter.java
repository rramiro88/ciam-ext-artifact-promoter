package org.wso2.promoter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.dto.Constants;
import org.wso2.dto.emailtemplates.TemplateTypeDTO;
import org.wso2.dto.residentidp.GeneralCategoryDTO;
import org.wso2.util.EncoderHelper;
import org.wso2.util.FilesHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used for import and export email templates configurations
 * of Identity Server.
 */
public class EmailTemplateConfigImporterExporter extends AbstractConfigImporterExporter {

    public EmailTemplateConfigImporterExporter(String patchEndpointPath, String getCategoryPath, String getItemPath,
                                               String baseFolder) {
        this.patchEndpointPath = patchEndpointPath;
        this.getCategoryPath = getCategoryPath;
        this.getItemPath = getItemPath;
        this.baseFolder = baseFolder;
    }

    @Override
    protected void processInputFiles(String plainDestinationCredentials, String patchURL, List<File> files)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : files) {
            String templateTypeId = EncoderHelper.encodeName(file.getName().split("\\.")[0]);
            TemplateTypeDTO templateTypeDTO = FilesHelper.readSingleObjectYaml(file, TemplateTypeDTO.class);
            Request putRequest = Request.Put(patchURL.replace(":categoryId", templateTypeId));
            putRequest.setHeader("Authorization", "Basic "
                    + EncoderHelper.getEncodedCredentials(plainDestinationCredentials));
            StringEntity content = new StringEntity(objectMapper.writeValueAsString(templateTypeDTO.getTemplates()));
            content.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            putRequest.body(content);
            HttpResponse putResponse = putRequest.execute().returnResponse();
            if (putResponse.getStatusLine().getStatusCode() == 200) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Update successful: "
                        + file.getName());
            }

        }
    }

    @Override
    protected void saveResponseToFiles(String getItemsURL, List<GeneralCategoryDTO> categoryDTOS,
                                       String base64SourceCredentials) throws IOException {
        for (GeneralCategoryDTO categoryDTO : categoryDTOS) {
            Request getConnectorsRequest = Request.Get(parametrizeURL(getItemsURL, categoryDTO.getId()));
            getConnectorsRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
            HttpResponse connectorsResponse = getConnectorsRequest.execute().returnResponse();
            if (Objects.nonNull(connectorsResponse)) {
                TemplateTypeDTO templateTypeDTO =
                        FilesHelper.parseResponseToObject(connectorsResponse.getEntity().getContent(),
                                TemplateTypeDTO.class);
                FilesHelper.writeYaml(templateTypeDTO, EncoderHelper.decodeId(categoryDTO.getId()),
                        Constants.FOLDER_NAME_EMAIL_TEMPLATES, baseFolder);
            }
        }
    }
}
