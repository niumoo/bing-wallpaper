package com.wdbyte.bing;

import java.util.Objects;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class Images {
    private String desc;
    private String date;
    private String url;

    @Override
    public String toString() {
        String smallUrl = url;
        if (smallUrl.contains("&")) {
            smallUrl = smallUrl.substring(0, smallUrl.indexOf("&"));
        }
        smallUrl = smallUrl + "&pid=hp&w=384&h=216&rs=1&c=4";
        //return String.format("![](%s)[%s\\| %s](%s)", smallUrl, date, desc, url);
        return String.format("![](%s)%s [download 4k](%s)", smallUrl, date, url);
    }

    public String formatMarkdown() {
        return String.format("%s | [%s](%s) ", date, desc, url);
    }

    public String toLarge() {
        String smallUrl = url;
        if (smallUrl.contains("&")) {
            smallUrl = smallUrl.substring(0, smallUrl.indexOf("&"));
        }
        smallUrl = smallUrl + "&w=1000";
        return String.format("![](%s)Today: [%s](%s)", smallUrl, desc, url);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public String getSimpleUrl() {
        if (url.contains("&")) {
            return url.substring(0, url.indexOf("&"));
        }
        return url;
    }

    public String getDetailUrlPath() {
        String yyyymm = date.replace("-", "").substring(0, 6);
        String dd = date.substring(8);
        return "day/" + yyyymm+"/" + dd+".html";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Images() {
    }

    public Images(String desc, String date, String url) {
        this.desc = desc;
        this.date = date;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Images images = (Images)o;
        return Objects.equals(desc, images.desc) && Objects.equals(date, images.date)
            && Objects.equals(url, images.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desc, date, url);
    }
}
