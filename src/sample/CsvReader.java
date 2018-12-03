/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 *   
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package sample;

import java.io.*;
import java.util.*;

/**
 * 
 * CSV文件解析器
 * 
 * @author hjh
 * @date 2017/11/2
 */
public class CsvReader {

	/** 目标文件 */
	private File file;

	/** 输入流对象 */
	private BufferedReader reader;

	/** 文件头 */
	private List<String> headerList;

	/** 行数 */
	private int lineCount;

	/** 下一行缓存 */
	private String nextLine;

	/** 文件元数据 */
	private Map<String, String> metaData;
	
	private String charSet;
	
	public CsvReader(File file) throws Exception {
		
	}
	
	public CsvReader(File file,String charSet) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("文件不存在");
		}
		if (!file.exists()) {
			throw new FileNotFoundException("文件不存在");
		}
		this.file = file;
		this.charSet = charSet;
	}

	public String readHeaders() throws Exception {
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file),charSet));
			String headers = reader.readLine();
			lineCount++;
			metaData = new LinkedHashMap<String, String>();
			String[] split = headers.split(",");
			headerList = new ArrayList<String>(Arrays.asList(split));
			for (String str : headerList) {
				if (metaData.put(str, null) != null) {
					throw new RuntimeException("不合法的表头数据，存在相同名称的列");
 				}
			}
			return headers;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean hasNext() throws Exception {
		try {
			if ((nextLine = reader.readLine()) != null) {
				lineCount++;
				String[] split = nextLine.split(",");
				if (split.length != headerList.size()) {
					throw new RuntimeException("第" + lineCount + "行，格式错误");
				}
				metaData = new LinkedHashMap<String,String>();
				for(int i=0; i<headerList.size(); i++){
					metaData.put(headerList.get(i), split[i]);
				}
				return true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return false;
	}
	
	public String get(String key) throws Exception{
		try {
			return metaData.get(key);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public Map<String,String> getRowMap() throws Exception {
		return metaData;
	}
	
	public int getLine(){
		return lineCount;
	}
}