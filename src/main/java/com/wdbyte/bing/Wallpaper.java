package com.wdbyte.bing;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.wdbyte.bing.html.HtmlFileUtils;
import com.wdbyte.bing.html.WebSiteGenerator;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class Wallpaper {

    // BING API
    private static final String BING_API_TEMPLATE = "https://global.bing.com/HPImageArchive.aspx?format=js&idx=0&n=9&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160&setmkt=%s&setlang=en";
    private static String BING_API = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&nc=1612409408851&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160";

    private static String BING_URL = "https://cn.bing.com";

    /**
     *
     * {"en-US", "zh-CN", "ja-JP", "en-IN", "pt-BR", "fr-FR", "de-DE", "en-CA", "en-GB", "it-IT", "es-ES", "fr-CA"};
     */
    private static String[] regions =  {"en-US", "zh-CN"};
    public static void main(String[] args) throws IOException {
        for (String region : regions) {
            String bingApi = String.format(BING_API_TEMPLATE, region);
            changeConfig(region);
            String httpContent = HttpUtls.getHttpContent(bingApi);
            JSONObject jsonObject = JSON.parseObject(httpContent);
            JSONArray jsonArray = jsonObject.getJSONArray("images");

            jsonObject = (JSONObject)jsonArray.get(0);
            // 图片地址
            String url = BING_URL + (String)jsonObject.get("url");

            // 图片时间
            String enddate = (String)jsonObject.get("enddate");
            LocalDate localDate = LocalDate.parse(enddate, DateTimeFormatter.BASIC_ISO_DATE);
            enddate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // 图片版权
            String copyright = (String)jsonObject.get("copyright");

            List<Images> imagesList = BingFileUtils.readBing();
            imagesList.set(0,new Images(copyright, enddate, url));
            imagesList = imagesList.stream().distinct().collect(Collectors.toList());
            BingFileUtils.writeBing(imagesList);
            BingFileUtils.writeReadme(imagesList);
            BingFileUtils.writeMonthInfo(imagesList);
            new WebSiteGenerator().htmlGenerator();
        }
    }

    public static void changeConfig(String region) {
        region = region.toLowerCase();
        if ("en-us".equalsIgnoreCase(region)) {
            BingFileUtils.README_PATH = Paths.get("README.md");
            BingFileUtils.BING_PATH = Paths.get("bing-wallpaper.md");
            BingFileUtils.MONTH_PATH = Paths.get("picture/");
            HtmlFileUtils.BING_HTML_ROOT = Paths.get("docs/");
        } else {
            BingFileUtils.README_PATH = Paths.get(region + "/README.md");
            BingFileUtils.BING_PATH = Paths.get(region + "/bing-wallpaper.md");
            BingFileUtils.MONTH_PATH = Paths.get(region + "/picture/");
            HtmlFileUtils.BING_HTML_ROOT = Paths.get("docs/" + region + "/");
        }
    }

}
