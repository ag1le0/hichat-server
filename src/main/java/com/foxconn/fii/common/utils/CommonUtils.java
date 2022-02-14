package com.foxconn.fii.common.utils;

import com.foxconn.fii.common.exception.CommonException;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

@UtilityClass
public class CommonUtils {

    public boolean validateEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public String getExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = filename.lastIndexOf(".");
            return index == -1 ? "" : filename.substring(index + 1);
        }
    }

    public void generateThumb(File input, File output) throws IOException {
        BufferedImage image = ImageIO.read(input);
        if (image == null) {
            throw CommonException.of("File is not image");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        float ratio = width * 1.0f / height;
        if (width > 256) {
            width = 256;
            height = Math.round(width / ratio);
        }

        java.awt.Image tmp = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage resized = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        ImageIO.write(resized, "png", output);
    }

    public String htmlEscape(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
