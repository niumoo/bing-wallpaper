package com.wdbyte.bing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件操作工具类
 *
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class FileUtils {

    private static Path readmePath = Paths.get("README.md");
    private static Path bingPath = Paths.get("bing-wallpaper.md");

    /**
     * 读取 bing-wallpaper.md
     *
     * @return
     * @throws IOException
     */
    public static List<Images> readBing() throws IOException {
        if (!Files.exists(bingPath)) {
            Files.createFile(bingPath);
        }
        List<String> allLines = Files.readAllLines(bingPath);
        allLines = allLines.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<Images> imgList = new ArrayList<>();
        imgList.add(new Images());
        for (int i = 1; i < allLines.size(); i++) {
            String s = allLines.get(i).trim();
            int descEnd = s.indexOf("]");
            int urlStart = s.lastIndexOf("(") + 1;

            String date = s.substring(0, 10);
            String desc = s.substring(14, descEnd);
            String url = s.substring(urlStart, s.length() - 1);
            imgList.add(new Images(desc, date, url));
        }
        return imgList;
    }

    /**
     * 写入 bing-wallpaper.md
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeBing(List<Images> imgList) throws IOException {
        if (!Files.exists(bingPath)) {
            Files.createFile(bingPath);
        }
        Files.write(bingPath, "## Bing Wallpaper".getBytes());
        Files.write(bingPath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        for (Images images : imgList) {
            Files.write(bingPath, images.formatMarkdown().getBytes(), StandardOpenOption.APPEND);
            Files.write(bingPath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            Files.write(bingPath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 读取 README.md
     *
     * @return
     * @throws IOException
     */
    public static List<Images> readReadme() throws IOException {
        if (!Files.exists(readmePath)) {
            Files.createFile(readmePath);
        }
        List<String> allLines = Files.readAllLines(readmePath);
        List<Images> imgList = new ArrayList<>();
        for (int i = 3; i < allLines.size(); i++) {
            String content = allLines.get(i);
            Arrays.stream(content.split("\\|"))
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    int dateStartIndex = s.indexOf("[", 3) + 1;
                    int urlStartIndex = s.indexOf("(", 4) + 1;
                    String date = s.substring(dateStartIndex, dateStartIndex + 10);
                    String url = s.substring(urlStartIndex, s.length() - 1);
                    return new Images(null, date, url);
                })
                .forEach(imgList::add);
        }
        return imgList;
    }

    /**
     * 写入 README.md
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeReadme(List<Images> imgList) throws IOException {
        if (!Files.exists(readmePath)) {
            Files.createFile(readmePath);
        }
        //List<String> allLines = Files.readAllLines(path);
        Files.write(readmePath, "## Bing Wallpaper".getBytes());
        Files.write(readmePath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(readmePath, imgList.get(0).toLarge().getBytes(), StandardOpenOption.APPEND);
        Files.write(readmePath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(readmePath, "|      |      |      |".getBytes(), StandardOpenOption.APPEND);
        Files.write(readmePath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        //Files.write(readmePath, "| ---- | ---- | ---- |".getBytes(), StandardOpenOption.APPEND);
        Files.write(readmePath, "| :----: | :----: | :----: |".getBytes(), StandardOpenOption.APPEND);
        Files.write(readmePath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        int i = 1;
        for (Images images : imgList) {
            Files.write(readmePath, ("|" + images.toString()).getBytes(), StandardOpenOption.APPEND);
            if (i % 3 == 0) {
                Files.write(readmePath, "|".getBytes(), StandardOpenOption.APPEND);
                Files.write(readmePath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
            i++;
        }
        if (i % 3 != 1) {
            Files.write(readmePath, "|".getBytes(), StandardOpenOption.APPEND);
        }
    }

}
