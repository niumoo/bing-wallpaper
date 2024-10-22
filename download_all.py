import os
import re
import urllib
import urllib.request
import tqdm

def download(date, url):
    try:
        urllib.request.urlretrieve(url, "dist/" + date + ".jpg")
    except Exception as e:
        print(e, url)

def wash(filename : str) -> list[tuple[str]]:
    with open(filename, "r") as f:
        # 2021-02-28 [download 4k](https://cn.bing.com/th?id=OHR.TwinsDenning_EN-US9910127756_UHD.jpg)
        ans = re.findall(r'(\d{4}-\d{2}-\d{2})\s+\[download 4k\]\((https?://[^\s]+)\)\|', f.read())
        return ans

def downloads(arr : list):
    for date, url in tqdm.tqdm(arr):
        download(date, url)

ans = []

if not os.path.isdir("dist"):
  os.mkdir("dist")
for i in os.listdir("picture"):
    path = os.path.join("picture", i, "README.md")
    if os.path.isfile(path):
        ans.extend(wash(path))

downloads(ans)
