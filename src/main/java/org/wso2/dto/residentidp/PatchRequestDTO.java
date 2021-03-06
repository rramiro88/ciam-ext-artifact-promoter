package org.wso2.dto.residentidp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for residentIdP rest calls.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchRequestDTO implements Serializable {

    private String operation;
    private List<PropertyDTO> properties;
}
