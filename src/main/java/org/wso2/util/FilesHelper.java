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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class is used to help with files related tasks.
 */
public class FilesHelper {

    private static String tempFolder;

    public static void writeYaml(Object input, String name, String folderName, String baseFolder) throws IOException {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        File file = new File(baseFolder + folderName +
                "/" + name + ".yaml");
        file.getParentFile().mkdirs();
        om.writeValue(file, input);
    }

    public static <T> List<T> readListYaml(File file, Class<T> cls) throws IOException {

        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, cls);
        return om.readValue(file, type);
    }

    public static <T> T readSingleObjectYaml(File file, Class<T> cls) throws IOException {

        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        return om.readValue(file, cls);
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

    public static List<File> getFiles(String zipPath) throws IOException {

        File file = new File(zipPath);
        String destinationFolder = file.getParentFile().getPath() + Constants.TEMP_FOLDER;
        tempFolder = destinationFolder;
        extractAll(zipPath, destinationFolder);
        File folder = new File(destinationFolder);
        return Arrays.stream(folder.listFiles()).filter(File::isFile).collect(Collectors.toList());
    }

    public static <T> List<T> parseResponseToList(InputStream content, Class<T> cls) throws IOException {
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, cls);
        return om.readValue(content, type);
    }

    public static <T> T parseResponseToObject(InputStream content, Class<T> cls) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(content, cls);
    }

    public static void cleanTempFolder() {
        File folder = new File(tempFolder);
        for (File file : folder.listFiles()) {
            file.delete();
        }
        folder.delete();
    }
}
