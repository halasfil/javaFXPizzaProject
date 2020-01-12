package model;

public enum Role {
    ROLE_USER, ROLE_ADMIN

    // rola -> ROLE_AUTHORITY       ROLE_USER
    // authority -> AUTHORITY       USER
    // permission -> Set <Role>     [ROLE_USER, ROLE_ADMIN]
}
