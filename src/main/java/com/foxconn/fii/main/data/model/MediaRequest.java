package com.foxconn.fii.main.data.model;

import com.foxconn.fii.main.data.entity.Media;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MediaRequest {

    private long id;

    private Media.Type type;

    private Media.Privacy privacy = Media.Privacy.PUBLIC;

    private MultipartFile file;
}
