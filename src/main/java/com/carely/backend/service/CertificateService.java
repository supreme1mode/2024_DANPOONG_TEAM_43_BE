package com.carely.backend.service;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

@Service
@Slf4j
public class CertificateService {
    public String createVolunteerSession(Volunteer volunteer) {
        String volunteerType = null;
        if (volunteer.getVolunteer().getUserType().equals(UserType.VOLUNTEER)) {
            volunteerType = UserType.VOLUNTEER.name();
        }
        else if ((volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER)) && (!volunteer.getVolunteer().getCertificateCheck())){
            volunteerType = UserType.VOLUNTEER.name();
        }
        else if (volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER)) {
            volunteerType = UserType.CARE_WORKER.name();
        }

        Function function = new Function(
                "createVolunteerSession",
                Arrays.asList(new Utf8String(volunteer.getVolunteer().getId().toString()), new Utf8String(volunteer.getVolunteer().getUsername()), new Uint256(BigInteger.valueOf(volunteer.getDurationHours())),  new Utf8String(volunteer.getDate().toString()),  new Utf8String(volunteerType)),
                Collections.emptyList()
        );
        return null;
    }
}
