package com.carely.backend.jwt;

import com.carely.backend.code.ErrorCode;
import com.carely.backend.code.SuccessCode;
import com.carely.backend.domain.RefreshEntity;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.repository.RefreshRepository;
import com.carely.backend.util.TokenErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRedisRepository;
  
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // path and method 체크
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        ErrorCode errorCode = null;

        // refresh token
        String refreshToken = request.getHeader("refresh");
        if (refreshToken == null) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getType(refreshToken);
        if (!category.equals("refreshToken")) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // DB에 저장되어 있는지 확인
        Optional<RefreshEntity> isExist = refreshRedisRepository.findById(refreshToken);
        if (isExist.isEmpty()) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_NOT_FOUND);
        }

        //로그아웃 진행
        //Refresh 토큰 redis에서 제거
        refreshRedisRepository.deleteById(refreshToken);

        // response
        ResponseDTO responseDTO = new ResponseDTO(SuccessCode.SUCCESS_LOGOUT, null);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }

}

