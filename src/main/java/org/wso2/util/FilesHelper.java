package org.wso2.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.wso2.dto.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class is used to help with files related tasks.
 */
public class FilesHelper {
    public static void writeYaml(Object input, String name) throws IOException {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        File file = new File(PropertiesUtil.readProperty("filesFolder") + "/" + name + ".yaml");
        file.getParentFile().mkdirs();
        om.writeValue(file, input);
    }

    public static String removeSpaces(String source) {

        return source.replace(" ", "");
    }

    public static <T> List<T> readYaml(File file, Class<T> cls) throws IOException {

        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, cls);
        return om.readValue(file, type);
    }

    public static void extractAll(String zipFile, String destinationFolder) throws IOException {

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

    public static File[] getFiles() throws IOException {

        extractAll(PropertiesUtil.readProperty(Constants.ZIP_PATH),
                PropertiesUtil.readProperty(Constants.FILES_FOLDER));
        File folder = new File(PropertiesUtil.readProperty(Constants.FILES_FOLDER));
        return folder.listFiles();
    }

    public static <T> List<T> parseResponse(InputStream content, Class<T> cls) throws IOException {
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, cls);
        return om.readValue(content, type);
    }
}
