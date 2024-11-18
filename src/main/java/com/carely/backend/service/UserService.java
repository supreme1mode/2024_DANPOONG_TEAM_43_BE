package com.carely.backend.service;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.user.MapUserDTO;
import com.carely.backend.dto.user.MyPageDTO;
import com.carely.backend.dto.user.RegisterDTO;
import com.carely.backend.dto.user.UserResponseDTO;
import com.carely.backend.exception.DuplicateUsernameException;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.service.kakao.KakaoAddressService;
import com.carely.backend.service.parser.AddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public MyPageDTO.DetailRes getMypage(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return MyPageDTO.DetailRes.toDTO(user);
    }

    public MyPageDTO.DetailRes getDetailUserInfo(Long userId, String kakaoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return MyPageDTO.DetailRes.toDTO(user);
    }

    public List<MapUserDTO> findUserByAddress(String city) {
        // 도시별로 필터링, 데이터가 없으면 빈 리스트 반환
        List<User> users = userRepository.findByCity(city);

        return users.stream()
                .map(user -> new MapUserDTO().toDTO(user))
                .collect(Collectors.toList()); // 빈 리스트도 collect로 반환
    }



    @Transactional(readOnly = true)
    public List<MapUserDTO> findUsersByCityAndUserTypes(String city, List<UserType> userTypes) {
        List<User> users = userRepository.findByCityAndUserTypeIn(city, userTypes);

        return users.stream()
                .map(user -> new MapUserDTO().toDTO(user))
                .collect(Collectors.toList()); // 빈 리스트도 collect로 반환
    }

}
