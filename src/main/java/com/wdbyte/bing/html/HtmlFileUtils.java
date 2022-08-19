package com.wdbyte.bing.html;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author niulang
 * @date 2022/08/18
 */
public class HtmlFileUtils {

    private static Path BING_HTML_ROOT = Paths.get("docs/");
    private static Path BING_HTML_INDEX_TEMPLATE = Paths.get("docs/bing-template.html");

    public static String readIndexTemplateFile() throws IOException {
        byte[] bytes = Files.readAllBytes(BING_HTML_INDEX_TEMPLATE);
        return new String(bytes);
    }

    public static void writeIndexHtml(String html) throws IOException {
        Path path = BING_HTML_ROOT.resolve("index.html");
       write(path,html);
    }
    private static void write(Path path,String html) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.write(path, html.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeMonthHtml(String month, String html) throws IOException {
        Path path = BING_HTML_ROOT.resolve( month + ".html");
        write(path, html);
    }
}
