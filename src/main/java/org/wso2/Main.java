package org.wso2;

import org.wso2.dto.Constants;
import org.wso2.promoter.ConfigImporterExporter;
import org.wso2.promoter.EmailTemplateConfigImporterExporter;
import org.wso2.promoter.ResidentIdpConfigImporterExporter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This utility can be used as Identity Server configurations importer and exporter.
 * On the config.properties file there's two props to be configured:
 * filesFolder -> the folder where the output files will be stored in --export mode
 * zipPath -> the path where the zip file is located, for --import mode.
 * Usage:
 * Exporter: java -jar ciam-ext-artifact-promoter.jar --export entityName user:pass https://sourceURL:port
 * Importer: java -jar ciam-ext-artifact-promoter.jar --import entityName user:pass https://destinationURL:port
 */
public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length < 6) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Invalid arguments");
        } else {
            String action = args[0];
            String entity = args[1];
            String option = args[2];
            String baseFolder = args[3];
            String plainCredentials = args[4];
            String url = args[5];

            switch (entity) {
                case Constants.RESIDENT_IDP: {
                    ConfigImporterExporter residentIdpConfigImpExp =
                            new ResidentIdpConfigImporterExporter(Constants.PATCH_PATH_RIDP,
                                    Constants.GET_CATEGORY_PATH_RIDP, Constants.GET_ITEM_PATH_RIDP, baseFolder);
                    if (action.equals("--import")) {
                        residentIdpConfigImpExp.importConfig(plainCredentials, url);
                    } else if (action.equals("--export")) {
                        residentIdpConfigImpExp.exportConfig(plainCredentials, url);
                    }
                    break;
                }
                case Constants.EMAIL_TEMPLATES: {
                    ConfigImporterExporter emailTemplatesConfigImpExp =
                            new EmailTemplateConfigImporterExporter(Constants.PATCH_PATH_EMAIL_TEMPLATES,
                                    Constants.GET_CATEGORY_PATH_EMAIL_TEMPLATES,
                                    Constants.GET_ITEM_PATH_EMAIL_TEMPLATES, baseFolder);
                    if (action.equals("--import")) {
                        emailTemplatesConfigImpExp.importConfig(plainCredentials, url);
                    } else if (action.equals("--export")) {
                        emailTemplatesConfigImpExp.exportConfig(plainCredentials, url);
                    }
                    break;
                }
            }


        }
    }
}
