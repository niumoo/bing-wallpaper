package com.wdbyte.bing.html;

/**
 * @author niulang
 * @date 2022/08/18
 */
public class HtmlConstant {

    /**
     * 侧边栏目录的归档菜单
     */
    public static class Sidebar{
        public static final String VAR_SIDABAR = "${sidabar}";
        public static final String VAR_SIDABAR_NOW_COLOR = "w3-green";
        public static final String VAR_SIDABAR_COLOR = "w3-hover-green";
        /**
         * <a href="#" onclick="w3_close()" class="w3-bar-item w3-button w3-hover-text-green w3-large">2022-08</a>
         */
        private static final String SIDABAR_MENU = "<a href=\"${sidabar_href_url}\" onclick=\"w3_close()\" class=\"w3-bar-item w3-button w3-hover-green w3-large\">${sidabar_href_title}</a>";

        public static String getSidabarMenuList(String hrefUrl, String hrefTitle) {
            String result = SIDABAR_MENU.replace("${sidabar_href_url}", hrefUrl);
            return result.replace("${sidabar_href_title}", hrefTitle);
        }
    }

    /**
     * 头部图片
     */
    public static class Head{
        public static final String HEAD_IMG_URL = "${head_img_url}";
        public static final String HEAD_IMG_DESC = "${head_img_desc}";
        public static final String HEAD_TITLE = "${head_title}";
    }

    /**
     * 图片列表
     */
    public static class ImgCard {
        public static final String VAR_IMG_CARD_LIST = "${img_card_list}";
        private static final String VAR_IMG_CARD_URL = "${img_card_url}";
        private static final String VAR_IMG_CARD_DATE = "${img_card_date}";
        private static final String IMG_CARD = ""
            + "<div class=\"w3-third \" style=\"position: relative;\">\n"
            +"  <img class=\"smallImg\" src=\"${img_card_url}&pid=hp&w=50\"  style=\"width:95%;\" />"
            + " <img class=\"bigImg\" src=\"${img_card_url}&pid=hp&w=384&h=216&rs=1&c=4\" style=\"width:95%\" onload=\"imgloading(this)\">\n"
            + " <p>${img_card_date} <a href=\"${img_card_url}\" target=\"_blank\">Download 4k</a> </p>\n"
            + "</div>";

        public static String getImgCard(String imgUrl,String date) {
            String result = IMG_CARD.replace(VAR_IMG_CARD_URL, imgUrl);
            return result.replace(VAR_IMG_CARD_DATE, date);
        }
    }
    /**
     * 底部归档
     */
    public static class MonthHistory{
        public static final String VAR_MONTH_HISTORY = "${month_history}";

        public static final String VAR_MONTH_HISTORY_NOW_MONTH_COLOR = "w3-green";
        public static final String VAR_MONTH_HISTORY_MONTH_COLOR = "w3-light-grey";
        private static final String VAR_MONTH_HISTORY_HREF_URL = "${month_href_url}";
        private static final String VAR_MONTH_HISTORY_HREF_TITLE = "${month_href_title}";
        private static final String MONTH_HISTORY_HREF = "<a class=\"w3-tag w3-button w3-hover-green w3-light-grey w3-margin-bottom\" href=\"${month_href_url}\">${month_href_title}</a>";
        public static String getMonthHistory(String url,String title) {
            String result = MONTH_HISTORY_HREF.replace(VAR_MONTH_HISTORY_HREF_URL, url);
            return result.replace(VAR_MONTH_HISTORY_HREF_TITLE, title);
        }
    }
}
