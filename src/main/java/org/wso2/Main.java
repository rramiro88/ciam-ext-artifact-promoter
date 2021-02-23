package org.wso2;

import org.wso2.dto.Constants;
import org.wso2.promoter.ConfigImporterExporter;
import org.wso2.promoter.EmailTemplateConfigImporterExporter;
import org.wso2.promoter.ResidentIdpConfigImporterExporter;
import org.wso2.util.PropertiesUtil;

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

        if (args.length < 4) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Invalid arguments");
        } else {
            String action = args[0];
            String entity = args[1];
            String plainCredentials = args[2];
            String url = args[3];

            switch (entity) {
                case Constants.RESIDENT_IDP: {
                    String patchEndpointPath = PropertiesUtil.readProperty("patchPathResidentIdp");
                    String getCategoryPath = PropertiesUtil.readProperty("getCategoryPathResidentIdp");
                    String getItemPath = PropertiesUtil.readProperty("getItemPathResidentIdp");
                    ConfigImporterExporter residentIdpConfigImpExp =
                            new ResidentIdpConfigImporterExporter(patchEndpointPath,
                                    getCategoryPath, getItemPath);
                    if (action.equals("--import")) {
                        residentIdpConfigImpExp.importConfig(plainCredentials, url);
                    } else if (action.equals("--export")) {
                        residentIdpConfigImpExp.exportConfig(plainCredentials, url);
                    }
                    break;
                }
                case Constants.EMAIL_TEMPLATES: {
                    String patchEndpointPath = PropertiesUtil.readProperty("patchPathEmailTemplates");
                    String getCategoryPath = PropertiesUtil.readProperty("getCategoryPathEmailTemplates");
                    String getItemPath = PropertiesUtil.readProperty("getItemPathEmailTemplates");
                    ConfigImporterExporter emailTemplatesConfigImpExp =
                            new EmailTemplateConfigImporterExporter(patchEndpointPath, getCategoryPath, getItemPath);
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
