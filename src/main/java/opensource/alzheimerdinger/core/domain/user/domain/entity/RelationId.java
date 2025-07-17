package opensource.alzheimerdinger.core.domain.user.domain.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RelationId implements Serializable {

    @EqualsAndHashCode.Include
    private String patient;

    @EqualsAndHashCode.Include
    private String guardian;

    public RelationId(String patient, String guardian) {
        this.patient = patient;
        this.guardian = guardian;
    }
}