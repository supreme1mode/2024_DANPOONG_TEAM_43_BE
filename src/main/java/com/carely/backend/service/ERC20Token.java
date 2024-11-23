package com.carely.backend.service;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class ERC20Token {
    private Web3j web3j;
    private String contractAddress;
    private Credentials credentials;

    public ERC20Token(Web3j web3j, String contractAddress, String privateKey) {
        this.web3j = web3j;
        this.contractAddress = contractAddress;
        this.credentials = Credentials.create(privateKey);
    }

    // balanceOf 함수 호출
    public BigInteger getBalance(String userAddress) throws Exception {
        Function balanceOfFunction = new Function(
                "balanceOf",
                Arrays.asList(new Address(userAddress)),
                Arrays.asList(new TypeReference<Uint256>() {}));

        String encodedFunction = FunctionEncoder.encode(balanceOfFunction);

        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(credentials.getAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        if (response.hasError()) {
            System.err.println("Error fetching balance: " + response.getError().getMessage());
            throw new RuntimeException("Error fetching balance: " + response.getError().getMessage());
        } else {
            // Process the balance
            List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), balanceOfFunction.getOutputParameters());
            return (BigInteger) decoded.get(0).getValue();
        }

    }
}
