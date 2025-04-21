package com.example.ecom.proj.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

import static com.example.ecom.proj.enums.Permission.*;

public enum Role {
    USER,
    ADMIN,
    MANAGER
    ;

//@RequiredArgsConstructor
//public enum Role {
//    USER(Collections.emptySet()),
//    ADMIN(
//            Set.of(
//                    ADMIN_READ,
//                    ADMIN_CREATE,
//                    ADMIN_UPDATE,
//                    ADMIN_DELETE,
//                    MANAGER_READ,
//                    MANAGER_CREATE,
//                    MANAGER_UPDATE,
//                    MANAGER_DELETE
//            )
//    ),
//    MANAGER(
//            Set.of(
//                    MANAGER_READ,
//                    MANAGER_CREATE,
//                    MANAGER_UPDATE,
//                    MANAGER_DELETE
//            )
//    )
//    ;
//
//    @Getter
//    private final Set<Permission>permissions;

//    public List<SimpleGrantedAuthority> getAuthorities() {
//        var authorities = getPermissions()
//                .stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))    // eg. "admin:read", "admin:update"
//                .collect(Collectors.toList());
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));  // eg. ROLE_ADMIN
//        return authorities;
//    }

}
