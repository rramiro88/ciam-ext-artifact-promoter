package org.wso2.util;

import java.util.Base64;

/**
 * Utility for helping on encoding and decoding Base64 strings.
 */
public class EncoderHelper {

    public static String getEncodedCredentials(String plainCredentials) {

        byte[] plainCredentialsBytes = plainCredentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(plainCredentialsBytes);
        return new String(base64CredentialsBytes);
    }

    public static String decodeId(String id) {

        return new String(Base64.getDecoder().decode(id));
    }

    public static String encodeName(String name) {

        return Base64.getEncoder().withoutPadding().encodeToString(name.getBytes());
    }
}
