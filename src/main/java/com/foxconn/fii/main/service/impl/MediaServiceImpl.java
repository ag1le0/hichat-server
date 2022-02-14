package com.foxconn.fii.main.service.impl;

import com.foxconn.fii.common.exception.CommonException;
import com.foxconn.fii.common.exception.ForbiddenException;
import com.foxconn.fii.common.exception.NotFoundException;
import com.foxconn.fii.common.utils.CommonUtils;
import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.model.MediaRequest;
import com.foxconn.fii.main.data.repository.GroupMediaRepository;
import com.foxconn.fii.main.data.repository.MediaRepository;
import com.foxconn.fii.main.data.repository.UserMediaRepository;
import com.foxconn.fii.main.service.GroupService;
import com.foxconn.fii.main.service.MediaService;
import com.foxconn.fii.main.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private UserMediaRepository userMediaRepository;

    @Autowired
    private GroupMediaRepository groupMediaRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${path.data}")
    private String dataPath;

    @Override
    public Media getMedia(long mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> CommonException.of("Media {} is not found", mediaId));
    }

    @Override
    public Media uploadMedia(MediaRequest request) {
        if (request.getFile() == null || StringUtils.isEmpty(request.getFile().getOriginalFilename())) {
            throw CommonException.of("Uploaded file is blank");
        }

        String originalName = request.getFile().getOriginalFilename().toLowerCase();
        String extension = FilenameUtils.getExtension(originalName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String name = uuid + "." + extension;

        String subFolder;
        if (request.getType() == null) {
            if (!StringUtils.isEmpty(request.getFile().getContentType()) && request.getFile().getContentType().startsWith("image/")) {
                subFolder = "image/";
                request.setType(Media.Type.IMAGE);
            } else {
                subFolder = "file/";
                request.setType(Media.Type.FILE);
            }
        } else if (Media.Type.IMAGE == request.getType()) {
            subFolder = "image/";
        } else {
            subFolder = "file/";
        }

        User currentUser = userService.getCurrentUser();
        Media media = new Media();
        media.setUuid(uuid);
        media.setOriginalName(originalName);
        media.setUrl(String.format("%s/media/%s/%s", contextPath, request.getType().toString().toLowerCase(), uuid));
        media.setOwner(currentUser);
        media.setType(request.getType());
        media.setPrivacy(request.getPrivacy());
        media.setPath(subFolder + name);

        File file = new File(dataPath + subFolder + name);
        try {
            request.getFile().transferTo(file);
        } catch (IOException e) {
            throw CommonException.of("Upload file error {}", e.getMessage());
        }

//        if (Media.Type.IMAGE == request.getType()) {
//            File thumb = new File(dataPath + subFolder + "thumb/" + name);
//            try {
//                CommonUtils.generateThumb(file, thumb);
//            } catch (IOException e) {
//                throw CommonException.of("Upload file error {}", e.getMessage());
//            }
//            media.setThumbUrl(String.format("%s/media/%s/thumb/%s", contextPath, request.getType().toString().toLowerCase(), uuid));
//            media.setThumbPath(subFolder + "thumb/" + name);
//        }

        mediaRepository.save(media);

        return media;
    }

    @Override
    public byte[] getImageThumbMedia(String uuid) {
        User currentUser = userService.getCurrentUser();
        Media media = mediaRepository.findByUuid(uuid)
                .orElseThrow(() -> NotFoundException.of("Media {} is not found", uuid));

        if (media.getPrivacy() == Media.Privacy.PRIVATE && media.getOwner().getId() != currentUser.getId()) {
            throw ForbiddenException.of("You don't have permission");
        } else if (media.getPrivacy() == Media.Privacy.PROTECTED) {
            boolean userFlag = userMediaRepository.existsByUserAndMedia(currentUser, media);

            List<Group> groupList = groupService.getGroupList(currentUser);
            boolean groupFlag = false;
            for (Group group : groupList) {
                if (groupMediaRepository.existsByGroupAndMedia(group, media)) {
                    groupFlag = true;
                    break;
                }
            }

            if (!userFlag && !groupFlag) {
                throw ForbiddenException.of("You don't have permission");
            }
        }

        try {
            final InputStream in = Files.newInputStream(Paths.get(dataPath + media.getThumbPath()));
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw CommonException.of("Get file error {}", e.getMessage());
        }
    }

    @Override
    public byte[] getImageMedia(String uuid) {
        User currentUser = userService.getCurrentUser();
        Media media = mediaRepository.findByUuid(uuid)
                .orElseThrow(() -> NotFoundException.of("Media {} is not found", uuid));

        if (media.getPrivacy() == Media.Privacy.PRIVATE && media.getOwner().getId() != currentUser.getId()) {
            throw ForbiddenException.of("You don't have permission");
        } else if (media.getPrivacy() == Media.Privacy.PROTECTED) {
            boolean userFlag = userMediaRepository.existsByUserAndMedia(currentUser, media);

            List<Group> groupList = groupService.getGroupList(currentUser);
            boolean groupFlag = false;
            for (Group group : groupList) {
                if (groupMediaRepository.existsByGroupAndMedia(group, media)) {
                    groupFlag = true;
                    break;
                }
            }

            if (!userFlag && !groupFlag) {
                throw ForbiddenException.of("You don't have permission");
            }
        }

        try {
            final InputStream in = Files.newInputStream(Paths.get(dataPath + media.getPath()));
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw CommonException.of("Get file error {}", e.getMessage());
        }
    }

    @Override
    public byte[] getFileMedia(String uuid) {
        User currentUser = userService.getCurrentUser();
        Media media = mediaRepository.findByUuid(uuid)
                .orElseThrow(() -> NotFoundException.of("Media {} is not found", uuid));

        if (media.getPrivacy() == Media.Privacy.PRIVATE && media.getOwner().getId() != currentUser.getId()) {
            throw ForbiddenException.of("You don't have permission");
        } else if (media.getPrivacy() == Media.Privacy.PROTECTED) {
            boolean userFlag = userMediaRepository.existsByUserAndMedia(currentUser, media);

            List<Group> groupList = groupService.getGroupList(currentUser);
            boolean groupFlag = false;
            for (Group group : groupList) {
                if (groupMediaRepository.existsByGroupAndMedia(group, media)) {
                    groupFlag = true;
                    break;
                }
            }

            if (!userFlag && !groupFlag) {
                throw ForbiddenException.of("You don't have permission");
            }
        }

        try {
            final InputStream in = Files.newInputStream(Paths.get(dataPath + media.getPath()));
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw CommonException.of("Get file error {}", e.getMessage());
        }
    }
}
