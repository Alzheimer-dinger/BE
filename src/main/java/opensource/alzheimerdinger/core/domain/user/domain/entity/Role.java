package opensource.alzheimerdinger.core.domain.user.domain.entity;

import lombok.Getter;

@Getter
public enum Role {
    GUARDIAN("ROLE_GUARDIAN"),
    PATIENT("ROLE_PATIENT"),;

    private final String name;

    Role(String name) {
        this.name = name;
    }
}