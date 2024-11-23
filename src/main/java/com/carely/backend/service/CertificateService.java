package com.carely.backend.service;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
@Slf4j
public class CertificateService {
    protected final Web3j web3j;
    protected final RawTransactionManager txManager;
    protected final StaticGasProvider gasProvider;

    private final GanacheProperties ganacheProperties;

    @Autowired
    public CertificateService(Web3j web3j, GanacheProperties ganacheProperties) {
        this.ganacheProperties = ganacheProperties;
        log.info("Loaded Contract Key: {}", ganacheProperties.getContractKey());
        log.info("Loaded Private Key: {}", ganacheProperties.getPrivateKey());
        this.web3j = Web3j.build(new HttpService("http://127.0.0.1:7545")); // Ganache 노드 URL
        Credentials credentials = Credentials.create(ganacheProperties.getPrivateKey()); // 프라이빗 키
        this.txManager = new RawTransactionManager(web3j, credentials);
        this.gasProvider = new StaticGasProvider(
                new BigInteger("1000000000"), // 가스 가격 (1 Gwei)
                new BigInteger("3000000")    // 가스 한도
        );
        System.out.println("Account Address: " + credentials.getAddress());
        System.out.println("Gas Price: " + gasProvider.getGasPrice());
        System.out.println("Gas Limit: " + gasProvider.getGasLimit());


    }

    public void createVolunteerSession(volunteerDTO volunteer) throws Exception {
        // 개인키로 Credentials 객체를 생성하여 주소를 추출합니다.
        Credentials credentials = Credentials.create(ganacheProperties.getPrivateKey());
        String userAddress = credentials.getAddress(); // 개인키에서 주소를 추출

        // 봉사 세션 생성
        Function function = new Function(
                "createVolunteerSession",
                Arrays.asList(
                        new Utf8String(volunteer.getUserId().toString()),
                        new Utf8String(volunteer.getUsername()),
                        new Uint256(BigInteger.valueOf(volunteer.getVolunteerHours())),
                        new Utf8String(volunteer.getDate().toString()),
                        new Utf8String(volunteer.getVolunteerType()),
                        new Address(userAddress)  // 올바른 이더리움 주소를 전달
                ),
                Collections.emptyList()
        );

        String transactionResult = sendTransaction(function);
        log.info("Transaction Result: {}", transactionResult);
    }







    public List<Map<String, Object>> getVolunteerSessionsByUserId(String userId) throws Exception {
        Function function = new Function(
                "getVolunteerSessionsByUserId",
                List.of(new Utf8String(userId)),
                List.of(
                )
        );

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        if (response.hasError()) {
            throw new RuntimeException("Error fetching sessions: " + response.getError().getMessage());
        }

        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

        List<Uint256> ids = (List<Uint256>) decoded.get(0).getValue();
        List<Utf8String> usernames = (List<Utf8String>) decoded.get(1).getValue();
        List<Uint256> volunteerHours = (List<Uint256>) decoded.get(2).getValue();
        List<Utf8String> dates = (List<Utf8String>) decoded.get(3).getValue();
        List<Utf8String> types = (List<Utf8String>) decoded.get(4).getValue();

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Map<String, Object> session = new HashMap<>();
            session.put("userId", userId);
            session.put("sessionId", ids.get(i).getValue().toString());
            session.put("username", usernames.get(i).getValue());
            session.put("volunteerHours", volunteerHours.get(i).getValue());
            session.put("date", dates.get(i).getValue());
            session.put("volunteerType", types.get(i).getValue());
            sessions.add(session);
        }

        return sessions;
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
                    ganacheProperties.getContractKey(),
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
