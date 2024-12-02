package com.carely.backend.service.EasyCodef;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * io.codef.easycodef
 *   |_ EasyCodefProperties.java
 * </pre>
 *
 * Desc : 코드에프의 쉬운 사용을 위한 프로퍼티 클래스
 * @Company : ?CODEF corp.
 * @Author  : notfound404@codef.io
 * @Date    : Jun 26, 2020 3:36:51 PM
 */
@Getter
@Configuration
public class EasyCodefProperties {

    //	데모 엑세스 토큰 발급을 위한 클라이언트 아이디
    private String demoClientId 	= "09249b56-fd6b-4392-83e9-4a85c53649f9";

    //	데모 엑세스 토큰 발급을 위한 클라이언트 시크릿
    private String demoClientSecret 	= "5e35b244-9fcc-4b99-a0ba-26d762cd37d8";

    /**
     * -- SETTER --
     *  Desc : 데모 접속 토큰 설정
     *
     * @param demoAccessToken
     */
    //	OAUTH2.0 데모 토큰
    private String demoAccessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXJ2aWNlX3R5cGUiOiIxIiwic2NvcGUiOlsicmVhZCJdLCJzZXJ2aWNlX25vIjoiMDAwMDA0OTExMDAyIiwiZXhwIjoxNzMzNzMyNTExLCJhdXRob3JpdGllcyI6WyJJTlNVUkFOQ0UiLCJQVUJMSUMiLCJCQU5LIiwiRVRDIiwiU1RPQ0siLCJDQVJEIl0sImp0aSI6IjE1YzE1YzBiLWMyNmEtNDIyYi05MWFlLTE2ZjM0NWMwZjk4MSIsImNsaWVudF9pZCI6IjA5MjQ5YjU2LWZkNmItNDM5Mi04M2U5LTRhODVjNTM2NDlmOSJ9.NhWjygiPQrgMNPcii17sdRvt71_u4IWiHsaN8AgqfP5_rWXQJ6yAiL6zWITr_TmIgpPYcr94A1Q6MhDf_IgsrAIsJOadsFcqdEDneugG_0yDdNo88ZgxCJuNVktUKMy_-nJwJxO1Wg01zmt5zO5lqbZb_a1tvs9TfXb66aqWLW4a8Z2yXAOs0JC_T24woy0LbM3gyndX25JoMX9xGX7snnptqfSGpkCyRPPqUzH3ZcZbri30uh0qNK9VScL4N43pJF65Kwq0wS6b4t5Z4fhNOnmGpcZdlWEr9ZF081xttVugPcCafC2aWnPff3Tvc3aKUORrRKkYTDFIaH2MqHqY6g";

    /**
     * -- SETTER --
     *  Desc : RSA암호화를 위한 퍼블릭키 설정
     *
     * @param publicKey
     */
    //	RSA암호화를 위한 퍼블릭키
    @Setter
    private String publicKey 	= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnHDYTekdjHbJxv5YWrJmMFjDvlx3BiiUM/f7c28DbrFrGQF9Etns35lxSeAuiKi/C55Py23CZPG29MRB6alhXWOZjOAy5StaUNzT0M6U90lyy7ymvNdT1u0XFNQEzEGgQ+PBdBaAQIrLzow4Kg/HATB3lN3hJApddX5e3tuoOAOxXc4yXXjbKAHBOwSrPvu4ex7e4xfdZU2WDuwxUA3aGO8DavhteFnfbVxneJD+n8cuuhksp8jL2Q9u96+nIHMfGI9Stdvgyf3je2vkJGHCotZFL0vsVS54Qchq3+99Lf4a9mm9cImlabswLtSRNpYt6xNXd0L97jQxKhc0otmgrQIDAQAB";


    /**
     * Desc : 데모서버 사용을 위한 클라이언트 정보 설정
     * @Company : ?CODEF corp.
     * @Author  : notfound404@codef.io
     * @Date    : Jun 26, 2020 3:37:10 PM
     * @param demoClientId
     * @param demoClientSecret
     */
    public void setClientInfoForDemo(String demoClientId, String demoClientSecret) {
        this.demoClientId = demoClientId;
        this.demoClientSecret = demoClientSecret;
    }
}