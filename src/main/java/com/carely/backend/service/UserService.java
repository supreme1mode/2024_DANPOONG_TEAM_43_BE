package com.carely.backend.service;

import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.user.*;
import com.carely.backend.exception.DuplicateUsernameException;
import com.carely.backend.exception.UserNotFoundException;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import com.carely.backend.service.kakao.KakaoAddressService;
import com.carely.backend.service.parser.AddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    // private final KakaoAddressService kakaoAddressService;

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
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return MyPageDTO.DetailRes.toDTO(user);
    }

    public MyPageDTO.DetailRes getDetailUserInfo(Long userId, String kakaoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        User viewer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 함께한 사람 추가
        MyPageDTO.DetailRes res = MyPageDTO.DetailRes.toDTO(user);
        res.setTogetherTime(calculateTotalDuration(viewer, user));
        return res;
    }

    public UserResponseDTO.Verification verifyAuthentication(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return UserResponseDTO.Verification.toDTO(user);
    }

    public UserResponseDTO.VerificationAddress verifyAuthenticationPost(String kakaoId, AddressDTO addressDTO) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String address = addressDTO.getAddress();
        String detailAddress = addressDTO.getDetailAddress();

        KakaoAddressService kakaoAddressService = new KakaoAddressService();
        String jsonResponse = kakaoAddressService.getAddressDetails(address);
        String[] addressDetails = AddressParser.parseAddress(jsonResponse);

        String city = addressDetails[1]; // 시/군 정보

        user.updateUserAddress(address, detailAddress, city);


        userRepository.save(user);

        return UserResponseDTO.VerificationAddress.toDTO(user);
    }

    public Long getUserId(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    private Long calculateTotalDuration(User user1, User user2) {
        List<Volunteer> sharedVolunteers = volunteerRepository.findByVolunteerAndCaregiver(user1, user2);

        return sharedVolunteers.stream()
                .mapToLong(volunteer -> {
                    if (volunteer.getStartTime() != null && volunteer.getEndTime() != null &&
                            volunteer.getEndTime().isBefore(LocalDateTime.now())) { // 종료 시간이 현재 시간보다 이전인 경우
                        return volunteer.getDurationHours(); // 함께한 시간에 추가
                    }
                    return 0;
                })
                .sum();
    }

    @Transactional(readOnly = true)
    public List<MapUserDTO> findUsersByCityAndOptionalUserTypes(String city, List<UserType> userTypes, String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        KakaoAddressService kakaoAddressService = new KakaoAddressService();
        Map<String, Double> userAddress = kakaoAddressService.getLocationFromAddress(user.getAddress());
        Double latitude = userAddress.get("latitude");
        Double longtitude = userAddress.get("longtitude");

        if (userTypes == null || userTypes.isEmpty() || userTypes.contains(UserType.ALL)) {
            // userType이 없거나 'ALL' 포함된 경우
            return findUserByAddress(city, user, latitude, longtitude);
        } else {
            // userType이 명시되어 있는 경우
            return findUsersByCityAndUserTypes(city, userTypes, user, latitude, longtitude);
        }
    }

    public List<MapUserDTO> findUsersByCityAndUserTypes(String city, List<UserType> userTypes, User superUser, Double latitude, Double longittude) {
        List<User> users = userRepository.findByCityAndUserTypeIn(city, userTypes);

        return users.stream()
                .map(user -> new MapUserDTO().toDTO(user, calculateTotalDuration(user, superUser), latitude, longittude))
                .collect(Collectors.toList()); // 빈 리스트도 collect로 반환
    }

    public List<MapUserDTO> findUserByAddress(String city, User superUser, Double latitude, Double longittude) {
        List<User> users = userRepository.findByCity(city);

        return users.stream()
                .map(user -> new MapUserDTO().toDTO(user, calculateTotalDuration(user, superUser),  latitude, longittude))
                .collect(Collectors.toList()); // 빈 리스트도 collect로 반환
    }

}
