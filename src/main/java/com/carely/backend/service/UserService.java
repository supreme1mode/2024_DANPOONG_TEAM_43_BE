package com.carely.backend.service;

import com.carely.backend.domain.GuestBookEntity;
import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.guestBook.UserDetailGuestBookDTO;
import com.carely.backend.dto.user.*;
import com.carely.backend.exception.*;
import com.carely.backend.repository.GroupRepository;
import com.carely.backend.repository.GuestBookRepository;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import com.carely.backend.service.kakao.KakaoAddressService;
import com.carely.backend.service.ocr.OCRService;
import com.carely.backend.service.parser.AddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final OCRService ocrService;
    private final GuestBookRepository guestBookRepository;

    public RegisterDTO.Res register(RegisterDTO registerDTO, MultipartFile file) throws IOException {

        String imageUrl = null;

        if (registerDTO.getUserType().equals(UserType.CARE_WORKER)) {
            if (file == null && registerDTO.getUserType().equals(UserType.CARE_WORKER)) {
                throw new NoFileException("파일 없는데");
            }

            imageUrl = ocrService.uploadCertificateImage(file, registerDTO.getKakaoId());

        }


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


        Map<String, Double> location = getLocation(address);
        // 위도, 경도 설정
        Double latitude = location.get("latitude");
        Double longitude = location.get("longitude");

        // user 생성
        User user = User.builder()
                .kakaoId(kakaoId)
                .city(city)
                .username(username)
                .userType(userType)
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
                .latitude(latitude)
                .age(getAge(registerDTO.getIdentity()))
                .longitude(longitude)
                .certificateCheck(false)
                .identity(registerDTO.getIdentity())
                .certificateImage(imageUrl)
                .build();

        return RegisterDTO.Res.toDTO(userRepository.save(user));
    }

    private static int getAge(String identify) {
        String birthDateStr = identify.substring(0, 6);
        int year = Integer.parseInt(birthDateStr.substring(0, 2));
        int month = Integer.parseInt(birthDateStr.substring(2, 4));
        int day = Integer.parseInt(birthDateStr.substring(4, 6));

        char genderCode = identify.charAt(6);
        if (genderCode == '1' || genderCode == '2') {
            year += 1900;
        }
        else {
            year += 2000;
        }

        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears() + 1; // 만나이 아님
    }

    private Map<String, Double> getLocation(String address) {
        KakaoAddressService kakaoAddressService = new KakaoAddressService();
        return kakaoAddressService.getLocationFromAddress(address);
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

        // 함께한 사람 방명록 추가
        List<Volunteer> result = volunteerRepository.findByVolunteerOrCaregiver(user);
        List<Volunteer> list = result.size() > 5 ? result.subList(0, 5) : result;

        if (list.isEmpty()) {
            res.setGuestbookDTOS(null);
        }
        else {
            res.setGuestbookDTOS(
                    list.stream()
                            .map(volunteer -> {
                                User partner;
                                // 내가 간병인이면
                                if (user.getUserType().equals(UserType.CAREGIVER)) {
                                    partner = volunteer.getVolunteer();
                                } else {
                                    partner = volunteer.getCaregiver();
                                }

                                GuestBookEntity guestBook = guestBookRepository.findByVolunteerSectionIdAndWriterId(volunteer.getId(), partner.getId());

                                // guestBook이 null인 경우 null 반환 (Stream에서 제외될 수 있음)
                                if (guestBook == null) {
                                    return null;
                                }

                                // guestBook이 있는 경우 DTO 생성
                                return UserDetailGuestBookDTO.builder()
                                        .partnerUsername(partner.getUsername())
                                        .partnerUserId(partner.getId())
                                        .partnerUserType(partner.getUserType().name())
                                        .content(guestBook.getContent())
                                        .date(volunteer.getDate().toString())
                                        .build();
                            })
                            .filter(Objects::nonNull) // null 제거
                            .collect(Collectors.toList())
            );

        }
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

        // 시연을 위해 city 업데이트 안 함
        user.updateUserAddress(address, detailAddress);


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

    public List<MapUserDTO> findUsersByCityAndUserTypes(String city, List<UserType> userTypes, User currentUser, Double latitude, Double longittude) {
        List<User> users = userRepository.findByCityAndUserTypeIn(city, userTypes);

        return users.stream()
                .map(user -> new MapUserDTO().toDTO(user, calculateTotalDuration(user, currentUser), currentUser))
                .collect(Collectors.toList()); // 빈 리스트도 collect로 반환
    }

    public List<MapUserDTO> findUserByAddress(String city, User currentUser, Double latitude, Double longittude) {
        List<User> users = userRepository.findByCity(city);

        return users.stream()
                .map(user -> new MapUserDTO().toDTO(user, calculateTotalDuration(user, currentUser), currentUser))
                .collect(Collectors.toList()); // 빈 리스트도 collect로 반환
    }


    @Transactional
    public void deleteUser(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    public List<MapUserDTO> findAllUsers(String kakaoId) {
        User viewer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> !user.getKakaoId().equals(kakaoId))  // 본인 제외
                .map(user -> new MapUserDTO().toDTO(user, calculateTotalDuration(user, viewer), viewer))
                .collect(Collectors.toList());  // 빈 리스트도 collect로 반환
    }


    public List<MapUserDTO> findAllUsersByCityAndUserTypes(List<UserType> userTypes, String kakaoId) {
        User viewer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<User> users = userRepository.findByUserTypeIn(userTypes);

        return users.stream()
                .filter(user -> !user.getKakaoId().equals(kakaoId))  // 본인 제외
                .map(user -> new MapUserDTO().toDTO(user, calculateTotalDuration(user, viewer), viewer))
                .collect(Collectors.toList());  // 빈 리스트도 collect로 반환
    }

    // 간병인 리스트 추천
    public List<RecommandUserDTO.Res> recommendCaregivers(User currentUser) {

        // 같은 city에 있는 간병인만 조회
        String currentCity = currentUser.getCity();
        System.out.println(currentCity);
        // 같은 city에 있는 모든 사용자 조회
        List<User> allCaregivers = userRepository.findByUserTypeAndCity(UserType.CAREGIVER, currentCity);
        System.out.println(allCaregivers);
        System.out.println(currentUser.getAddress());

        // 본인 제외, 보완 가능한 trait 필터링
        List<User> filteredCaregivers = allCaregivers.stream()
                .filter(user -> !user.getId().equals(currentUser.getId())) // 본인 제외
                .filter(user -> hasComplementaryTraits(currentUser, user)) // 부족한 요소 보완 가능
                .collect(Collectors.toList());

        List<User> selectedCaregivers;

        if (filteredCaregivers.size() > 10) {
            // 추천 대상이 많으면 10명을 랜덤으로 선택
            Collections.shuffle(filteredCaregivers);
            selectedCaregivers = filteredCaregivers.subList(0, 10);
        } else if (!filteredCaregivers.isEmpty()) {
            // 추천 대상이 적으면 그대로 반환
            selectedCaregivers = filteredCaregivers;
        } else {
            // 추천 대상이 없으면 도움 될 만한 간병인 10명 추천
            selectedCaregivers = allCaregivers.stream()
                    .filter(user -> !user.getId().equals(currentUser.getId())) // 본인 제외
                    .sorted(Comparator.comparingInt(user -> calculateHelpfulness(currentUser, user))) // 도움 되는 순으로 정렬
                    .limit(10)
                    .collect(Collectors.toList());
        }
        return selectedCaregivers.stream()
                .map(user -> {
                    Long totalDuration = calculateTotalDuration(user, currentUser);
                    return RecommandUserDTO.Res.toDTO(user, totalDuration, currentUser);
                })
                .collect(Collectors.toList());
    }

    // 자원봉사 리스트 조회
// 자원봉사 리스트 조회
    public List<RecommandUserDTO.Res> recommendUsers(User currentUser) {

        // 현재 사용자의 city 값 조회
        String currentCity = currentUser.getCity();

        // 같은 city에 있는 모든 사용자 조회
        List<User> allUsers = userRepository.findByCity(currentCity);

        // 본인 제외, CAREGIVER 제외, 보완 가능한 trait 필터링
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> !user.getId().equals(currentUser.getId())) // 본인 제외
                .filter(user -> !user.getUserType().equals(UserType.CAREGIVER)) // 간병인은 제외
                .filter(user -> hasComplementaryTraits(currentUser, user)) // 부족한 요소 보완 가능
                .collect(Collectors.toList());

        List<User> selectedUsers;

        if (filteredUsers.size() > 10) {
            // 추천 대상이 많으면 10명을 랜덤으로 선택
            Collections.shuffle(filteredUsers);
            selectedUsers = filteredUsers.subList(0, 10);
        } else if (!filteredUsers.isEmpty()) {
            // 추천 대상이 적으면 그대로 반환
            selectedUsers = filteredUsers;
        } else {
            // 추천 대상이 없으면 도움 될 만한 유저 10명 추천
            selectedUsers = allUsers.stream()
                    .filter(user -> !user.getId().equals(currentUser.getId())) // 본인 제외
                    .filter(user -> !user.getUserType().equals(UserType.CAREGIVER)) // 간병인은 제외
                    .sorted(Comparator.comparingInt(user -> calculateHelpfulness(currentUser, user))) // 도움 되는 순으로 정렬
                    .limit(10)
                    .collect(Collectors.toList());
        }

        System.out.println(selectedUsers);

        return selectedUsers.stream()
                .map(user -> {
                    Long totalDuration = calculateTotalDuration(user, currentUser);
                    return RecommandUserDTO.Res.toDTO(user, totalDuration, currentUser);
                })
                .collect(Collectors.toList());
    }


    // 부족한 요소를 보완할 수 있는지 확인
    private boolean hasComplementaryTraits(User currentUser, User otherUser) {
        return isComplementary(currentUser.getTalk(), otherUser.getTalk()) &&
                isComplementary(currentUser.getEat(), otherUser.getEat()) &&
                isComplementary(currentUser.getToilet(), otherUser.getToilet()) &&
                isComplementary(currentUser.getBath(), otherUser.getBath()) &&
                isComplementary(currentUser.getWalk(), otherUser.getWalk());
    }

    // 두 요소를 비교하여 보완 가능 여부 확인
    private boolean isComplementary(String currentLevel, String otherLevel) {
        int currentValue = mapToInt(currentLevel);
        int otherValue = mapToInt(otherLevel);

        return currentValue < otherValue; // 상대방이 높은 능력을 가졌는지 확인
    }

    // 수준 문자열을 정수로 매핑
    private int mapToInt(String level) {
        switch (level) {
            case "서투름":
            case "하급":
                return 0;
            case "보통":
            case "중급":
                return 1;
            case "수월":
            case "상급":
                return 2;
            default:
                return -1; // 예상치 못한 값 처리
        }
    }

    // 사용자 간 도움 정도 계산
    private int calculateHelpfulness(User currentUser, User otherUser) {
        return mapToInt(otherUser.getTalk()) +
                mapToInt(otherUser.getEat()) +
                mapToInt(otherUser.getToilet()) +
                mapToInt(otherUser.getBath()) +
                mapToInt(otherUser.getWalk());
    }

    public User findUserByKakaoId(String kakaoId) {
        Optional<User> user = userRepository.findByKakaoId(kakaoId);
        if (user.isPresent()) {
            return user.get();
        }

        throw new KakaoIdNotFoundException("Kakao ID가 존재하지 않습니다: " + kakaoId);
    }
}
