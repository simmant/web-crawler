package com.reliable.seoapp.view;

import java.util.List;

import lombok.Getter;

@Getter
public class CrawleDataView {
	StringBuilder urlData;
	List<String> dataList;
	byte[] dataArray;

	public CrawleDataView(StringBuilder urlData, List<String> dataList, byte[] dataArray) {
		this.urlData = urlData;
		this.dataList = dataList;
		this.dataArray = dataArray;
	}

}
