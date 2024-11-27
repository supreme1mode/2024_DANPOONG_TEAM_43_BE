package com.carely.backend.dto.certificate;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;

import org.web3j.abi.datatypes.DynamicStruct;

public class VolunteerSessionStruct extends DynamicStruct {
    public final String userId;
    public final String username;
    public final BigInteger volunteerHours;
    public final String date;
    public final String volunteerType;
    public final String userAddress;

    public VolunteerSessionStruct(Utf8String userId, Utf8String username, Uint256 volunteerHours,
                                  Utf8String date, Utf8String volunteerType, Address userAddress) {
        super(userId, username, volunteerHours, date, volunteerType, userAddress);
        this.userId = userId.getValue();
        this.username = username.getValue();
        this.volunteerHours = volunteerHours.getValue();
        this.date = date.getValue();
        this.volunteerType = volunteerType.getValue();
        this.userAddress = userAddress.toString();
    }
}