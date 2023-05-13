package com.reliable.seoapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reliable.seoapp.helper.SeoHelper;
import com.reliable.seoapp.view.CrawleRequetView;

@RestController
public class SeoController {
	@Value("${cloud.files.path}")
	private String rootDir;

	@Autowired
	SeoHelper helper; 
	
	@RequestMapping(value = "/zip", method = RequestMethod.GET)
	public void downloadSite(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String url) throws IOException {
		CrawleRequetView crawleRequetView = new CrawleRequetView();
		crawleRequetView.setUrlName(url);
		crawleRequetView.setTagName(url);
		helper.beginCrawle(crawleRequetView);
		String folderName = url.split("\\.").clone()[1];
		// return Files.readAllBytes(Paths.get(Constants.ROOT_DIR + ".zip"));
		File file = new File(rootDir+"/"+folderName + ".zip");
		if (file.exists()) {

			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);

			/**
			 * In a regular HTTP response, the Content-Disposition response header is a
			 * header indicating if the content is expected to be displayed inline in the
			 * browser, that is, as a Web page or as part of a Web page, or as an
			 * attachment, that is downloaded and saved locally.
			 * 
			 */

			/**
			 * Here we have mentioned it to show inline
			 */
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			// Here we have mentioned it to show as attachment
			// response.setHeader("Content-Disposition", String.format("attachment;
			// filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}

}