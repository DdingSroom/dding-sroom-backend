package com.example.ddingsroom.user.controller;

import com.example.ddingsroom.user.docs.UserApiDocs;
import com.example.ddingsroom.user.dto.*;
import com.example.ddingsroom.user.service.JoinService;
import com.example.ddingsroom.user.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/user", produces="application/json; charset=utf8")
public class JoinController implements UserApiDocs {
    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @Override
    @PostMapping("/modify-password")
    public ResponseEntity<ResponseDTO> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return joinService.modifyPassword(modifyPasswordDTO);
    }

    @Override
    @PostMapping("/code-send")
    public ResponseEntity<ResponseDTO> codeSend(@RequestBody CodeSendDTO codeSendDTO) {
        return joinService.autentication1(codeSendDTO);
    }

    @Override
    @PostMapping("/code-verify")
    public ResponseEntity<ResponseDTO> codeVerify(@RequestBody CodeVerifyDTO codeVerifyDTO) {
        return joinService.verifyCode(codeVerifyDTO);
    }

    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDTO> signUp(@RequestBody SignUpDTO signUpDTO) {
        return joinService.signUp(signUpDTO);
    }

    @Override
    @PutMapping("/change-username")
    public ResponseEntity<ResponseDTO> changeUsername(@RequestBody com.example.ddingsroom.user.dto.ChangeUsernameDTO changeUsernameDTO) {
        // 인증된 사용자 정보 추출 (신원은 토큰에서 파생)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserEntity().getId();

        return joinService.changeUsername(userId, changeUsernameDTO);
    }

    @Override
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseDTO> verifyEmail(@RequestBody EmailVerificationDTO emailVerificationDTO) {
        // 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tokenEmail = userDetails.getUserEntity().getEmail();
        
        return joinService.verifyUserEmail(tokenEmail, emailVerificationDTO.getEmail());
    }

    @Override
    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseDTO> withdrawUser() {
        // 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserEntity().getId();
        
        return joinService.withdrawUserById(userId);
    }
}
