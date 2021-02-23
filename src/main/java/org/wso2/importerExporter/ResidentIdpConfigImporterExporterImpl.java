package org.wso2.importerExporter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.Main;
import org.wso2.dto.CategoryDTO;
import org.wso2.dto.ConnectorDTO;
import org.wso2.dto.Constants;
import org.wso2.dto.PatchRequestDTO;
import org.wso2.util.FilesHelper;
import org.wso2.util.RestHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.wso2.util.FilesHelper.removeSpaces;
import static org.wso2.util.FilesHelper.writeYaml;
import static org.wso2.util.RestHelper.getEncodedCredentials;

/**
 * This class is used to export the Resident IdP configuration.
 */
public class ResidentIdpConfigImporterExporterImpl implements ConfigImporterExporter {

    public void importConfig(String plainDestinationCredentials, String destinationURL) throws IOException {
        String patchURL = destinationURL + "/t/carbon.super/api/server/v1/identity-governance/" +
                ":categoryId/connectors/:connectorId";
        ObjectMapper objectMapper = new ObjectMapper();
        File[] files = FilesHelper.getFiles();
        for (File file : files) {
            List<ConnectorDTO> connectorDTOS = FilesHelper.readYaml(file, ConnectorDTO.class);
            for (ConnectorDTO connectorDTO : connectorDTOS) {
                String categoryId = file.getName().split("-")[1].split("\\.")[0];
                Request patchRequest = Request.Patch(parametrizeURL(patchURL, categoryId, connectorDTO.getId()));
                patchRequest.setHeader("Authorization", "Basic "
                        + RestHelper.getEncodedCredentials(plainDestinationCredentials));
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
                        System.out.println("Successfully patched connector: " + connectorDTO.getName());
                        System.out.println("With properties:");
                        patchRequestDTO.getProperties().forEach(p -> System.out.println(
                                p.getName() + ":" + p.getValue())
                        );
                    }
                } catch (IOException e) {
                    Logger.getLogger(ResidentIdpConfigImporterExporterImpl.class.getName())
                            .log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void exportConfig(String plainSourceCredentials, String sourceURL) throws IOException {

        String getCategoriesURL = sourceURL + "/t/carbon.super/api/server/v1/identity-governance";
        String getConnectorsURL = sourceURL + "/t/carbon.super/api/server/v1/identity-governance/" +
                ":categoryId/connectors";
        ObjectMapper objectMapper = new ObjectMapper();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();

        String base64SourceCredentials = getEncodedCredentials(plainSourceCredentials);
        Request getCategoriesRequest = Request.Get(getCategoriesURL);
        getCategoriesRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
        HttpResponse httpResponse = getCategoriesRequest.execute().returnResponse();
        if (httpResponse.getEntity() != null) {
            categoryDTOS = objectMapper.readValue(httpResponse.getEntity().getContent(),
                    new TypeReference<List<CategoryDTO>>() {
                    });
        }
        for (CategoryDTO categoryDTO : categoryDTOS) {
            Request getConnectorsRequest = Request.Get(
                    parametrizeURL(getConnectorsURL, categoryDTO.getId())
            );
            getConnectorsRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
            HttpResponse connectorsResponse = null;
            try {
                connectorsResponse = getConnectorsRequest.execute().returnResponse();
            } catch (IOException e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }

            if (Objects.nonNull(connectorsResponse)) {
                List<ConnectorDTO> connectorDTOS = objectMapper.readValue(connectorsResponse.getEntity().getContent(),
                        new TypeReference<List<ConnectorDTO>>() {
                        });
                writeYaml(connectorDTOS, removeSpaces(categoryDTO.getName() + "-" + categoryDTO.getId()));
            }
        }
    }

    private String parametrizeURL(String url, String categoryId) {

        return url.replace(":categoryId", categoryId);
    }

    private String parametrizeURL(String url, String categoryId, String connectorId) {

        return url.
                replace(":categoryId", categoryId).
                replace(":connectorId", connectorId != null ? connectorId : "");
    }
}
