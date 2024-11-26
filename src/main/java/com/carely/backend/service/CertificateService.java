package com.carely.backend.service;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.dto.certificate.VolunteerSessionStruct;
import com.carely.backend.dto.certificate.volunteerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
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
import org.web3j.tx.gas.StaticGasProvider;

@Service
@Slf4j
@RedisHash("VolunteerSession")
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

    public List<VolunteerListDTO> getVolunteerSessionsByUserId(String userId) throws Exception {
        Function function = new Function(
                "getVolunteerSessionsByUserId",
                List.of(new Utf8String(userId)),
                List.of(new TypeReference<DynamicArray<VolunteerSessionStruct>>() {})
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
        DynamicArray<VolunteerSessionStruct> sessionsStruct = (DynamicArray<VolunteerSessionStruct>) decoded.get(0);
        List<VolunteerListDTO> sessions = new ArrayList<>();
        for (VolunteerSessionStruct sessionStruct : sessionsStruct.getValue()) {
            VolunteerListDTO session = VolunteerListDTO.builder()
                    .userId(sessionStruct.userId)
                    .username(sessionStruct.username)
                    .volunteerHours(sessionStruct.volunteerHours.intValue())
                    .date(sessionStruct.date)
                    .volunteerType(sessionStruct.volunteerType)
                    .userAddress(sessionStruct.userAddress)
                    .build();
            sessions.add(session);
        }

        return sessions;
    }

    public String issueCertificate(String certificateId, Long userId, String username, String issueDate) throws Exception {
        // 총 봉사 시간을 계산
        int totalVolunteerHours = calculateTotalVolunteerHours(userId.toString());
        log.info("Calculated total volunteer hours for user {}: {}", userId, totalVolunteerHours);
        if (totalVolunteerHours < 80) {
            throw new RuntimeException("Total volunteer hours must be at least 80. Current: " + totalVolunteerHours);
        }
        // Solidity 함수 호출을 위한 Function 객체 생성
        Function function = new Function(
                "issueCertificate",
                Arrays.asList(
                        new Utf8String(certificateId),  // Certificate ID
                        new Utf8String(userId.toString()),         // 사용자 ID
                        new Utf8String(username),       // 사용자 이름
                        new Utf8String(issueDate)   // 발급일
                        //new Uint256(BigInteger.valueOf(totalVolunteerHours)) // 총 봉사 시간

                ),
                Collections.emptyList() // 반환값이 없음s
        );

        // 트랜잭션 전송
        String transactionHash = sendTransaction(function);
        log.info("Certificate issued successfully. Transaction Hash: {}", transactionHash);
        return transactionHash; // 트랜잭션 해시 반환
    }

    public ResponseEntity<CertificateDTO> getCertificateById(String certificateId) throws Exception {
        // Define the Solidity function call
        Function function = new Function(
                "getCertificateById",
                Collections.singletonList(new Utf8String(certificateId)), // Input parameter
                Arrays.asList(
                        new TypeReference<Utf8String>() {}, // Certificate ID
                        new TypeReference<Utf8String>() {}, // User ID
                        new TypeReference<Utf8String>() {}, // Username
                        new TypeReference<Uint256>() {},   // Total Hours
                        new TypeReference<Utf8String>() {}  // Issue Date
                )
        );

        // Encode the function for the smart contract call
        String encodedFunction = FunctionEncoder.encode(function);

        // Make the call to the contract
        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        // Check for errors in the response
        if (response.hasError()) {
            throw new RuntimeException("Error fetching certificate: " + response.getError().getMessage());
        }

        // Debugging: Print the raw response
        System.out.println("Raw Response: " + response.getValue());

        // Decode the response
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        for (Type<?> type : decoded) {
            System.out.println("Decoded Value: " + type.getValue());
        }

        // Map decoded values to variables
        String decodedCertificateId = decoded.get(0).getValue().toString();
        String decodedUserId = decoded.get(1).getValue().toString();
        String decodedUsername = decoded.get(2).getValue().toString();
        BigInteger decodedTotalHours = (BigInteger) decoded.get(3).getValue();
        String decodedIssueDate = decoded.get(4).getValue().toString();

        // Build and return the DTO
        return ResponseEntity.status(HttpStatus.OK).body(CertificateDTO.builder()
                .certificateId(decodedCertificateId)
                .userId(decodedUserId)
                .username(decodedUsername)
                .totalHours(decodedTotalHours.intValue()) // Convert BigInteger to int
                .issueDate(decodedIssueDate)
                .build());
    }


    public int calculateTotalVolunteerHours(String userId) throws Exception {
        // Solidity 함수 호출을 위한 Function 객체 생성
        Function function = new Function(
                "calculateTotalVolunteerHours",
                Collections.singletonList(new Utf8String(userId)), // 사용자 ID
                Collections.singletonList(new TypeReference<Uint256>() {}) // 반환값: 총 봉사 시간
        );

        // 함수 호출 및 결과 디코딩
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        if (response.hasError()) {
            throw new RuntimeException("Error calculating total hours: " + response.getError().getMessage());
        }

        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        return ((Uint256) decoded.get(0)).getValue().intValue();
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
