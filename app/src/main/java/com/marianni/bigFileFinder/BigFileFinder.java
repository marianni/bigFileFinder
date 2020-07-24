package com.marianni.bigFileFinder;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BigFileFinder {



    public List<String> getNBiggestFiles(File dir, Integer n){
        List<FileWithSize> allFiles = initializeFileList(dir);
        Collections.sort(allFiles, new FileWithSizeComparator());

        List<String> nBiggestFiles = new ArrayList<>();

        int index = 0;
        for (FileWithSize file : allFiles){
            index++;
            if (index > n){
                break;
            }
            BigDecimal kilobytes = new BigDecimal(file.getSizeInBytes()).divide(new BigDecimal(1024));
            BigDecimal megabytes = kilobytes.divide(new BigDecimal(1024));

            String format = String.format("%s | %s KB | %s MB", file.getPath(),kilobytes.setScale(2, RoundingMode.HALF_UP), megabytes.setScale(2, RoundingMode.HALF_UP));
            nBiggestFiles.add(format);
        }
        return nBiggestFiles;
    }

    private void findFilesRecursive(File file, List<FileWithSize> list){
        if (file.isDirectory()) {
            List<File> files = Arrays.asList(file.listFiles());
            for(File dirFile : files){
                findFilesRecursive(dirFile, list);
            }
        } else {
            list.add(new FileWithSize(file.getAbsolutePath(), file.length()));
        }
    }

    private List<FileWithSize> initializeFileList(File dir){
        List<FileWithSize> list = new ArrayList<>();
        findFilesRecursive(dir, list);
        return list;
    }

    private static class FileWithSizeComparator implements Comparator<FileWithSize> {
        @Override
        public int compare(FileWithSize a, FileWithSize b) {
            return b.getSizeInBytes().compareTo(a.getSizeInBytes());
        }
    }


}
