package com.carely.backend.dto.certificate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.web3j.abi.datatypes.Address;

@Getter
@Setter
@Builder
public class volunteerDTO {
    private String userId;
    private String username;
    private Integer volunteerHours;
    private String date;
    private String volunteerType;

}
