package org.wso2.promoter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.dto.Constants;
import org.wso2.dto.residentidp.ConnectorDTO;
import org.wso2.dto.residentidp.PatchRequestDTO;
import org.wso2.util.FilesHelper;
import org.wso2.util.EncoderHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to export the Resident IdP configuration.
 */
public class ResidentIdpConfigImporterExporter extends AbstractConfigImporterExporter<ConnectorDTO> {

    public ResidentIdpConfigImporterExporter(String patchEndpointPath, String getCategoryPath, String getItemPath) {
        super.patchEndpointPath = patchEndpointPath;
        super.getCategoryPath = getCategoryPath;
        super.getItemPath = getItemPath;
        super.itemClass = ConnectorDTO.class;
    }

    protected void processInputFiles(String plainDestinationCredentials, String patchURL,
                                     File[] files) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : files) {
            List<ConnectorDTO> connectorDTOS = FilesHelper.readYaml(file, ConnectorDTO.class);
            for (ConnectorDTO connectorDTO : connectorDTOS) {
                String categoryId = file.getName().split("-")[1].split("\\.")[0];
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
