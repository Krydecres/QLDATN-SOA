package com.qldatn.auth.dto;

public record LoginResponse(String username, String hoTen, String role, String maSV, String token) {}