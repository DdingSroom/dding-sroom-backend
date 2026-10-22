package com.example.ddingsroom.notification.entity;

import com.example.ddingsroom.community_post.entity.CommunityPostEntity;
import com.example.ddingsroom.community_post_comment.entity.CommunityPostCommentEntity;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post_comment.entity.SuggestPostCommentEntity;
import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import com.example.ddingsroom.user.entity.RefreshEntity;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.entity.VerificationCode;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

// DB 연결 없이 Hibernate가 만들 DDL을 스크립트로만 생성해 검증
class NotificationReadSchemaTest {

    @Test
    void readTableCascadesOnNotificationAndUserDelete() throws Exception {
        Path script = Path.of("build", "schema", "create.sql");
        Files.createDirectories(script.getParent());
        Files.deleteIfExists(script);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
                .applySetting("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName())
                .applySetting("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName())
                .applySetting("jakarta.persistence.schema-generation.database.action", "none")
                .applySetting("jakarta.persistence.schema-generation.scripts.action", "create")
                .applySetting("jakarta.persistence.schema-generation.scripts.create-target", script.toString())
                .applySetting("hibernate.hbm2ddl.delimiter", ";")
                .build();
        try {
            Metadata metadata = new MetadataSources(registry)
                    .addAnnotatedClasses(UserEntity.class, RefreshEntity.class, VerificationCode.class,
                            ReservationEntity.class, RoomEntity.class, CommunityPostEntity.class,
                            CommunityPostCommentEntity.class, SuggestPostEntity.class, SuggestPostCommentEntity.class,
                            SuggestPostImageEntity.class, NotificationEntity.class, NotificationReadEntity.class)
                    .buildMetadata();
            metadata.buildSessionFactory().close();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }

        String ddl = Files.readString(script).toLowerCase();
        assertThat(ddl).contains("create table notification_read");
        assertThat(ddl).containsPattern(
                "constraint uk_notification_read_user_notification unique \\(user_id, ?notification_id\\)");
        assertThat(ddl).containsPattern(
                "fk_notification_read_user foreign key \\(user_id\\) references user_entity \\(id\\) on delete cascade");
        assertThat(ddl).containsPattern(
                "fk_notification_read_notification foreign key \\(notification_id\\) references notification \\(id\\) on delete cascade");
    }
}
