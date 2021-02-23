package org.wso2.dto.emailtemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Builder
public class TemplateDTO {
    private String contentType;
    private String subject;
    private String body;
    private String footer;
    private String id;
}
