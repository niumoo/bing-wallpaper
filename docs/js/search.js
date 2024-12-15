function search(){
    w3.getHttpObject("/images.json", myFunction);
    function myFunction(myObject) {
        // 1. List of items to search in
        const jsonArray = myObject;
        // 2. Set up the Fuse instance
        const fuse = new Fuse(jsonArray, {
            threshold: 0.5, // 匹配度
            keys: ['desc','date'],
            includeScore: true,
            findAllMatches: true, // 返回所有匹配项
            includeMatches: true // 包含匹配的信息
        })
        // 3. Now search!
        const searchValue = document.getElementById("search").value;
        const results = fuse.search(searchValue);
        // const sortedResults = results.sort((a, b) => b.score - a.score);
        // 限制返回的结果最多为 10 个
        const limitedResults = results.slice(0, 30);
        // alert(JSON.stringify(limitedResults));
        // console.log(generateHTML(limitedResults));
        document.getElementById('img_list').innerHTML = generateHTML(limitedResults);
        addImgBtn();
    }
}



function generateHTML(data) {
    let htmlOutput = '';
    data.forEach(entry => {
        const date = entry.item.date;
        const formattedDate = date.substring(0, 7).replace('-',''); // 提取年-月
        const day = date.substring(8, 10); // 提取日
        const imgSrc = entry.item.url;
        var region = entry.item.region;
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
                    <button class="like-button img-btn" onclick="updateLove('${entry.item.region}','${date}')">喜欢</button></p>
                </div>
            `;
    });
    return htmlOutput;
}


// 获取输入框并添加键盘事件监听器
document.getElementById('search').addEventListener('keypress', function(event) {
    if (event.key === 'Enter') {
        search();
    }
});