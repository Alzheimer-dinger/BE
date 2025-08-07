//package opensource.alzheimerdinger.core.domain.relation.application.usecase;
//
//import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationConnectRequest;
//import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationReconnectRequest;
//import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
//import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
//import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
//import opensource.alzheimerdinger.core.domain.relation.domain.service.RelationService;
//import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
//import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
//import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
//import opensource.alzheimerdinger.core.global.exception.RestApiException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class RelationManagementUseCaseTest {
//
//    @Mock RelationService relationService;
//    @Mock UserService userService;
//    @InjectMocks RelationManagementUseCase relationManagementUseCase;
//
//    /* ---------- findRelations ---------- */
//    @Test
//    void findRelations_success() {
//        String userId = "u1";
//        List<RelationResponse> expected = List.of(mock(RelationResponse.class));
//        when(relationService.findRelations(userId)).thenReturn(expected);
//
//        List<RelationResponse> actual = relationManagementUseCase.findRelations(userId);
//
//        assertThat(actual).isSameAs(expected);
//        verify(relationService).findRelations(userId);
//    }
//
//    /* ---------- reply ---------- */
//    @Test
//    void reply_success() {
//        User user = new User();
//        String relationId = "r1";
//        Relation relation = mock(Relation.class);
//
//        when(relationService.findRelation(relationId)).thenReturn(relation);
//        when(relation.getRelationStatus()).thenReturn(RelationStatus.REQUESTED);
//        when(relation.isReceiver(user)).thenReturn(true);
//
//        relationManagementUseCase.reply(user.getUserId(), relationId, RelationStatus.ACCEPTED);
//
//        verify(relation).updateStatus(RelationStatus.ACCEPTED);
//    }
//
//    @Test
//    void reply_fail_notRequested() {
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation("r1")).thenReturn(relation);
//        when(relation.getRelationStatus()).thenReturn(RelationStatus.ACCEPTED);
//
//        Throwable thrown = catchThrowable(() -> relationManagementUseCase.reply("receiver", "r1", RelationStatus.ACCEPTED));
//
//        assertThat(thrown)
//                .isInstanceOf(RestApiException.class);
//
//        assertThat(((RestApiException) thrown).getErrorCode())
//                .isEqualTo(_NOT_FOUND.getCode());
//    }
//
//    @Test
//    void reply_fail_notReceiver() {
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation("r1")).thenReturn(relation);
//        when(relation.getRelationStatus()).thenReturn(RelationStatus.REQUESTED);
//        when(relation.isReceiver("receiver")).thenReturn(false);
//
//        Throwable thrown = catchThrowable(() -> relationManagementUseCase.reply("receiver", "r1", RelationStatus.ACCEPTED));
//
//        assertThat(thrown)
//                .isInstanceOf(RestApiException.class);
//
//        assertThat(((RestApiException) thrown).getErrorCode())
//                .isEqualTo(_UNAUTHORIZED.getCode());
//    }
//
//    /* ---------- send ---------- */
//    @Test
//    void send_success() {
//        String guardianId = "g1";
//        String patientId = "p1";
//        RelationConnectRequest req = new RelationConnectRequest(patientId);
//
//        User guardian = mock(User.class);
//        User patient = mock(User.class);
//        when(userService.findUser(guardianId)).thenReturn(guardian);
//        when(userService.findUser(patientId)).thenReturn(patient);
//        when(relationService.existsByGuardianAndPatient(guardian, patient)).thenReturn(false);
//
//        relationManagementUseCase.send(guardianId, req);
//
//        verify(relationService)
//                .save(patient, guardian, RelationStatus.REQUESTED, Role.GUARDIAN);
//    }
//
//    @Test
//    void send_fail_alreadyExists() {
//        String guardianId = "g1";
//        String patientId = "p1";
//        RelationConnectRequest req = new RelationConnectRequest(patientId);
//
//        User guardian = mock(User.class);
//        User patient = mock(User.class);
//        when(userService.findUser(anyString())).thenReturn(guardian, patient);
//        when(relationService.existsByGuardianAndPatient(guardian, patient)).thenReturn(true);
//
//        Throwable thrown = catchThrowable(() -> relationManagementUseCase.send(guardianId, req));
//
//        assertThat(thrown)
//                .isInstanceOf(RestApiException.class);
//
//        assertThat(((RestApiException) thrown).getErrorCode())
//                .isEqualTo(_EXIST_ENTITY.getCode());
//    }
//
//    /* ---------- resend ---------- */
//    @Test
//    void resend_success() {
//        User user = new User();
//        String relationId = "r1";
//        String guardianId = "g2";
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation(relationId)).thenReturn(relation);
//        when(relation.getRelationStatus()).thenReturn(RelationStatus.DISCONNECTED);
//        when(relation.isMember(user)).thenReturn(false);
//
//        RelationReconnectRequest req = new RelationReconnectRequest(relationId, guardianId);
//        relationManagementUseCase.resend(user, req);
//
//        verify(relation).resend(user);
//    }
//
//    @Test
//    void resend_fail_notDisconnected() {
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation("r1")).thenReturn(relation);
//        when(relation.getRelationStatus()).thenReturn(RelationStatus.ACCEPTED);
//
//        RelationReconnectRequest req = new RelationReconnectRequest("r1", "보호자");
//
//        Throwable thrown = catchThrowable(() -> relationManagementUseCase.resend("g1", req));
//
//        assertThat(thrown)
//                .isInstanceOf(RestApiException.class);
//
//        assertThat(((RestApiException) thrown).getErrorCode())
//                .isEqualTo(_NOT_FOUND.getCode());
//    }
//
//    @Test
//    void resend_fail_memberUnauthorized() {
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation("r1")).thenReturn(relation);
//        when(relation.getRelationStatus()).thenReturn(RelationStatus.DISCONNECTED);
//        when(relation.isMember("g1")).thenReturn(true);
//
//        RelationReconnectRequest req = new RelationReconnectRequest("r1", "보호자");
//
//        Throwable thrown = catchThrowable(() -> relationManagementUseCase.resend("g1", req));
//
//        assertThat(thrown)
//                .isInstanceOf(RestApiException.class);
//
//        assertThat(((RestApiException) thrown).getErrorCode())
//                .isEqualTo(_UNAUTHORIZED.getCode());
//    }
//
//    /* ---------- disconnect ---------- */
//    @Test
//    void disconnect_success() {
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation("r1")).thenReturn(relation);
//        when(relation.isMember("g1")).thenReturn(true);
//
//        relationManagementUseCase.disconnect("g1", "r1");
//
//        verify(relation).updateStatus(RelationStatus.DISCONNECTED);
//    }
//
//    @Test
//    void disconnect_fail_notMember() {
//        Relation relation = mock(Relation.class);
//        when(relationService.findRelation("r1")).thenReturn(relation);
//        when(relation.isMember("g1")).thenReturn(false);
//
//        Throwable thrown = catchThrowable(() -> relationManagementUseCase.disconnect("g1", "r1"));
//
//        assertThat(thrown)
//                .isInstanceOf(RestApiException.class);
//
//        assertThat(((RestApiException) thrown).getErrorCode())
//                .isEqualTo(_NOT_FOUND.getCode());
//    }
//}
