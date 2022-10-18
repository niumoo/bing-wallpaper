package com.wdbyte.bing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件操作工具类
 *
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class BingFileUtils {

    private static Path README_PATH = Paths.get("README.md");
    private static Path BING_PATH = Paths.get("bing-wallpaper.md");

    private static Path MONTH_PATH = Paths.get("picture/");


    /**
     * 读取 bing-wallpaper.md
     *
     * @return
     * @throws IOException
     */
    public static List<Images> readBing() throws IOException {
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        List<String> allLines = Files.readAllLines(BING_PATH);
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
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        Files.write(BING_PATH, "## Bing Wallpaper".getBytes());
        Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        for (Images images : imgList) {
            Files.write(BING_PATH, images.formatMarkdown().getBytes(), StandardOpenOption.APPEND);
            Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 读取 README.md
     *
     * @return
     * @throws IOException
     */
    public static List<Images> readReadme() throws IOException {
        if (!Files.exists(README_PATH)) {
            Files.createFile(README_PATH);
        }
        List<String> allLines = Files.readAllLines(README_PATH);
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
        if (!Files.exists(README_PATH)) {
            Files.createFile(README_PATH);
        }
        List<Images> imagesList = imgList.subList(0, 30);
        writeFile(README_PATH, imagesList, null);

        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        // 归档
        Files.write(README_PATH, "### 历史归档：".getBytes(), StandardOpenOption.APPEND);
        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        List<String> dateList = imgList.stream()
            .map(Images::getDate)
            .map(date -> date.substring(0, 7))
            .distinct()
            .collect(Collectors.toList());
        int i = 0;
        for (String date : dateList) {
            String link = String.format("[%s](/picture/%s/) | ", date, date);
            Files.write(README_PATH, link.getBytes(), StandardOpenOption.APPEND);
            i++;
            if (i % 8 == 0) {
                Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
        }
    }


    /**
     * 按月份写入图片信息
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeMonthInfo(List<Images> imgList) throws IOException {
        Map<String, List<Images>> monthMap = convertImgListToMonthMap(imgList);
        for (String key : monthMap.keySet()) {
            Path path = MONTH_PATH.resolve(key);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            path = path.resolve("README.md");
            writeFile(path, monthMap.get(key), key);
        }
    }

    /**
     * 转换图片列表为月度 Map
     *
     * @param imagesList
     * @return
     */
    public static Map<String, List<Images>> convertImgListToMonthMap( List<Images> imagesList){
        Map<String, List<Images>> monthMap = new LinkedHashMap<>();
        for (Images images : imagesList) {
            if (images.getUrl() == null){
                continue;
            }
            String key = images.getDate().substring(0, 7);
            if (monthMap.containsKey(key)) {
                monthMap.get(key).add(images);
            } else {
                ArrayList<Images> list = new ArrayList<>();
                list.add(images);
                monthMap.put(key, list);
            }
        }
        return monthMap;
    }

    /**
     * 写入图片列表到指定位置
     *
     * @param path
     * @param imagesList
     * @param name
     * @throws IOException
     */
    private static void writeFile(Path path, List<Images> imagesList, String name) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        String title = "## Bing Wallpaper";
        if (name != null) {
            title = "## Bing Wallpaper (" + name + ")";
        }
        Files.write(path, title.getBytes());
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, imagesList.get(0).toLarge().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "|      |      |      |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "| :----: | :----: | :----: |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        int i = 1;
        for (Images images : imagesList) {
            Files.write(path, ("|" + images.toString()).getBytes(), StandardOpenOption.APPEND);
            if (i % 3 == 0) {
                Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
                Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
            i++;
        }
        if (i % 3 != 1) {
            Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
        }
    }

}
