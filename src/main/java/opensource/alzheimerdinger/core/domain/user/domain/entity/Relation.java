package opensource.alzheimerdinger.core.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "guardian_patient_relation")
@IdClass(RelationId.class)
public class Relation extends BaseEntity{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_no", nullable = false)
    private User patient;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_no", nullable = false)
    private User guardian;

}