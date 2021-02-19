package org.wso2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.dto.CategoryDTO;
import org.wso2.dto.ConnectorDTO;
import org.wso2.dto.Constants;
import org.wso2.dto.PatchRequestDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

        String action = args[0];
        String plainCredentials = args[1];
        String url = args[2];

        if(Objects.isNull(action) || Objects.isNull(plainCredentials) || Objects.isNull(url)) {
            System.out.println("Invalid parameters");
        } else {
            if (action.equals("--import")) {
                importConfig(plainCredentials, url);
            } else if (action.equals("--export")) {
                exportConfig(plainCredentials, url);
            }
        }
    }

    private static void importConfig(String plainDestinationCredentials, String destinationURL) throws IOException {
        String patchURL = destinationURL + "/t/carbon.super/api/server/v1/identity-governance/" +
                ":categoryId/connectors/:connectorId";
        ObjectMapper objectMapper = new ObjectMapper();
        extractAll(PropertiesUtil.readProperty(Constants.ZIP_PATH), PropertiesUtil.readProperty(Constants.FILES_FOLDER));
        File folder = new File(PropertiesUtil.readProperty(Constants.FILES_FOLDER));
        File[] files = folder.listFiles();
        for (File file : files) {
            List<ConnectorDTO> connectorDTOS = readYaml(file);
            for (ConnectorDTO connectorDTO : connectorDTOS) {
                String categoryId = file.getName().split("-")[1].split("\\.")[0];
                Request patchRequest = Request.Patch(parametrizeURL(patchURL, categoryId, connectorDTO.getId()));
                patchRequest.setHeader("Authorization", "Basic " + getEncodedCredentials(plainDestinationCredentials));
                PatchRequestDTO patchRequestDTO = PatchRequestDTO.builder()
                        .operation(Constants.OPERATION_UPDATE)
                        .properties(connectorDTO.getProperties())
                        .build();
                StringEntity content = new StringEntity(objectMapper.writeValueAsString(patchRequestDTO));
                content.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                patchRequest.body(content);
                HttpResponse patchResponse;
                try {
                    patchResponse = patchRequest.execute().returnResponse();
                    if (patchResponse.getStatusLine().getStatusCode() == 200) {
                        System.out.println("Successfully patched connector: " + connectorDTO.getName());
                        System.out.println("With properties:");
                        patchRequestDTO.getProperties().forEach(p -> System.out.println(
                                p.getName() + ":" + p.getValue())
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void exportConfig(String plainSourceCredentials, String sourceURL) throws IOException {

        String getCategoriesURL = sourceURL + "/t/carbon.super/api/server/v1/identity-governance";
        String getConnectorsURL = sourceURL + "/t/carbon.super/api/server/v1/identity-governance/" +
                ":categoryId/connectors";
        ObjectMapper objectMapper = new ObjectMapper();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();

        String base64SourceCredentials = getEncodedCredentials(plainSourceCredentials);
        Request getCategoriesRequest = Request.Get(getCategoriesURL);
        getCategoriesRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
        HttpResponse httpResponse = getCategoriesRequest.execute().returnResponse();
        System.out.println(httpResponse.getStatusLine());
        if (httpResponse.getEntity() != null) {
            categoryDTOS = objectMapper.readValue(httpResponse.getEntity().getContent(),
                    new TypeReference<List<CategoryDTO>>() {
                    });
        }
        for (CategoryDTO categoryDTO : categoryDTOS) {
            Request getConnectorsRequest = Request.Get(
                    parametrizeURL(getConnectorsURL, categoryDTO.getId())
            );
            getConnectorsRequest.setHeader("Authorization", "Basic " + base64SourceCredentials);
            HttpResponse connectorsResponse = null;
            try {
                connectorsResponse = getConnectorsRequest.execute().returnResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Objects.nonNull(connectorsResponse)) {
                List<ConnectorDTO> connectorDTOS = objectMapper.readValue(connectorsResponse.getEntity().getContent(),
                        new TypeReference<List<ConnectorDTO>>() {});
                writeYaml(connectorDTOS, removeSpaces(categoryDTO.getName()+"-"+categoryDTO.getId()));
            }
        }
    }

    private static String parametrizeURL(String url, String categoryId) {

        return parametrizeURL(url, categoryId, null);
    }

    private static String parametrizeURL(String url, String categoryId, String connectorId) {

        return url.
                replace(":categoryId", categoryId).
                replace(":connectorId", connectorId != null ? connectorId : "");
    }

    private static String getEncodedCredentials(String plainCredentials) {

        byte[] plainCredentialsBytes = plainCredentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(plainCredentialsBytes);
        return new String(base64CredentialsBytes);
    }

    private static void writeYaml(Object input, String name) throws IOException {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        File file = new File(PropertiesUtil.readProperty("filesFolder")+ "/" + name + ".yaml");
        file.getParentFile().mkdirs();
        om.writeValue(file, input);
    }

    private static String removeSpaces(String source) {

        return source.replace(" ", "");
    }

    private static List<ConnectorDTO> readYaml(File file) throws IOException {

        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        return om.readValue(file, new TypeReference<List<ConnectorDTO>>() {
        });
    }

    private static void extractAll(String zipFile, String destinationFolder) throws IOException {

        File destDir = new File(destinationFolder);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory " + parent);
            }
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {

        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}
