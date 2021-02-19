# CIAM artifact promoter

This utility can be used as Identity Server configurations importer and exporter.
On the config.properties file there's two props to be configured:

filesFolder -> the folder where the output files will be stored in --export mode

zipPath -> the path where the zip file is located, for --import mode.

Usage:

Exporter: java -jar ciam-ext-artifact-promoter.jar --export user:pass https://sourceURL:port

Importer: java -jar ciam-ext-artifact-promoter.jar --import user:pass https://destinationURL:port
