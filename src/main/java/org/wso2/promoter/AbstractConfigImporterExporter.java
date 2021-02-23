package org.wso2.promoter;

import org.wso2.util.FilesHelper;

import java.io.File;
import java.io.IOException;

/**
 * Abstract class with common behavior.
 */
public abstract class AbstractConfigImporterExporter implements ConfigImporterExporter {

    protected abstract void processInputFiles(String plainDestinationCredentials, String patchURL,
                                              File[] files) throws IOException;

    public void importConfig(String plainDestinationCredentials, String destinationApiURL,
                             String endpointPath) throws IOException {

        String patchURL = destinationApiURL + endpointPath;
        File[] files = FilesHelper.getFiles();
        processInputFiles(plainDestinationCredentials, patchURL, files);
    }
}
