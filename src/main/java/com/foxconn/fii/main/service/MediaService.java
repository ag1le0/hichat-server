package com.foxconn.fii.main.service;

import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.model.MediaRequest;

public interface MediaService {

    Media getMedia(long mediaId);

    Media uploadMedia(MediaRequest request);

    byte[] getImageThumbMedia(String uuid);

    byte[] getImageMedia(String uuid);

    byte[] getFileMedia(String uuid);
}
