package org.example.dastorage.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.dastorage.audit.BaseEntity;

import java.util.UUID;
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class FileEntity extends BaseEntity {
    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID file_id;
    @Builder.Default
    private boolean deleted =false;
    @Column(name = "file_type")
    private String fileType;
    private Long size;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "user_id")
    private UUID userId;
    private int version;
    @Column(name = "check_sum")
    private String checkSum;
    //private byte[] content;
}
