package com.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CdiscountDataUtil {

	private static final String OUTPUT_FILE_NAME = "files/output/CdiscountOutput.xlsx";
	static Map<Float,Integer> standard_minimum_fixed_profit_amount_cat_map=new LinkedHashMap<Float, Integer>();

	public static void main(String[] args) {
		//createSheetFromTemplate();
		
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(100),6);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(200),10);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(300),13);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(400),16);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(500),20);
		processASINDataFeed("files/input/CdisocuntInputASINs.csv","CDiscount");
		
	}
	
	public static boolean processASINDataFeed(String filepath, String marketType) {
//		try {
			List<String> linesList = CommonUtils.readCSVData(filepath);
			// category comisionmap
			List<String> commisionLinesList = CommonUtils.readCSVData("files/input/CdiscountCategoryCommisionTable.csv");
			commisionLinesList.remove(0);
			Map<String,Float> commissionMap=new HashMap<String, Float>();
			for (String string : commisionLinesList) {
				String[] temp=string.split(",");
				commissionMap.put(temp[0], Float.valueOf(temp[1]));
			}
			
			//linesList.remove(0);
			List<String> blackList= new ArrayList<String>();
			// if incase blacklist is not created
			try {
				blackList = CommonUtils.readCSVData("files/blacklist/" + marketType + "_BlackList.csv");
			} catch (Exception e) {
				System.out.println("blacklist Utility issue" + e);
			}
			List<List<String>> lineValuesInStockArray = new ArrayList<List<String>>();
			List<List<String>> lineValuesOutOfStockArray = new ArrayList<List<String>>();

			String paramFilePath = "files/OutputFileConfig.properties";
			Properties prop = CommonUtils.loadProps(paramFilePath);
			String standard_minimum_fixed_profit_amount = prop.getProperty("standard_minimum_fixed_profit_amount");
			String standard_profit_margin = prop.getProperty("standard_profit_margin");
			String quantity = prop.getProperty("quantity");
			String comment = prop.getProperty("comment");
			String shipTime= prop.getProperty("shipTime");
			for (String string : linesList) {
				try {
					List<String> lineValues = new ArrayList<String>();
					String[] values = string.split(",");
					String ASINCode = values[0];
					String[] code = ASINCode.split("-");
					lineValues.add(code[0]);
					String category=code[2].replace("_", "");
					//System.out.println("actualAmount"+code[2].replace("_", ""));
					
					
					String status = values[2];
					
					lineValues.add("N");
					lineValues.add(comment);
					lineValues.add(code[0]);
					if (status.trim().equalsIgnoreCase("in stock")) {
						// amount need to be processed
						Float actualAmount = Float.valueOf(values[3]);
						for (Float thresholdCategory : standard_minimum_fixed_profit_amount_cat_map.keySet()) {
							standard_minimum_fixed_profit_amount=standard_minimum_fixed_profit_amount_cat_map.get(thresholdCategory).toString();
							if(actualAmount <= thresholdCategory) {
								break;
							}
							if(actualAmount > 500) {
								standard_minimum_fixed_profit_amount="23";
								break;
							}
						}
						DecimalFormat df = new DecimalFormat("#.##");
						Float finalAmount=actualAmount+(actualAmount*Float.valueOf(standard_profit_margin)/100)+Float.valueOf(standard_minimum_fixed_profit_amount);
						// add commision
						if(commissionMap.containsKey(category)) {
							finalAmount=finalAmount+(finalAmount*(commissionMap.get(category)));
						}else {
							finalAmount=finalAmount+((finalAmount*18));
						}
						if (!(blackList.contains(code[0].toString()))) {
							lineValues.add(2, quantity);
							lineValues.add(1, df.format(finalAmount).toString());
							lineValues.add(shipTime);
							lineValuesInStockArray.add(lineValues);
						} else {
							System.out.println("out of stock"+code[0].toString());
							lineValues.add(2, "0");
							lineValues.add(1, "7000");
							lineValuesOutOfStockArray.add(lineValues);
						}
					} else {
						lineValues.add(2, "0");
						lineValues.add(1, "7000");
						lineValuesOutOfStockArray.add(lineValues);
					}
				}catch (Exception e) {
					System.out.println("Exception Occured for "+string +" Exception"+e);
				}

			}
			createSheetFromTemplate(lineValuesInStockArray,lineValuesOutOfStockArray);
			//marketPlaceOutputFiles("files/output/" + marketType + "_output.txt", lineValuesInStockArray);
			//marketPlaceOutputFiles("files/output/" + marketType + "_output_OUT OF STOCK.txt", lineValuesOutOfStockArray);
			return true;
//		} catch (Exception e) {
//			System.out.println("E" +e);
//			return false;
//		}
	}
	

	public static void createSheetFromTemplate(List<List<String>> inStockList,List<List<String>> outOfStockList) {
		try {
			FileInputStream excelFile = new FileInputStream(new File("files/input/CdiscountTemplate.xlsx"));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet sheet = workbook.getSheetAt(0);
			int rowNum = 5;
			for (List<String> datatype : inStockList) {
				Row row = sheet.createRow(rowNum++);
//				for (String field : datatype) {
					Cell cell1 = row.createCell(0);
					Cell cell2 = row.createCell(1);
					Cell cell3 = row.createCell(2);
					Cell cell4 = row.createCell(3);
					Cell cell5 = row.createCell(4);
					Cell cell6 = row.createCell(5);
					cell1.setCellValue((String) datatype.get(0));
					cell2.setCellValue((String) datatype.get(0));
					cell3.setCellValue("Neuf - Neuf");
					cell4.setCellValue((String) datatype.get(3));
					cell5.setCellValue((String) datatype.get(1));
					cell6.setCellValue(20);
					row.createCell(6).setCellValue(0);
					row.createCell(7).setCellValue(0);
					row.createCell(19).setCellValue("Oui");
					row.createCell(20).setCellValue( datatype.get(1));
					//shipping time need to be from property file
					row.createCell(21).setCellValue((String) datatype.get(6));
					row.createCell(22).setCellValue(0);
					row.createCell(24).setCellValue(0);
					row.createCell(26).setCellValue(0);
					row.createCell(28).setCellValue("Oui");
					row.createCell(29).setCellValue("Oui");
					
//				}
			}

			try {
				FileOutputStream outputStream = new FileOutputStream(OUTPUT_FILE_NAME);
				workbook.write(outputStream);
				workbook.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Done");


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void create() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
		Object[][] datatypes = { { "Datatype", "Type", "Size(in bytes)" }, { "int", "Primitive", 2 },
				{ "float", "Primitive", 4 }, { "double", "Primitive", 8 }, { "char", "Primitive", 1 },
				{ "String", "Non-Primitive", "No fixed size" } };

		int rowNum = 0;
		System.out.println("Creating excel");

		for (Object[] datatype : datatypes) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : datatype) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(OUTPUT_FILE_NAME);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");
		
	}
}
