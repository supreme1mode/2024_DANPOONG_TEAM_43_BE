package com.carely.backend.service;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.certificate.volunteerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
@Slf4j
public class CertificateService {
    protected final Web3j web3j;
    protected final String contractAddress = "0x80f728e27AA6B83B7304F024A4a64B1Da37edD2a";
    protected final RawTransactionManager txManager;
    protected final StaticGasProvider gasProvider;

    @Autowired
    public CertificateService(Web3j web3j) {
        this.web3j = Web3j.build(new HttpService("http://127.0.0.1:7545")); // Ganache 노드 URL
        Credentials credentials = Credentials.create("0x130a3322bdcacb9bb8be47a2da848fe5affb732a8b783af5cbb1dad895d46cb6"); // 프라이빗 키
        this.txManager = new RawTransactionManager(web3j, credentials);
        this.gasProvider = new StaticGasProvider(
                new BigInteger("20000000000"), // 가스 가격
                new BigInteger("6000000")      // 가스 한도
        );
    }

    public void createVolunteerSession(volunteerDTO volunteer) throws Exception {
        //String volunteerType = determineVolunteerType(volunteer);

        Function function = new Function(
                "createVolunteerSession",
                Arrays.asList(
                        new Utf8String(volunteer.getUserId().toString()),
                        new Utf8String(volunteer.getUsername()),
                        new Uint256(BigInteger.valueOf(volunteer.getVolunteerHours())),
                        new Utf8String(volunteer.getDate().toString()),
                        new Utf8String(volunteer.getVolunteerType())
                ),
                Collections.emptyList()
        );

        String transactionResult = sendTransaction(function);
        log.info("Transaction Result: {}", transactionResult);
    }

    public void determineVolunteerType(Volunteer volunteer) throws Exception {
        String userType = null;

        if (volunteer.getVolunteer().getUserType().equals(UserType.VOLUNTEER)) {
            userType = UserType.VOLUNTEER.name();
        } else if (volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER) && (!volunteer.getVolunteer().getCertificateCheck())) {
            userType =  UserType.VOLUNTEER.name();
        } else {
            userType =  UserType.CARE_WORKER.name();
        }
        createVolunteerSession(volunteerDTO.builder()
                .userId(volunteer.getVolunteer().getId().toString())
                .username(volunteer.getVolunteer().getUsername())
                .date(volunteer.getDate().toString())
                .volunteerHours(volunteer.getDurationHours())
                .volunteerType(userType)
                .build());
    }

    protected String sendTransaction(Function function) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);
        System.out.println("Encoded Function: " + encodedFunction);

        try {
            EthSendTransaction transactionResponse = txManager.sendTransaction(
                    gasProvider.getGasPrice(),
                    gasProvider.getGasLimit(),
                    contractAddress,
                    encodedFunction,
                    BigInteger.ZERO
            );

            System.out.println("Transaction Response: " + transactionResponse);
            if (transactionResponse.hasError()) {
                System.err.println("Transaction Error: " + transactionResponse.getError().getMessage());
                throw new RuntimeException(transactionResponse.getError().getMessage());
            }

            System.out.println("Transaction Successful. Hash: " + transactionResponse.getTransactionHash());
            return transactionResponse.getTransactionHash();
        } catch (Exception e) {
            System.err.println("Error during transaction execution: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



}
