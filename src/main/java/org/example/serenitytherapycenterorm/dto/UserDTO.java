package org.example.serenitytherapycenterorm.dto;

import lombok.*;
import org.example.serenitytherapycenterorm.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String address;
    private User.Role role;
    private User.Status status;
}