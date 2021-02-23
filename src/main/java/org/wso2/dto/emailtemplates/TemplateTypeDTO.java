package org.wso2.dto.emailtemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for email templates rest calls.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TemplateTypeDTO {
    private String displayName;
    private String id;
    private List<TemplateDTO> templates;
}
