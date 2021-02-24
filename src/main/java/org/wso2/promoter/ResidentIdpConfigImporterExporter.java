package org.wso2.promoter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.dto.Constants;
import org.wso2.dto.residentidp.ConnectorDTO;
import org.wso2.dto.residentidp.GeneralCategoryDTO;
import org.wso2.dto.residentidp.PatchRequestDTO;
import org.wso2.util.EncoderHelper;
import org.wso2.util.FilesHelper;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to export the Resident IdP configuration.
 */
public class ResidentIdpConfigImporterExporter extends AbstractConfigImporterExporter {

    public ResidentIdpConfigImporterExporter(String patchEndpointPath, String getCategoryPath, String getItemPath,
                                             String baseFolder) {
        this.patchEndpointPath = patchEndpointPath;
        this.getCategoryPath = getCategoryPath;
        this.getItemPath = getItemPath;
        this.baseFolder = baseFolder;
    }

    @Override
    protected void saveResponseToFiles(String getItemsURL, List<GeneralCategoryDTO> categoryDTOS,
                                       String base64SourceCredentials) throws IOException {
        for (GeneralCategoryDTO categoryDTO : categoryDTOS) {
            Request getConnectorsRequest = Request.Get(parametrizeURL(getItemsURL, categoryDTO.getId()));
            getConnectorsRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
            HttpResponse connectorsResponse = getConnectorsRequest.execute().returnResponse();
            if (Objects.nonNull(connectorsResponse)) {
                List<ConnectorDTO> itemDTOs =
                        FilesHelper.parseResponseToList(connectorsResponse.getEntity().getContent(),
                                ConnectorDTO.class);
                FilesHelper.writeYaml(itemDTOs, EncoderHelper.decodeId(categoryDTO.getId()),
                        Constants.FOLDER_NAME_RIDP, baseFolder);
            }
        }
    }

    protected void processInputFiles(String plainDestinationCredentials, String patchURL,
                                     List<File> files) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : files) {
            List<ConnectorDTO> connectorDTOS = FilesHelper.readListYaml(file, ConnectorDTO.class);
            for (ConnectorDTO connectorDTO : connectorDTOS) {
                String categoryId = EncoderHelper.encodeName(file.getName().split("\\.")[0]);
                Request patchRequest = Request.Patch(parametrizeURL(patchURL, categoryId, connectorDTO.getId()));
                patchRequest.setHeader("Authorization", "Basic "
                        + EncoderHelper.getEncodedCredentials(plainDestinationCredentials));
                PatchRequestDTO patchRequestDTO = PatchRequestDTO.builder()
                        .operation(Constants.OPERATION_UPDATE)
                        .properties(connectorDTO.getProperties())
                        .build();
                StringEntity content = new StringEntity(objectMapper.writeValueAsString(patchRequestDTO));
                content.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                patchRequest.body(content);
                HttpResponse patchResponse;
                try {
                    patchResponse = patchRequest.execute().returnResponse();
                    if (patchResponse.getStatusLine().getStatusCode() == 200) {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Update successful");
                    }
                } catch (IOException e) {
                    Logger.getLogger(ResidentIdpConfigImporterExporter.class.getName())
                            .log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
