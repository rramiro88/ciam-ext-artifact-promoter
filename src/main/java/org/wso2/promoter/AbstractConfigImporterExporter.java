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

/**
 * Abstract class with common behavior.
 */
public abstract class AbstractConfigImporterExporter implements ConfigImporterExporter {

    protected String patchEndpointPath;
    protected String getCategoryPath;
    protected String getItemPath;
    protected String baseFolder;

    /**
     * This method implementation must contain the specific logic to process
     * the files during the import process.
     * @param plainDestinationCredentials
     * @param patchURL
     * @param files
     * @throws IOException
     */
    protected abstract void processInputFiles(String plainDestinationCredentials, String patchURL,
                                              List<File> files) throws IOException;

    /**
     * This method implementation must contain the specific logic to handle the rest response
     * and save it into yaml files.
     * @param getItemsURL
     * @param categoryDTOS
     * @param base64SourceCredentials
     * @throws IOException
     */
    protected abstract void saveResponseToFiles(String getItemsURL, List<GeneralCategoryDTO> categoryDTOS,
                                                String base64SourceCredentials) throws IOException;

    public void importConfig(String plainDestinationCredentials, String destinationApiURL) throws IOException {

        String patchURL = destinationApiURL + patchEndpointPath;
        List<File> files = FilesHelper.getFiles(baseFolder);
        processInputFiles(plainDestinationCredentials, patchURL, files);
        FilesHelper.cleanTempFolder();
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
            categoryDTOS = FilesHelper.parseResponseToList(httpResponse.getEntity().getContent(),
                    GeneralCategoryDTO.class);
        }
        saveResponseToFiles(getItemsURL, categoryDTOS, base64SourceCredentials);
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
