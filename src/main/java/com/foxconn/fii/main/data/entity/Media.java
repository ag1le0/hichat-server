package com.foxconn.fii.main.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "pea_media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "url")
    private String url;

    @Column(name = "path")
    private String path;

    @Column(name = "thumb_url")
    private String thumbUrl;

    @Column(name = "thumb_path")
    private String thumbPath;

    @Column(name = "type")
    private Type type;

    @Column(name = "privacy")
    private Privacy privacy;

    @Column(name = "original_name")
    private String originalName;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum Type {
        IMAGE,
        FILE
    }

    public enum Privacy {
        PUBLIC,
        PROTECTED,
        PRIVATE
    }

    public static Media of(long id) {
        Media ins = new Media();
        ins.setId(id);
        return ins;
    }
}
