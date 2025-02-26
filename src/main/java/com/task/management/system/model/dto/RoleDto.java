package com.task.management.system.model.dto;

import com.task.management.system.model.entity.User;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private Long id;
    private String name;
    private Set<User> users;
}
