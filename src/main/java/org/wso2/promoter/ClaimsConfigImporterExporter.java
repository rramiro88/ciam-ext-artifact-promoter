package org.wso2.promoter;

import org.wso2.dto.residentidp.GeneralCategoryDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the logic to import and export claims configuration
 * on WSO2 Identity Server.
 */
public class ClaimsConfigImporterExporter extends AbstractConfigImporterExporter {
    @Override
    protected void processInputFiles(String plainDestinationCredentials, String patchURL, List<File> files)
            throws IOException {

    }

    @Override
    protected void saveResponseToFiles(String getItemsURL, List<GeneralCategoryDTO> categoryDTOS,
                                       String base64SourceCredentials) throws IOException {

    }
}
