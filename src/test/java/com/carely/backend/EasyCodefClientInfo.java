package com.carely.backend;
/**
 * <pre>
 * io.codef.easycodef
 *   |  EasyCodefClientInfo.java
 * </pre>
 *
 * Desc : EasyCodef 발급 클라이언트 정보 설정 클래스
 * @Company : ©CODEF corp.
 * @Author  : notfound404@codef.io
 * @Date    : Jun 26, 2020 3:42:11 PM
 * @Version : 1.0.1
 */
public class EasyCodefClientInfo {

    /**
     * TODO :	사용자는 코드에프 가입을 통해 발급 받은 클라이언트 정보와 RSA 공개키 정보를 설정해야 함.
     * 			설정하지 않은 상태에서는 SANDBOX 테스트만 사용 가능.
     */
    public static final String DEMO_CLIENT_ID = "09249b56-fd6b-4392-83e9-4a85c53649f9";
    public static final String DEMO_CLIENT_SECRET = "5e35b244-9fcc-4b99-a0ba-26d762cd37d8";

    /**  임시로 설정된 PUBLIC_KEY를 제거하고 코드에프 가입을 통해 발급 받은 본인 계정의 RSA 공개키 정보 설정 필요. */
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhlfj2lXaCwOf9rSjEk+K8zQ0tCarc3Q2U/XzelrXynlg6S8oG7Juby4Y+xENX/JkgliPKU9vady+IUVCvXMESXRmJ5C7uey9zrHTJMmpArD0BbEg81HNStE+AdxKn7xqSu0K3+tg6RKN2ijvXtugjfxhJBok7vVHsMxfk7UTOVLNgzXEpHtploF/7ppvsGPHbuJYGceJqGKVJDPOxB+erOVVQx5/35eAALAcKasSBFFzIszJHZCcmTYEGrckD7JxrYWzR91oHkwLBAo+qHRRKfovGxCWqLRcMr4AyrBRXDK/Fi/4F5ARf+w/Qf4I4XxkQlqewuBRVEPaHO4y16PPTQIDAQAB";

}