package com.reliable.seoapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.reliable.seoapp.helper.ZipCompress;

public class AdvanceHtmlParser {

	
    static List<String> traverseList = new ArrayList<>();
    static final String rootDir = "C:\\Users\\RitaMaa\\Desktop\\root";
    static String url2 = "";
    public static void main(String ar[]) throws Exception {
    	String sitename = "test/url";
    	url2 = sitename;
    	crawleUrl(sitename, sitename);
    	ZipCompress.compress(rootDir+"\\"+sitename.split("\\.")[1]);
    }

    public static void crawleUrl(String url1, String tag) throws Exception {
        if (!traverseList.contains(url1)) {
            System.out.println(url1);
            traverseList.add(url1);
            CrawleDataView crawleDataView = getData(url1);
            writeDataToFile(crawleDataView.getDataArray(), url1, tag);
            //data = data.replaceAll("\\<(.*?)\\>","");
            crawleDataView.getDataList().forEach(data -> {
                String[] data1 = data.split("\"");
                for (String str : data1) {
                	
                	if ((str.startsWith("http")||str.startsWith("'/'") ) && str.contains(tag)) {
                        try {
                        	//System.out.println(str);
                        	crawleUrl(str, tag);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            });
        }
    }


    private static CrawleDataView getData(String url1) throws Exception {
        URL url = new URL(url1);
        URLConnection conn = url.openConnection();
        URL url2 = new URL(url1);
        URLConnection conn1 = url.openConnection();

        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn1.setRequestProperty("User-Agent", "Mozilla/5.0");

        List<String> list = new ArrayList<>();

        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder br = new StringBuilder();
        String dat;
        while (scanner.hasNext()) {
            dat = scanner.nextLine();
            if (dat.contains("src") || dat.contains("http")) {
                list.add(dat);
            }
            br.append(dat);
        }

        CrawleDataView crawleDataView = new CrawleDataView(br, list, getBytesFromInputStream(conn1.getInputStream()));

        return crawleDataView;
    }

    private static void writeDataToFile(byte[] data, String fileName, String tag) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        File file;
        String folderName = fileName.split("\\.")[1];
        fileName = fileName.replace(url2, "");
        if (fileName.equals("")) {
            fileName = "C:\\Users\\RitaMaa\\Desktop\\root\\"+folderName+"\\index.html";
            file = new File(fileName);
        } else {
        	fileName = fileName.replace(':', '\\');
        	//fileName = fileName.replace('.', '\\');
            file = new File("C:\\Users\\RitaMaa\\Desktop\\root\\"+folderName+"\\" + fileName);
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);

    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    static class CrawleDataView {
        StringBuilder urlData;
        List<String> dataList;
        byte[] dataArray;

        public CrawleDataView(StringBuilder urlData, List<String> dataList, byte[] dataArray) {
            this.urlData = urlData;
            this.dataList = dataList;
            this.dataArray = dataArray;
        }

        public StringBuilder getUrlData() {
            return urlData;
        }

        public List<String> getDataList() {
            return dataList;
        }

        public byte[] getDataArray() {
            return dataArray;
        }
    }


}
