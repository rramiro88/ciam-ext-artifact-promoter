package org.wso2.dto.residentidp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CategoryDTO {
    private String id;
    private String name;
    private List<ConnectorDTO> connectors;
}
