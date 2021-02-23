package org.wso2.promoter;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.wso2.dto.residentidp.GeneralCategoryDTO;
import org.wso2.util.EncoderHelper;
import org.wso2.util.FilesHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class with common behavior.
 * @param <T> the class of item-level element.
 */
public abstract class AbstractConfigImporterExporter<T> implements ConfigImporterExporter {

    protected String patchEndpointPath;
    protected String getCategoryPath;
    protected String getItemPath;
    protected Class<T> itemClass;

    protected abstract void processInputFiles(String plainDestinationCredentials, String patchURL,
                                              File[] files) throws IOException;

    public void importConfig(String plainDestinationCredentials, String destinationApiURL) throws IOException {

        String patchURL = destinationApiURL + patchEndpointPath;
        File[] files = FilesHelper.getFiles();
        processInputFiles(plainDestinationCredentials, patchURL, files);
    }

    @Override
    public void exportConfig(String plainSourceCredentials, String sourceURL) throws IOException {

        String getCategoriesURL = sourceURL + getCategoryPath;
        String getItemsURL = sourceURL + getItemPath;
        List<GeneralCategoryDTO> categoryDTOS = new ArrayList<>();

        String base64SourceCredentials = EncoderHelper.getEncodedCredentials(plainSourceCredentials);
        Request getCategoriesRequest = Request.Get(getCategoriesURL);
        getCategoriesRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
        HttpResponse httpResponse = getCategoriesRequest.execute().returnResponse();
        if (httpResponse.getEntity() != null) {
            categoryDTOS = FilesHelper.parseResponse(httpResponse.getEntity().getContent(), GeneralCategoryDTO.class);
        }
        for (GeneralCategoryDTO categoryDTO : categoryDTOS) {
            Request getConnectorsRequest = Request.Get(parametrizeURL(getItemsURL, categoryDTO.getId()));
            getConnectorsRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
            HttpResponse connectorsResponse = getConnectorsRequest.execute().returnResponse();
            if (Objects.nonNull(connectorsResponse)) {
                List<T> itemDTOs = FilesHelper.parseResponse(connectorsResponse.getEntity().getContent(), itemClass);
                FilesHelper.writeYaml(itemDTOs, FilesHelper.removeSpaces(
                        EncoderHelper.decodeId(categoryDTO.getId()) + "-" + categoryDTO.getId())
                );
            }
        }
    }

    protected String parametrizeURL(String url, String categoryId) {

        return url.replace(":categoryId", categoryId);
    }

    protected String parametrizeURL(String url, String categoryId, String itemId) {

        return url.
                replace(":categoryId", categoryId).
                replace(":itemId", itemId != null ? itemId : "");
    }
}
