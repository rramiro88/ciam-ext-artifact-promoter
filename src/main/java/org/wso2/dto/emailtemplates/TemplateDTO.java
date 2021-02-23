package org.wso2.dto.emailtemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for email templates rest calls.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDTO {
    private String contentType;
    private String subject;
    private String body;
    private String footer;
    private String id;
}
