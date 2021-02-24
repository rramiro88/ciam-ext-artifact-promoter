package org.wso2.dto;

/**
 * Interface intended to store constants.
 */
public interface Constants {
    String OPERATION_UPDATE = "UPDATE";
    String RESIDENT_IDP = "residentIdP";
    String EMAIL_TEMPLATES = "emailTemplates";
    String CLAIMS = "claims";
    String TEMP_FOLDER = "/tmp";

    String FOLDER_NAME_RIDP = "/residentIdP";
    String PATCH_PATH_RIDP = "/t/carbon.super/api/server/v1/identity-governance/:categoryId/connectors/:itemId";
    String GET_CATEGORY_PATH_RIDP = "/t/carbon.super/api/server/v1/identity-governance";
    String GET_ITEM_PATH_RIDP = "/t/carbon.super/api/server/v1/identity-governance/:categoryId/connectors";

    String FOLDER_NAME_EMAIL_TEMPLATES = "/emailTemplates";
    String PATCH_PATH_EMAIL_TEMPLATES = "/t/carbon.super/api/server/v1/email/template-types/:categoryId";
    String GET_CATEGORY_PATH_EMAIL_TEMPLATES = "/t/carbon.super/api/server/v1/email/template-types";
    String GET_ITEM_PATH_EMAIL_TEMPLATES = "/t/carbon.super/api/server/v1/email/template-types/:categoryId";

}
