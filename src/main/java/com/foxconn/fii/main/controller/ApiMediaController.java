package com.foxconn.fii.main.controller;

import com.foxconn.fii.common.response.CommonResponse;
import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.model.MediaRequest;
import com.foxconn.fii.main.data.model.MediaResponse;
import com.foxconn.fii.main.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class ApiMediaController {

    @Autowired
    private MediaService mediaService;

    @Value("${path.data}")
    private String dataPath;

    @PostMapping("/api/media/upload")
    public CommonResponse<MediaResponse> uploadMedia(@ModelAttribute MediaRequest request) throws Exception {
        Media media = mediaService.uploadMedia(request);
        return CommonResponse.success(MediaResponse.of(media));
    }

    @GetMapping(value = "/media/image/thumb/{uuid}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public byte[] getImageThumbMedia(@PathVariable String uuid, final HttpServletResponse response) throws Exception {
        response.addHeader("Cache-Control", "max-age=31536000");
        return mediaService.getImageThumbMedia(uuid);
    }

    @GetMapping(value = "/media/image/{uuid}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public byte[] getImageMedia(@PathVariable String uuid, final HttpServletResponse response) throws Exception {
        response.addHeader("Cache-Control", "max-age=31536000");
        return mediaService.getImageMedia(uuid);
    }

    @GetMapping(value = "/media/file/{uuid}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public byte[] getFileMedia(@PathVariable String uuid, final HttpServletResponse response) throws Exception {
        response.addHeader("Cache-Control", "max-age=31536000");
        return mediaService.getFileMedia(uuid);
    }
}
