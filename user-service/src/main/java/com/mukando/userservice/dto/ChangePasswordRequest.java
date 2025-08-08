package com.mukando.userservice.dto;

public record ChangePasswordRequest(
  String currentPassword,
  String newPassword
) {}
