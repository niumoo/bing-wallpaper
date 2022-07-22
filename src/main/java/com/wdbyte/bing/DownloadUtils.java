package com.wdbyte.bing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DownloadUtils {

	public static List<Images> readReadme(String month) throws IOException {
		List<Images> imgList = new ArrayList<>();
		Path readmePath = Paths.get("picture/" + month + "/" + "README.md");
		if (Files.exists(readmePath)) {
			List<String> allLines = Files.readAllLines(readmePath);
			for (int i = 4; i < allLines.size(); i++) {
				String content = allLines.get(i);
				Arrays.stream(content.split("\\|")).filter(s -> !s.isEmpty()).map(s -> {
					int dateStartIndex = s.indexOf(")", 3) + 1;
					int urlStartIndex = s.indexOf("(", 4) + 1;
					String date = s.substring(dateStartIndex, dateStartIndex + 10);
					String url = s.substring(urlStartIndex, s.length() - 1);
					return new Images(null, date, url);
				}).forEach(imgList::add);
			}
		}
		return imgList;
	}

	public static void saveImages(String month, List<Images> imgList) {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		FileOutputStream fos = null;
		try {
			client = HttpClients.createDefault();

			HttpGet get = null;
			for (Images img : imgList) {
				get = new HttpGet(img.getUrl());
				response = client.execute(get);

				Path monthPath = Paths.get("save/" + month);
				if (!Files.exists(monthPath)) {
					Files.createDirectories(monthPath);
				}
				Path savePath = Paths.get("save/" + month + "/" + img.getDate() + ".jpg");
				fos = new FileOutputStream(savePath.toAbsolutePath().toFile());
				fos.write(EntityUtils.toByteArray(response.getEntity()));
				System.out.println("保存文件:" + savePath.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {

		String month = "2022-06";
		List<Images> imgList = readReadme(month);
		saveImages(month, imgList);

	}

}