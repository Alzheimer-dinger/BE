package opensource.alzheimerdinger.core.domain.relation.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "guardian_patient_relation")
public class Relation extends BaseEntity {

    @Id @Tsid
    private String relationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    @Enumerated(EnumType.STRING)
    private RelationStatus relationStatus;

    @Enumerated(EnumType.STRING)
    private Role initiator;
}