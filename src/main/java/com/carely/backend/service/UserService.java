package com.carely.backend.service;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.user.RegisterDTO;
import com.carely.backend.exception.DuplicateUsernameException;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.service.kakao.KakaoAddressService;
import com.carely.backend.service.parser.AddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public RegisterDTO.Res register(RegisterDTO registerDTO, MultipartFile image) {
        String username = registerDTO.getUsername();
        UserType userType = registerDTO.getUserType();
        String kakaoId = registerDTO.getKakaoId();
        String address = registerDTO.getAddress();

        // 카카오 id 중복 검사
        if(userRepository.existsByKakaoId(kakaoId))
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");

        // 카카오 API 호출 및 주소 파싱

        KakaoAddressService kakaoAddressService = new KakaoAddressService();
        String jsonResponse = kakaoAddressService.getAddressDetails(address);
        String[] addressDetails = AddressParser.parseAddress(jsonResponse);

        String city = addressDetails[1]; // 시/군 정보

        // 중복 검사
        if(userRepository.existsByKakaoId(registerDTO.getKakaoId())) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        // user 생성
        User user = User.builder()
                .kakaoId(kakaoId)
                .city(city)
                .username(username)
                .userType(userType)
                .age(registerDTO.getAge())
                .phoneNum(registerDTO.getPhoneNum())
                .address(address)
                .detailAddress(registerDTO.getDetailAddress())
                .locationAuthentication(false) // 기본적으로 위치 인증 안됨
                .talk(registerDTO.getTalk())
                .eat(registerDTO.getEat())
                .toilet(registerDTO.getToilet())
                .bath(registerDTO.getBath())
                .walk(registerDTO.getWalk())
                .story(registerDTO.getStory())
                .shareLocation(registerDTO.getShareLocation())
                .role("ROLE_USER")
                .build();

        return RegisterDTO.Res.toDTO(userRepository.save(user));
    }
}
