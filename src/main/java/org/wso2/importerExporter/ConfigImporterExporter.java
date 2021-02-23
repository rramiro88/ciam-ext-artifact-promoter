package org.wso2.importerExporter;

import java.io.IOException;

/**
 * Interface intended to group all exporter classes.
 */
public interface ConfigImporterExporter {
    void exportConfig(String plainSourceCredentials, String sourceURL) throws IOException;
    void importConfig(String plainDestinationCredentials, String destinationURL) throws IOException;
}
