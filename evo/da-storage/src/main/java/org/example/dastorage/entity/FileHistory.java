package org.example.dastorage.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.dastorage.audit.BaseEntity;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@Entity
@Table(name = "files_history")
@NoArgsConstructor
@AllArgsConstructor
public class FileHistory extends BaseEntity {
    @Id
    @Column(name = "file_history_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID fileHistoryId;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "file_id")
    private UUID fileId;
    private String action;
}
