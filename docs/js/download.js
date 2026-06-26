/**
 * Bing Wallpaper 下载增强脚本
 *
 * 通过 fetch -> Blob -> objectURL 方式实现跨域图片直接下载，
 * 下载期间显示加载提示，失败时回退到新标签页打开图片。
 *
 * 使用方式：
 * 1. 给下载链接添加 class="bing-download"
 * 2. 可选 data-region / data-date / data-desc 属性，用于组装文件名
 * 3. 页面引入 <script src="/js/download.js" defer></script>
 */
(function () {
    'use strict';

    var DOWNLOAD_CLASS = 'bing-download';
    var LOADING_ID = 'bing-download-loading';
    var LOADING_STYLE_ID = 'bing-download-loading-style';

    /**
     * 清理文件名中的非法字符并截断长度
     * @param {string} raw 原始描述
     * @return {string} 安全的文件名片段
     */
    function sanitizeFilename(raw) {
        if (!raw) { return 'bing-wallpaper'; }
        // 去除文件名非法字符：/ \ : * ? " < > | 以及控制字符
        var cleaned = raw.replace(/[\/\\:*?"<>|\x00-\x1f]/g, '');
        // 去除首尾空格和点
        cleaned = cleaned.replace(/^[\s.]+|[\s.]+$/g, '');
        // 截断到 50 字符
        if (cleaned.length > 50) {
            cleaned = cleaned.substring(0, 50);
        }
        return cleaned || 'bing-wallpaper';
    }

    /**
     * 从链接的 data-* 属性组装最终文件名
     * 格式：{region}-{date}-{desc}.jpg
     */
    function buildFilename(link) {
        var region = link.getAttribute('data-region') || '';
        // 详情页未显式传 region 时，从 URL 路径推断
        if (!region) {
            var path = window.location.pathname;
            region = path.indexOf('/zh-cn/') !== -1 ? 'zh-cn' : 'en-us';
        }
        var date = link.getAttribute('data-date') || '';
        var desc = link.getAttribute('data-desc') || '';
        // 旧页面无 data-desc 时，从 URL 的 id 参数提取图片名作为描述回退
        if (!desc) {
            var idMatch = link.href.match(/id=(OHR\.[^&]+)/i);
            if (idMatch) {
                // OHR.GrandPlace_EN-US6561229456_UHD.jpg -> GrandPlace
                var nameMatch = idMatch[1].match(/OHR\.(.+?)_[A-Z]{2}-/);
                if (nameMatch) { desc = nameMatch[1]; }
            }
        }
        var parts = [];
        if (region) { parts.push(region); }
        if (date) { parts.push(date); }
        if (desc) { parts.push(sanitizeFilename(desc)); }
        var name = parts.join('-');
        if (!name) { name = 'bing-wallpaper'; }
        return name + '.jpg';
    }

    /**
     * 注入加载提示的样式（只注入一次）
     */
    function injectLoadingStyle() {
        if (document.getElementById(LOADING_STYLE_ID)) { return; }
        var style = document.createElement('style');
        style.id = LOADING_STYLE_ID;
        style.textContent = [
            '#' + LOADING_ID + ' {',
            '  position: fixed;',
            '  top: 0; left: 0; right: 0; bottom: 0;',
            '  background: rgba(0, 0, 0, 0.45);',
            '  display: none;',
            '  align-items: center;',
            '  justify-content: center;',
            '  z-index: 99999;',
            '  pointer-events: none;',
            '}',
            '#' + LOADING_ID + ' .bing-dl-box {',
            '  background: #fff;',
            '  border-radius: 12px;',
            '  padding: 24px 36px;',
            '  display: flex;',
            '  flex-direction: column;',
            '  align-items: center;',
            '  gap: 14px;',
            '  box-shadow: 0 8px 30px rgba(0,0,0,0.3);',
            '  pointer-events: auto;',
            '}',
            '#' + LOADING_ID + ' .bing-dl-spinner {',
            '  width: 38px;',
            '  height: 38px;',
            '  border: 4px solid #e0e0e0;',
            '  border-top-color: #4CAF50;',
            '  border-radius: 50%;',
            '  animation: bingDlSpin 0.8s linear infinite;',
            '}',
            '#' + LOADING_ID + ' .bing-dl-text {',
            '  color: #333;',
            '  font-size: 14px;',
            '  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;',
            '}',
            '@keyframes bingDlSpin {',
            '  to { transform: rotate(360deg); }',
            '}'
        ].join('\n');
        document.head.appendChild(style);
    }

    /**
     * 显示加载提示
     */
    function showLoading() {
        injectLoadingStyle();
        var el = document.getElementById(LOADING_ID);
        if (!el) {
            el = document.createElement('div');
            el.id = LOADING_ID;
            el.innerHTML =
                '<div class="bing-dl-box">' +
                '<div class="bing-dl-spinner"></div>' +
                '<div class="bing-dl-text">下载中，请稍候…</div>' +
                '</div>';
            document.body.appendChild(el);
        }
        el.style.display = 'flex';
    }

    /**
     * 隐藏加载提示
     */
    function hideLoading() {
        var el = document.getElementById(LOADING_ID);
        if (el) {
            el.style.display = 'none';
        }
    }

    /**
     * 下载图片核心函数
     * @param {string} url 图片地址
     * @param {string} filename 保存文件名
     */
    function downloadImage(url, filename) {
        showLoading();
        fetch(url, { mode: 'cors', credentials: 'omit' })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.blob();
            })
            .then(function (blob) {
                var objectUrl = URL.createObjectURL(blob);
                var a = document.createElement('a');
                a.href = objectUrl;
                a.download = filename;
                a.style.display = 'none';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                // 延迟释放，确保下载已触发
                setTimeout(function () {
                    URL.revokeObjectURL(objectUrl);
                }, 1000);
                hideLoading();
            })
            .catch(function (err) {
                console.warn('[bing-download] 下载失败，回退到新标签页打开:', err);
                hideLoading();
                window.open(url, '_blank');
            });
    }

    var BING_IMG_PATTERN = /cn\.bing\.com\/th\?id=OHR/i;

    /**
     * 判断是否为修饰键点击（应保留浏览器默认行为）
     */
    function isModifiedClick(event) {
        return event.ctrlKey || event.metaKey || event.shiftKey || event.altKey || event.button !== 0;
    }

    /**
     * 判断链接是否为 Bing 图片下载链接
     * 匹配带 bing-download class 的链接，或 href 指向 cn.bing.com/th?id=OHR 且 target=_blank 的链接
     */
    function isDownloadLink(link) {
        if (!link || !link.href) { return false; }
        if (link.className && link.className.indexOf(DOWNLOAD_CLASS) !== -1) {
            return true;
        }
        // 回退匹配：旧生成页面中没有 bing-download class，但 href 指向 Bing 图片
        if (link.getAttribute('target') === '_blank' && BING_IMG_PATTERN.test(link.href)) {
            return true;
        }
        return false;
    }

    /**
     * 事件委托：拦截 Bing 图片下载链接的点击
     */
    document.addEventListener('click', function (e) {
        if (isModifiedClick(e)) { return; }
        var link = e.target.closest ? e.target.closest('a') : null;
        if (!link || !isDownloadLink(link)) { return; }
        e.preventDefault();
        var url = link.href;
        var filename = buildFilename(link);
        downloadImage(url, filename);
    });

    /**
     * 为页面上所有 .bing-download 链接补充 data 属性（可选增强）
     * 主要供动态渲染后调用
     */
    function enhance() {
        // 事件委托已覆盖动态内容，此函数保留供外部调用兼容
    }

    // 暴露给外部
    window.bingDownloadEnhance = enhance;
    window.bingDownloadImage = downloadImage;
})();
