package com.carely.backend.service.certificate;

import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.exception.*;
import com.carely.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

@Service
@Slf4j
@RedisHash("VolunteerSession")
public class CertificateService {
    protected final Web3j web3j;

    private final UserRepository userRepository;
    protected final RawTransactionManager txManager;
    protected final StaticGasProvider gasProvider;

    private final GanacheProperties ganacheProperties;
    private String ganacheUrl = "http://3.34.24.211:7545";

    @Autowired
    public CertificateService(UserRepository userRepository, GanacheProperties ganacheProperties) {
        this.userRepository = userRepository;
        this.ganacheProperties = ganacheProperties;
        String privateKey = ganacheProperties.getPrivateKey().trim(); // 공백 제거
        String contractKey = ganacheProperties.getContractKey().trim(); // 공백 제거

        log.info("Loaded Contract Key: {}", contractKey);
        log.info("Loaded Private Key: {}", privateKey);

        // Web3j 초기화
        this.web3j = Web3j.build(new HttpService(ganacheUrl));

        // Credentials 초기화
        if (!Numeric.containsHexPrefix(privateKey)) {
            privateKey = "0x" + privateKey; // 0x 접두어 추가
        }

        Credentials credentials = Credentials.create(privateKey);
        this.txManager = new RawTransactionManager(web3j, credentials);
        this.gasProvider = new StaticGasProvider(
                new BigInteger("1000000000"), // 가스 가격 (1 Gwei)
                new BigInteger("3000000")    // 가스 한도
        );
    }

    @Transactional
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

        String transactionResult = null;
        transactionResult = sendTransaction(function);
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
                    Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 디코딩
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        System.out.println(decoded.get(0));


        // 각각의 배열 추출
        DynamicArray<Utf8String> userIds = (DynamicArray<Utf8String>) decoded.get(0);
        DynamicArray<Utf8String> usernames = (DynamicArray<Utf8String>) decoded.get(1);
        DynamicArray<Uint256> volunteerHours = (DynamicArray<Uint256>) decoded.get(2);
        DynamicArray<Utf8String> dates = (DynamicArray<Utf8String>) decoded.get(3);
        DynamicArray<Utf8String> volunteerTypes = (DynamicArray<Utf8String>) decoded.get(4);
        DynamicArray<Address> userAddresses = (DynamicArray<Address>) decoded.get(5);

        System.out.println(userIds.getValue());
        if (userIds.getValue().isEmpty()) {
            throw new ListEmptyException("비었음");
        }
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

    public CertificateDTO issueCertificate(String certificateId, String userId) throws Exception {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();

        if (checkIfCertificateExists(userId)) {
            throw new AlreadyHasCertificateException("이미 존재함...");
        }

        if (calculateTotalVolunteerHours(userId) < 80) {
            throw new TotalTimeNotEnoughException("시간 부족");
        }

        Function issueFunction = new Function(
                "issueCertificate",
                Arrays.asList(
                        new Utf8String(certificateId),
                        new Utf8String(userId),
                        new Utf8String(user.getUsername()), // Replace with actual username
                        new Utf8String(now.toString()) // Replace with actual issueDate
                ),
                Collections.emptyList()
        );

        // 트랜잭션 실행
        String transactionResult = sendTransaction(issueFunction);
        System.out.println("Transaction Hash: " + transactionResult);

        // 트랜잭션 상태 확인
        TransactionReceipt receipt = waitForTransactionReceipt(transactionResult);
        if (receipt.getStatus().equals("0x0")) {
            throw new RuntimeException("Transaction failed.");
        }

        // 트랜잭션 후 데이터 조회
        Function getCertificateFunction = new Function(
                "getCertificateByUserId",
                Arrays.asList(new Utf8String(userId)),
                Arrays.asList(
                        new TypeReference<Utf8String>() {}, // certificateId
                        new TypeReference<Utf8String>() {}, // username
                        new TypeReference<Uint256>() {},   // totalHours
                        new TypeReference<Utf8String>() {}  // issueDate
                )
        );

        String encodedGetFunction = FunctionEncoder.encode(getCertificateFunction);
        EthCall getResponse = web3j.ethCall(
                Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedGetFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        List<Type> getDecoded = FunctionReturnDecoder.decode(getResponse.getValue(), getCertificateFunction.getOutputParameters());
        if (getDecoded.isEmpty()) {
            throw new RuntimeException("Failed to retrieve certificate details.");
        }

        // 반환 데이터 매핑
        return CertificateDTO.builder()
                .certificateId(getDecoded.get(0).getValue().toString())
                .username(getDecoded.get(1).getValue().toString())
                .totalHours(((BigInteger) getDecoded.get(2).getValue()).intValue())
                .issueDate(getDecoded.get(3).getValue().toString())
                .build();
    }


    public boolean checkIfCertificateExists(String userId) throws IOException {
        // Solidity 함수 호출을 위한 Function 객체 생성
        Function function = new Function(
                "hasUserCertificate",
                Collections.singletonList(new Utf8String(userId)), // 사용자 ID
                Collections.singletonList(new TypeReference<Bool>() {
                }) // 반환값: Boolean
        );

        // 함수 호출 및 결과 디코딩
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = null;
        response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if (decoded.isEmpty() || !(decoded.get(0) instanceof Bool)) {
            throw new RuntimeException("Unexpected response type or empty response");
        }

// 디코드된 값 확인
        Bool certificateStatus = (Bool) decoded.get(0);
        System.out.println("Certificate status: " + certificateStatus.getValue());
        return certificateStatus.getValue();

    }


    public CertificateDTO getCertificateById(String certificateId) throws Exception {
        // Define the Solidity function call
        Function function = new Function(
                "getCertificateById",
                Collections.singletonList(new Utf8String(certificateId)), // Input parameter
                Arrays.asList(
                        new TypeReference<Utf8String>() {
                        }, // Certificate ID
                        new TypeReference<Utf8String>() {
                        }, // User ID
                        new TypeReference<Utf8String>() {
                        }, // Username
                        new TypeReference<Uint256>() {
                        },   // Total Hours
                        new TypeReference<Utf8String>() {
                        }  // Issue Date
                )
        );

        // Encode the function for the smart contract call
        String encodedFunction = FunctionEncoder.encode(function);

        try {
            // Make the call to the contract
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

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
            return CertificateDTO.builder()
                    .certificateId(decodedCertificateId)
                    .userId(decodedUserId)
                    .username(decodedUsername)
                    .totalHours(decodedTotalHours.intValue()) // Convert BigInteger to int
                    .issueDate(decodedIssueDate)
                    .build();

        } catch (RuntimeException e) {
            throw new NoCertificateUserException("존재하지 않음");
        }
    }


    public int calculateTotalVolunteerHours(String userId) {
        // Solidity 함수 호출을 위한 Function 객체 생성
        Function function = new Function(
                "calculateTotalVolunteerHours",
                Collections.singletonList(new Utf8String(userId)), // 사용자 ID
                Collections.singletonList(new TypeReference<Uint256>() {
                }) // 반환값: 총 봉사 시간
        );

        // 함수 호출 및 결과 디코딩
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = null;
        try {
            response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
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


    @Transactional
    public List<VolunteerListDTO> getVolunteerSessionsByUserIdAndType(String volunteerType, String userId) {
        User user_volunteer = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 유효한 volunteerType인지 확인
        UserType userType = Arrays.stream(UserType.values())
                .filter(type -> type.name().equalsIgnoreCase(volunteerType))
                .findFirst()
                .orElseThrow(() -> new NotValidUserTypeException("Invalid volunteer type: " + volunteerType));

        System.out.println(userType.name() + userId);
        // Solidity 함수 정의
        Function function = new Function(
                "getVolunteerSessionsByUserIdAndType",
                Arrays.asList(new Utf8String(userId), new Utf8String(userType.name())),
                List.of(
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },  // userIds
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },  // usernames
                        new TypeReference<DynamicArray<Uint256>>() {
                        },     // volunteerHours
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },  // dates
                        new TypeReference<DynamicArray<Address>>() {
                        }      // userAddresses
                )
        );

        // Solidity 함수 호출
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response;
        try {
            response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching volunteer sessions by userId and type", e);
        }

        log.info("Encoded Function: {}", encodedFunction);
        log.info("Response: {}", response.getValue());

//
//        if (response.hasError()) {
//            throw new RuntimeException("Error in Solidity call: " + response.getError().getMessage());
//        }

        // 결과 디코딩
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if (decoded.isEmpty()) {
            throw new ListEmptyException("Decoded result is empty.");
        }
        log.info("Decoded Result: {}", decoded);

        // 디코딩된 각 배열
        DynamicArray<Utf8String> userIds = (DynamicArray<Utf8String>) decoded.get(0);
        DynamicArray<Utf8String> usernames = (DynamicArray<Utf8String>) decoded.get(1);
        DynamicArray<Uint256> volunteerHours = (DynamicArray<Uint256>) decoded.get(2);
        DynamicArray<Utf8String> dates = (DynamicArray<Utf8String>) decoded.get(3);
        DynamicArray<Address> userAddresses = (DynamicArray<Address>) decoded.get(4);

//        System.out.println(userIds.getValue());
//        if (userIds.getValue().isEmpty()) {
//            throw new ListEmptyException("비었음");
//        }

        // 배열 크기 확인
        if (userIds.getValue().size() != usernames.getValue().size()
                || usernames.getValue().size() != volunteerHours.getValue().size()
                || volunteerHours.getValue().size() != dates.getValue().size()
                || dates.getValue().size() != userAddresses.getValue().size()) {
            throw new RuntimeException("Mismatched array sizes in Solidity response");
        }

        // 결과 매핑
        List<VolunteerListDTO> sessions = new ArrayList<>();
        for (int i = 0; i < userIds.getValue().size(); i++) {
            sessions.add(VolunteerListDTO.builder()
                    .userId(userIds.getValue().get(i).getValue())
                    .username(usernames.getValue().get(i).getValue())
                    .volunteerHours(volunteerHours.getValue().get(i).getValue().intValue())
                    .date(dates.getValue().get(i).getValue())
                    .volunteerType(userType.name())
                    .userAddress(userAddresses.getValue().get(i).getValue())
                    .build());
        }

        return sessions;
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


    @Transactional
    public CertificateDTO getCertificateByUserId(String userId) {
        User user_volunteer = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // Solidity 함수 정의
        Function function = new Function(
                "getCertificateByUserId",
                Collections.singletonList(new Utf8String(userId)), // 입력값
                Arrays.asList(
                        new TypeReference<Utf8String>() {}, // certificateId
                        new TypeReference<Utf8String>() {}, // username
                        new TypeReference<Uint256>() {},   // totalHours
                        new TypeReference<Utf8String>() {}  // issueDate
                )
        );

        // 함수 호출을 인코딩
        String encodedFunction = FunctionEncoder.encode(function);

        try {
            // 스마트 컨트랙트 호출
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, ganacheProperties.getContractKey(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            // 응답에 에러가 있는지 확인
            if (response.hasError()) {
                throw new RuntimeException("Error fetching certificate: " + response.getError().getMessage());
            }

            // 응답 디코딩
            List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            if (decoded.isEmpty()) {
                throw new NoCertificateUserException("No certificate found for userId: " + userId);
            }

            // 디코딩된 값을 변수에 매핑
            String decodedCertificateId = decoded.get(0).getValue().toString();
            String decodedUsername = decoded.get(1).getValue().toString();
            BigInteger decodedTotalHours = (BigInteger) decoded.get(2).getValue();
            String decodedIssueDate = decoded.get(3).getValue().toString();

            // DTO로 반환
            return CertificateDTO.builder()
                    .certificateId(decodedCertificateId)
                    .userId(userId)
                    .username(decodedUsername)
                    .totalHours(decodedTotalHours.intValue()) // BigInteger -> int 변환
                    .issueDate(decodedIssueDate)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Error calling smart contract function", e);
        }
    }

    public TransactionReceipt waitForTransactionReceipt(String transactionHash) throws Exception {
        int attempts = 40; // 최대 시도 횟수
        int sleepDuration = 1500; // 각 시도 사이 대기 시간 (밀리초)

        for (int i = 0; i < attempts; i++) {
            // 트랜잭션 상태를 조회
            var receiptResponse = web3j.ethGetTransactionReceipt(transactionHash).send();
            if (receiptResponse.getTransactionReceipt().isPresent()) {
                // 트랜잭션이 처리되었으면 결과 반환
                return receiptResponse.getTransactionReceipt().get();
            }

            // 트랜잭션이 처리되지 않았으면 대기
            Thread.sleep(sleepDuration);
        }

        // 최대 시도 횟수를 초과한 경우 예외 처리
        throw new RuntimeException("Transaction receipt was not generated after " + attempts + " attempts");
    }

    @Transactional
    public void determineVolunteerType(Volunteer volunteer) throws Exception {
        String userType = null;

        // 자원봉사자면 그냥 자원봉사
        if (volunteer.getVolunteer().getUserType().equals(UserType.VOLUNTEER)) {
            userType = UserType.VOLUNTEER.name();

            //만약에 요양보호사이면서, 자격증 검사도 받았으면.
        } else if (volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER) && (volunteer.getVolunteer().getCertificateCheck())) {
            userType = UserType.CARE_WORKER.name();
        }

        // 만약에 요양보호사인데 자격증 검증이 아직이라면...
        else if (volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER) && (!volunteer.getVolunteer().getCertificateCheck())) {
            userType = UserType.VOLUNTEER.name();
        }

        createVolunteerSession(volunteerDTO.builder()
                .userId(volunteer.getVolunteer().getId().toString())
                .username(volunteer.getVolunteer().getUsername())
                .date(volunteer.getDate().toString())
                .volunteerHours(volunteer.getDurationHours())
                .volunteerType(userType)
                .build());
    }


}

