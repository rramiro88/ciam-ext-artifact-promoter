# CIAM artifact promoter

This utility can be used as Identity Server configurations importer and exporter.

Usage:

Exporter: java -jar ciam-ext-artifact-promoter.jar --export entityName -f /destination/folder/path user:pass https://sourceURL:port

Importer: java -jar ciam-ext-artifact-promoter.jar --import entityName -f /source/zip/file.zip user:pass https://destinationURL:port
