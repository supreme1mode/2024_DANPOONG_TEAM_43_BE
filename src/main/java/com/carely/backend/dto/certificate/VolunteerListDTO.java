package com.carely.backend.dto.certificate;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VolunteerListDTO {
    private String identity;
    private String username;
    //유저타입도 따로
    private String date;
    private Integer volunteerHours;
    private String volunteerType;
    private String userAddress;

}
