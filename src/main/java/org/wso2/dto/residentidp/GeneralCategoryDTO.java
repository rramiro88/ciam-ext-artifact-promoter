package org.wso2.dto.residentidp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a general DTO, only consisting in id and name, common to all rest calls.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class GeneralCategoryDTO {
    private String id;
    private String name;
}
