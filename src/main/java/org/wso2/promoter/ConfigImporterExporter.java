package org.wso2.promoter;

import java.io.IOException;

/**
 * Interface intended to group all exporter classes.
 */
public interface ConfigImporterExporter {
    void exportConfig(String plainSourceCredentials, String sourceURL) throws IOException;
    void importConfig(String plainDestinationCredentials, String destinationURL, String endpointPath)
            throws IOException;
}
