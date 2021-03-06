package org.wso2.dto.residentidp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for residentIdP rest calls.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ConnectorDTO {
    private String id;
    private String name;
    private List<PropertyDTO> properties;
}
