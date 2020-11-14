package com.medvedskiy.tests;

import org.junit.jupiter.api.extension.ParameterResolutionException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileAsString {

    /**
     * Class for getting resources as strings
     *
     * @param filePath path to file in resources folder
     * @return string representation of contents of resource file
     */
    public static String getFile(String filePath) {
        ClassLoader loader = FileAsString.class.getClassLoader();
        try (
                InputStream in = Objects.requireNonNull(loader.getResourceAsStream(filePath));
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            in.transferTo(out);
            byte[] data = out.toByteArray();
            return new String(data, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            throw new ParameterResolutionException(String.format("ResourcesLoader : '%s'", filePath), ex);
        }
    }
}
