package com.marianni.bigFileFinder;

import java.util.Objects;

/**
 * @author mariannarachelova
 */
public class FileWithSize {

    private final String path;
    private final Long sizeInBytes;

    public FileWithSize(String path, Long sizeInBytes) {
        this.path = path;
        this.sizeInBytes = sizeInBytes;
    }

    public String getPath() {
        return path;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

}
