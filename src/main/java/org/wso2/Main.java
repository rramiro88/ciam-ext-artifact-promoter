package org.wso2;

import org.wso2.importerExporter.ConfigImporterExporter;
import org.wso2.importerExporter.ResidentIdpConfigImporterExporterImpl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This utility can be used as Identity Server configurations importer and exporter.
 * On the config.properties file there's two props to be configured:
 * filesFolder -> the folder where the output files will be stored in --export mode
 * zipPath -> the path where the zip file is located, for --import mode.
 * Usage:
 * Exporter: java -jar ciam-ext-artifact-promoter.jar --export user:pass https://sourceURL:port
 * Importer: java -jar ciam-ext-artifact-promoter.jar --import user:pass https://destinationURL:port
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ConfigImporterExporter residentIdpConfigImpExp = new ResidentIdpConfigImporterExporterImpl();

        if (args.length < 3) {
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Invalid arguments");
        } else {
            String action = args[0];
            String plainCredentials = args[1];
            String url = args[2];
            if (action.equals("--import")) {
                residentIdpConfigImpExp.importConfig(plainCredentials, url);
            } else if (action.equals("--export")) {
                residentIdpConfigImpExp.exportConfig(plainCredentials, url);
            }
        }
    }
}
