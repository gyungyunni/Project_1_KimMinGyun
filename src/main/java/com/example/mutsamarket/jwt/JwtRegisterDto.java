package com.example.mutsamarket.jwt;

import lombok.Data;
@Data
public class JwtRegisterDto {
    private String username;
    private String password;
    private String passwordCheck;
    private String address; // 주소
    private String email; // 이메일
    private String phone; // 전화번호
}