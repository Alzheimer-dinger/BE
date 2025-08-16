package opensource.alzheimerdinger.core.domain.user.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.global.common.BaseEntity;

import java.util.Objects;

@Entity
@Getter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id @Tsid
    private String userId;

    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String patientCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateProfile(String name, Gender gender, String encodedNewPassword) {
        this.name = name;
        this.gender = gender;
        if (encodedNewPassword != null && !encodedNewPassword.isBlank()) {
            this.password = encodedNewPassword;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
