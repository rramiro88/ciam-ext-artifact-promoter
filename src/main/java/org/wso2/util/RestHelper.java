package org.wso2.util;

import java.util.Base64;

public class RestHelper {

        public static String getEncodedCredentials(String plainCredentials) {

            byte[] plainCredentialsBytes = plainCredentials.getBytes();
            byte[] base64CredentialsBytes = Base64.getEncoder().encode(plainCredentialsBytes);
            return new String(base64CredentialsBytes);
        }
}
