package com.zah.util;

import com.zah.entity.LoginInfo;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel操作类
 */
public class ExcelUtil {
	@Autowired
	PublicService publicService;

	/**
	 * 导出excel表格
	 *
	 * @param list
	 *            要导出的内容集合
	 * @param path
	 *            要导出的路径
	 * @param strExcTitle
	 *            导出文件的表头
	 * @param strExcCols
	 *            list对象中map的key对应的表头
	 *
	 */
	public static boolean exportExcel(List<Map<String, Object>> list, String path, String[] strExcTitle,
			String[] strExcCols, HttpSession session) {
		try {
			if (list.size() > 0) {
				// 当前时间
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
				String time = sdf.format(new Date());
				// 生成当天文件夹
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
				String fileName = path + "/" + loginInfo.getName() + "-" + time + ".xlsx"; // 格式：登录者帐号_年月日时分秒
				OutputStream out = new FileOutputStream(fileName);
				// 标题参数
				String title = "sheet1";
				// 工作区
				SXSSFWorkbook wb = new SXSSFWorkbook(100);
				Sheet sh = null;
				Row row = null;
				int sheet_num = 1;// 生成1个SHEET
				int sheetShowNum = 1000000;
				// 计算导出数据需要的工作表
				int dataSum = list.size();
				// 根据数据条数来计算需要多少工做表
				if (dataSum > sheet_num) {
					sheet_num = dataSum % sheetShowNum == 0 ? dataSum / sheetShowNum : dataSum / sheetShowNum + 1;
				}
				List<Sheet> sheetList = new ArrayList<>();
				for (int j = 0; j < sheet_num; j++) {
					Sheet sh1 = wb.createSheet("sheet" + (j + 1));
					Row row1 = sh1.createRow(0);
					for (int i = 0; i < strExcTitle.length; i++) {
						// 设置表格默认列宽度为15个字节
						sh1.setColumnWidth(i, strExcTitle[i].length() * 2000);
						row1.createCell(i).setCellValue(strExcTitle[i]); // 添加列头
					}
					sheetList.add(sh1);
				}
				// 遍历集合数据，产生数据行
				Iterator<Map<String, Object>> it = list.iterator();
				int index = 0;
				int rowNum = 0;
				int colNum = 0;
				while (it.hasNext()) {
					if (rowNum % sheetShowNum == 0) {
						sh = sheetList.get(index);
						rowNum = 0;
						index++;
					}
					row = sh.createRow(rowNum + 1);// 创建行数
					Map<String, Object> t = it.next();
					colNum = 0;
					for (String colName : strExcCols) {
						row.createCell(colNum).setCellValue(t.get(colName).toString());
						colNum++;
					}
					rowNum++;
				}
				// 写文件
				wb.write(out);
				// 关闭输出流
				out.close();
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			return false;
		}
		return true;
	}
	/**
	 * 生成excel表格
	 *
	 * @param title
	 *            标题参数
	 * @param headers
	 *            导出Excel列头名称
	 * @param strExcCols
	 *            list对象中map的key对应的表头
	 * @param colsWidth
	 *            列宽(单元:字符)
	 * @param dataset
	 *            合并的参数
	 * @param dataset
	 *            需要导出的list集合信息
	 */
	public static SXSSFWorkbook generateExcel(String title, String[] headers, String[] strExcCols, int[] colsWidth,
			List<Map<String, Object>> dataset, List<int[]> mergeList, boolean mergeFlag) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		try {
			// 工作区
			Sheet sh = null;
			Row row = null;
			int sheet_num = 1;// 生成1个SHEET
			int sheetShowNum = 1000000;
			// 计算导出数据需要的工作表
			int dataSum = dataset.size();
			// 根据数据条数来计算需要多少工做表
			if (dataSum > sheet_num) {
				sheet_num = dataSum % sheetShowNum == 0 ? dataSum / sheetShowNum : dataSum / sheetShowNum + 1;
			}
			List<Sheet> sheetList = new ArrayList<Sheet>();
			for (int j = 0; j < sheet_num; j++) {
				Sheet sh1 = wb.createSheet("sheet" + (j + 1));
				Row row1 = sh1.createRow(0);
				for (int i = 0; i < headers.length; i++) {
					// 设置表格默认列宽度为15个字节
					// sh1.setDefaultColumnWidth((short) 30);
					sh1.setColumnWidth(i, colsWidth[i] * 256);
					row1.createCell(i).setCellValue(headers[i]); // 添加列头
					row1.setHeight((short) (35 * 20));
				}
				sheetList.add(sh1);
			}
			// 遍历集合数据，产生数据行
			Iterator<Map<String, Object>> it = dataset.iterator();
			int index = 0;
			int rowNum = 0;
			int colNum = 0;
			while (it.hasNext()) {
				if (rowNum % sheetShowNum == 0) {
					sh = sheetList.get(index);
					rowNum = 0;
					index++;
				}
				row = sh.createRow(rowNum + 1);// 创建行数
				Map<String, Object> t = it.next();

				colNum = 0;
				for (String colName : strExcCols) {
					row.createCell(colNum).setCellValue(t.get(colName).toString());
					colNum++;
				}
				rowNum++;
			}
			// 合并
			if (mergeFlag) {
				for (int[] merge : mergeList) {
					sh.addMergedRegion(new CellRangeAddress(merge[0], merge[1], merge[2], merge[3]));
				}
			}
		} catch (Exception e) {
			Start.projectLog.writeInfo("生成Excel异常" + e);
		}
		return wb;
	}

	/**
	 *
	 * 此方法描述的是: 下载excel文件
	 * 
	 * @param response
	 *            响应
	 * @param fileName
	 *            下载后保存的文件名(包含后缀)
	 * @param wb
	 *            SXSSFWorkbook文件
	 */
	@SuppressWarnings("static-access")
	public static void excelDownload(HttpServletResponse response, String fileName, SXSSFWorkbook wb) {
		try {
			response.reset();
			response.setContentType("APPLICATION/OCTET-STREAM");
			// 要显示到客户端的文件名转码是必需的，特别是中文名， 否则可能出现文件名乱码甚至是浏览器显示无法下载的问题
			fileName = response.encodeURL(new String(fileName.getBytes(), "ISO8859_1"));// 转码
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			ServletOutputStream out = response.getOutputStream();
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(out);
			bufferedOutPut.flush();
			wb.write(bufferedOutPut);
			bufferedOutPut.close();
			response.setStatus(response.SC_OK);
			response.flushBuffer();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean exportExcel1(List<Map<String, Object>> list, String path, String[] strExcTitle,
			String[] strExcCols) {
		try {
			if (list.size() > 0) {
				// 当前时间
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
				String time = sdf.format(new Date());
				// 生成当天文件夹
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				String fileName = path + "/" + time + ".xlsx"; // 格式：年月日时分秒
				OutputStream out = new FileOutputStream(fileName);
				// 标题参数
				String title = "sheet1";

				// 工作区
				SXSSFWorkbook wb = new SXSSFWorkbook(100);
				Sheet sh = null;
				Row row = null;
				int sheet_num = 1;// 生成1个SHEET
				int sheetShowNum = 1000000;
				// 计算导出数据需要的工作表
				int dataSum = list.size();
				// 根据数据条数来计算需要多少工做表
				if (dataSum > sheet_num) {
					sheet_num = dataSum % sheetShowNum == 0 ? dataSum / sheetShowNum : dataSum / sheetShowNum + 1;
				}
				List<Sheet> sheetList = new ArrayList<>();
				for (int j = 0; j < sheet_num; j++) {
					Sheet sh1 = wb.createSheet("sheet" + (j + 1));
					Row row1 = sh1.createRow(0);
					for (int i = 0; i < strExcTitle.length; i++) {
						// 设置表格默认列宽度为15个字节
						sh1.setColumnWidth(i, strExcTitle[i].length() * 2000);
						row1.createCell(i).setCellValue(strExcTitle[i]); // 添加列头
					}
					sheetList.add(sh1);
				}

				// 遍历集合数据，产生数据行
				Iterator<Map<String, Object>> it = list.iterator();
				int index = 0;
				int rowNum = 0;
				int colNum = 0;
				while (it.hasNext()) {
					if (rowNum % sheetShowNum == 0) {
						sh = sheetList.get(index);
						rowNum = 0;
						index++;
					}
					row = sh.createRow(rowNum + 1);// 创建行数
					Map<String, Object> t = it.next();
					colNum = 0;
					for (String colName : strExcCols) {
						row.createCell(colNum).setCellValue(t.get(colName).toString());
						colNum++;
					}
					rowNum++;
				}
				// 写文件
				wb.write(out);
				// 关闭输出流
				out.close();
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			return false;
		}
		return true;
	}

	public HttpServletResponse getResponse(HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		return response;
	}

}
