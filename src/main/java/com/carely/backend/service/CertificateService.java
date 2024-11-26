package com.carely.backend.service;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.dto.certificate.VolunteerSessionStruct;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.exception.AlreadyExistsGuestBookException;
import com.carely.backend.exception.AlreadyHasCertificateException;
import com.carely.backend.exception.NoCertificateUserException;
import com.carely.backend.exception.TotalTimeNotEnoughException;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
@Slf4j
@RedisHash("VolunteerSession")
public class CertificateService {
    protected final Web3j web3j;
    protected final RawTransactionManager txManager;
    protected final StaticGasProvider gasProvider;

    private final RemixIDEProperties remixIDEProperties;

    @Autowired
    public CertificateService(RemixIDEProperties remixIDEProperties) {
        this.remixIDEProperties = remixIDEProperties;
        log.info("Loaded Contract Key: {}", remixIDEProperties.getContractKey());
        log.info("Loaded Private Key: {}", remixIDEProperties.getPrivateKey());

        // Web3j 초기화
        this.web3j = Web3j.build(new HttpService("http://13.124.232.105:7545")); // 명시적으로 설정

        Credentials credentials = Credentials.create(remixIDEProperties.getPrivateKey()); // 프라이빗 키
        this.txManager = new RawTransactionManager(web3j, credentials);
        this.gasProvider = new StaticGasProvider(
                new BigInteger("1000000000"), // 가스 가격 (1 Gwei)
                new BigInteger("3000000")    // 가스 한도
        );

        System.out.println("Account Address: " + credentials.getAddress());
        System.out.println("Gas Price: " + gasProvider.getGasPrice());
        System.out.println("Gas Limit: " + gasProvider.getGasLimit());
    }

    @Transactional

    public void createVolunteerSession(volunteerDTO volunteer) {
        // 개인키로 Credentials 객체를 생성하여 주소를 추출합니다.
        Credentials credentials = Credentials.create(remixIDEProperties.getPrivateKey());
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

        String transactionResult = null;
        try {
            transactionResult = sendTransaction(function);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Transaction Result: {}", transactionResult);
    }
    @Transactional

    public List<VolunteerListDTO> getVolunteerSessionsByUserId(String userId) {
        Function function = new Function(
                "getVolunteerSessionsByUserId",
                List.of(new Utf8String(userId)), // 함수 입력값
                List.of(
                        new TypeReference<DynamicArray<Utf8String>>() {},  // userIds
                        new TypeReference<DynamicArray<Utf8String>>() {},  // usernames
                        new TypeReference<DynamicArray<Uint256>>() {},     // volunteerHours
                        new TypeReference<DynamicArray<Utf8String>>() {},  // dates
                        new TypeReference<DynamicArray<Utf8String>>() {},  // volunteerTypes
                        new TypeReference<DynamicArray<Address>>() {}      // userAddresses
                )
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthCall response = null;
        try {
            response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, remixIDEProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 디코딩
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

        // 각각의 배열 추출
        DynamicArray<Utf8String> userIds = (DynamicArray<Utf8String>) decoded.get(0);
        DynamicArray<Utf8String> usernames = (DynamicArray<Utf8String>) decoded.get(1);
        DynamicArray<Uint256> volunteerHours = (DynamicArray<Uint256>) decoded.get(2);
        DynamicArray<Utf8String> dates = (DynamicArray<Utf8String>) decoded.get(3);
        DynamicArray<Utf8String> volunteerTypes = (DynamicArray<Utf8String>) decoded.get(4);
        DynamicArray<Address> userAddresses = (DynamicArray<Address>) decoded.get(5);

        // VolunteerListDTO 리스트 생성
        List<VolunteerListDTO> sessions = new ArrayList<>();
        for (int i = 0; i < userIds.getValue().size(); i++) {
            VolunteerListDTO dto = VolunteerListDTO.builder().build();
            dto.setUserId(userIds.getValue().get(i).getValue());
            dto.setUsername(usernames.getValue().get(i).getValue());
            dto.setVolunteerHours(volunteerHours.getValue().get(i).getValue().intValue());
            dto.setDate(dates.getValue().get(i).getValue());
            dto.setVolunteerType(volunteerTypes.getValue().get(i).getValue());
            dto.setUserAddress(userAddresses.getValue().get(i).getValue());

            sessions.add(dto);
        }

        return sessions;
    }

    @Transactional
    public String issueCertificate(String certificateId, Long userId, String username, String issueDate) throws Exception {
        // 총 봉사 시간을 계산
        boolean hasCertificate = checkIfCertificateExists(userId.toString());
        if (hasCertificate) {
            throw new AlreadyHasCertificateException("이미 자격증 존재");
        }
        int totalVolunteerHours = 0;
        totalVolunteerHours = calculateTotalVolunteerHours(userId.toString());

        log.info("Calculated total volunteer hours for user {}: {}", userId, totalVolunteerHours);
        if (totalVolunteerHours < 80) {
            throw new TotalTimeNotEnoughException("시간 부족");
        }
        System.out.println(certificateId);
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
        String transactionHash = null;
        transactionHash = sendTransaction(function);
        log.info("Certificate issued successfully. Transaction Hash: {}", transactionHash);
        return transactionHash; // 트랜잭션 해시 반환
    }


    private boolean checkIfCertificateExists(String userId) {
        // Solidity 함수 호출을 위한 Function 객체 생성
        Function function = new Function(
                "userHasCertificate",
                Collections.singletonList(new Utf8String(userId)), // 사용자 ID
                Collections.singletonList(new TypeReference<Bool>() {}) // 반환값: Boolean
        );

        // 함수 호출 및 결과 디코딩
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = null;
        try {
            response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, remixIDEProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (response.hasError()) {
            throw new RuntimeException("Error checking certificate existence: " + response.getError().getMessage());
        }

        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        return ((Bool) decoded.get(0)).getValue();
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

        try {
            // Make the call to the contract
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, remixIDEProperties.getContractKey(), encodedFunction),
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

        } catch (RuntimeException e) {
            // Handle revert errors and provide meaningful response
            if (e.getMessage().contains("Certificate not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CertificateDTO.builder()
                                .certificateId(null)
                                .userId(null)
                                .username(null)
                                .totalHours(0)
                                .issueDate(null)
                                .build());
            } else {
                throw e; // Rethrow unexpected errors
            }
        }
    }


    public int calculateTotalVolunteerHours(String userId) {
        // Solidity 함수 호출을 위한 Function 객체 생성
        Function function = new Function(
                "calculateTotalVolunteerHours",
                Collections.singletonList(new Utf8String(userId)), // 사용자 ID
                Collections.singletonList(new TypeReference<Uint256>() {}) // 반환값: 총 봉사 시간
        );

        // 함수 호출 및 결과 디코딩
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = null;
        try {
            response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, remixIDEProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                    remixIDEProperties.getContractKey(),
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

    public ResponseEntity<CertificateDTO> getCertificateByUserId(String userId) {
        try {
            // Solidity 함수 정의
            Function function = new Function(
                    "getCertificateByUserId",
                    Collections.singletonList(new Utf8String(userId)), // 입력값: userId
                    Arrays.asList(
                            new TypeReference<Utf8String>() {}, // certificateId
                            new TypeReference<Utf8String>() {}, // userId
                            new TypeReference<Utf8String>() {}, // username
                            new TypeReference<Uint256>() {},   // totalHours
                            new TypeReference<Utf8String>() {}  // issueDate
                    )
            );

            // Solidity 함수 호출
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, remixIDEProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            // 응답 확인
            if (response.hasError()) {
                throw new RuntimeException("Error fetching certificate: " + response.getError().getMessage());
            }

            // 결과 디코딩
            List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            if (decoded.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 디코딩된 값 매핑
            String certificateId = decoded.get(0).getValue().toString();
            String decodedUserId = decoded.get(1).getValue().toString();
            String username = decoded.get(2).getValue().toString();
            BigInteger totalHours = (BigInteger) decoded.get(3).getValue();
            String issueDate = decoded.get(4).getValue().toString();

            // CertificateDTO 생성 및 반환
            CertificateDTO dto = CertificateDTO.builder()
                    .certificateId(certificateId)
                    .userId(decodedUserId)
                    .username(username)
                    .totalHours(totalHours.intValue()) // BigInteger -> int 변환
                    .issueDate(issueDate)
                    .build();

            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            // 예외 처리
            throw new NoCertificateUserException("자격증이 존재하지 않는 유저입니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
