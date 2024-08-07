package com.example.rosaceae.auth;

import com.example.rosaceae.enums.Role;
import com.example.rosaceae.model.RankMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String images;
    private boolean status, enabled;
    private int rankId;
    private Role role;
    private String locationUrl;
    private String address;
}
