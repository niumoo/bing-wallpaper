const BASE_URL = 'https://api.debug.group/api/v1/wallpaper/mylove';

// 校验登录状态和获取 token
function checkLogin() {
    const token = localStorage.getItem('userTicket');
    if (!token) {
        loadWeixinLogin();
        weixinLogin();
        showNotification(null, '请先登录');
        console.log('Token is empty. Handle accordingly.');
        return;
    }
    return token ? token : null;
}

function logout() {
    // 清除本地存储中的用户ticket
    localStorage.removeItem('userTicket');
    // 提示用户登出成功
    alertMessage('你已经退出登录');
    // 可选择重定向用户到登录页面或主页
    window.location.href = '/index.html';
    console.log('User has been logged out successfully.');
}

// 处理请求
async function handleRequest(method, body) {
    const token = checkLogin();
    if (!token) {
        return null;
    }
    const headers = new Headers();
    headers.append('Authorization', 'Bearer ' + token);
    headers.append('Content-Type', 'application/json');
    try {
        const response = await fetch(BASE_URL, {
            method: method,
            headers: headers,
            body: JSON.stringify(body)
        });
        const data = await response.json();
        // 检查 HTTP 响应状态
        if (response.ok && data.code === 200) {
            console.log('Success:', data);
            return data.data;
        } else if (response.ok && data.code === 1002) {
            // jwt token expired
            console.log('Token is expired. Handle accordingly.');
            alertMessage('你的登录状态已过期');
            // 休眠1s
            await new Promise(resolve => setTimeout(resolve, 1000));
            logout();
        } else {
            console.log(`Request failed with status ${response.status}: ${data.message}`);
            // throw new Error(`Request failed with status ${response.status}: ${data.message}`);
        }
    } catch (error) {
        console.error('Error:', error);
    }
    return null;
}

// 发起 PUT 请求，以 region 和 date 为参数
async function updateLove(region, date) {
    const body = {
        region: region,
        date: date
    };
    const result = await handleRequest('PUT', body);
    if (result != null) {
        alertMessage('已喜欢');
    }
}

// 发起 DELETE 请求，以 region 和 date 为参数
async function deleteLove(region, date) {
    const body = {
        region: region,
        date: date
    };
    const result = await handleRequest('DELETE', body);
    if (result != null) {
        alertMessage('已移除喜欢');
        window.location.reload();
    }
}

// 原有 getLoveList 函数优化
async function getLoveList() {
    try {
        const data = await handleRequest('GET');
        if (data) {
            generateLoveHTML(data);
        }
    } catch (error) {
        console.error('Error fetching love list:', error);
    }
}



// alert.js
function alertMessage(message) {
    // 创建弹窗元素
    const alertBox = document.createElement('div');
    alertBox.className = 'alert';
    alertBox.innerText = message;
    // 将弹窗元素添加到文档中
    document.body.appendChild(alertBox);
    // 设置初始样式
    alertBox.style.display = 'block'; // 显示弹窗
    alertBox.style.opacity = 1; // 设置透明度为1（可见）
    // 2秒后淡出并隐藏
    setTimeout(function() {
        alertBox.style.opacity = 0; // 设置透明度为0（隐藏）
        setTimeout(function() {
            alertBox.style.display = 'none'; // 完全隐藏
            document.body.removeChild(alertBox); // 移除 DOM 中的弹窗
        }, 500); // 等待淡出结束后再隐藏
    }, 2000); // 2秒后触发
    // 为弹窗添加样式
    const style = document.createElement('style');
    style.innerHTML = `
        .alert {
            position: fixed;
            top: 20px;
            right: 49%;
            background-color: #4caf50; /* 绿色背景 */
            color: white; /* 白色文字 */
            padding: 7px;
            border-radius: 5px;
            opacity: 0; /* 初始透明度 */
            transition: opacity 0.5s; /* 淡入效果 */
            z-index: 1000; /* 确保弹窗在上层 */
        }
    `;
    document.head.appendChild(style);
}

// 导出函数（如果需要在模块中使用，添加此行）
if (typeof module !== 'undefined') {
    module.exports = alertMessage;
}


function generateLoveHTML(data) {
    let htmlOutput = '';
    data.forEach(entry => {
        const date = entry.date;
        const formattedDate = date.substring(0, 7).replace('-',''); // 提取年-月
        const day = date.substring(8, 10); // 提取日
        const imgSrc = entry.url;
        var region = entry.region;
        if (region !== 'zh-cn'){
            region = '/';
        }else {
            region = '/'+region+'/';
        }

        htmlOutput += `
                <div class="w3-third" style="position: relative;height:249px">
                    <img class="smallImg" src="${imgSrc}&pid=hp&w=50" style="width:95%;" />
                    <a href="${region}day/${formattedDate}/${day}.html" target="_blank">
                        <img class="bigImg w3-hover-shadow" src="${imgSrc}&pid=hp&w=384&h=216&rs=1&c=4" style="width:95%" onload="imgloading(this)">
                    </a>
                    <p>${date} <a href="${imgSrc}&rf=LaDigue_UHD.jpg&pid=hp&w=3840&h=2160&rs=1&c=4" target="_blank">Download 4k</a>
                    <button class="like-button img-btn" onclick="deleteLove('${entry.region}','${date}')">移除</button></p>
                </div>
            `;
    });
    document.getElementById('img_list').innerHTML =htmlOutput;
    addImgBtn();
}

function addImgBtn(){
    // 图片增加悬浮按钮
    const w3ThirdList = document.querySelectorAll('.w3-third');
    w3ThirdList.forEach(thirdImg => {
        const likeButton = thirdImg.querySelector('.like-button');
        thirdImg.addEventListener('mouseenter', function() {
            likeButton.style.display = 'block';
        });

        // 当鼠标离开大图时隐藏Like按钮
        thirdImg.addEventListener('mouseleave', function() {
            likeButton.style.display = 'none';
        });
    });
}

addImgBtn();
