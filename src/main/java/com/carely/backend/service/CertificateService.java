package com.carely.backend.service;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
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
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
@Slf4j
public class CertificateService {
    protected final Web3j web3j;  // Web3j 인스턴스를 주입받음
    protected final String contractAddress = "0x4E7607e5a885e81f7e5cF7505067E5EcbFf4ade0"; // 배포된 스마트 컨트랙트 주소
    protected final String walletAddress = "0x398D69E7687C9d1B857DE1e4882295aadd98C952"; // 트랜잭션을 실행할 계정 주소
    protected final RawTransactionManager txManager;
    protected final StaticGasProvider gasProvider;


    @Autowired
    public CertificateService(Web3j web3j) {
        this.web3j = web3j;
        Credentials credentials = Credentials.create("0x130a3322bdcacb9bb8be47a2da848fe5affb732a8b783af5cbb1dad895d46cb6"); // walletAddress의 프라이빗 키
        this.txManager = new RawTransactionManager(web3j, credentials); // RawTransactionManager 사용
        this.gasProvider = new StaticGasProvider(
                new BigInteger("20000000000"), // 적절한 가스 가격
                new BigInteger("6000000")      // Ganache의 블록 가스 한도보다 낮은 값으로 설정
        );
    }

    public String createVolunteerSession(Volunteer volunteer) throws Exception {
        String volunteerType = null;

        // Volunteer Type 설정
        if (volunteer.getVolunteer().getUserType().equals(UserType.VOLUNTEER)) {
            volunteerType = UserType.VOLUNTEER.name();
        } else if (volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER) && !volunteer.getVolunteer().getCertificateCheck()) {
            volunteerType = UserType.VOLUNTEER.name();
        } else if (volunteer.getVolunteer().getUserType().equals(UserType.CARE_WORKER)) {
            volunteerType = UserType.CARE_WORKER.name();
        }

        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                "createVolunteerSession",
                Arrays.asList(
                        new Utf8String(volunteer.getVolunteer().getId().toString()),
                        new Utf8String(volunteer.getVolunteer().getUsername()),
                        new Uint256(BigInteger.valueOf(volunteer.getDurationHours())),
                        new Utf8String(volunteer.getDate().toString()),
                        new Utf8String(volunteerType)
                ),
                Collections.emptyList()
        );
        return sendTransaction(function);
    }


    protected String sendTransaction(Function function) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);
        EthSendTransaction transactionResponse = txManager.sendTransaction(
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                contractAddress,
                encodedFunction,
                BigInteger.ZERO
        );

        if (transactionResponse.hasError()) {
            return "Transaction failed: " + transactionResponse.getError().getMessage();
        }

        return "Transaction successful, hash: " + transactionResponse.getTransactionHash();
    }

}
