package com.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CommonUtils {
	static Map<Float,Integer> standard_minimum_fixed_profit_amount_cat_map=new LinkedHashMap<Float, Integer>();
	public static void main(String[] args) {
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(100),6);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(200),10);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(300),13);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(400),16);
		standard_minimum_fixed_profit_amount_cat_map.put(Float.valueOf(500),20);
		 processASINDataFeed("C:\\Users\\1023610\\Downloads\\HighCur (2).csv","R");
		//processCommisionTableData("C:\\Users\\1023610\\Downloads\\Dec_2018.csv");
		

	}

	public static List<String> readCSVData(String filepath) {
		List<String> linesList = new ArrayList<String>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(filepath));
			while ((line = br.readLine()) != null) {
				linesList.add(line.toString());
			}
		} catch (Exception e) {
			System.out.println("error" + e);
			e.printStackTrace();
		}
		return linesList;
	}

	public static Properties loadProps(String path) {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream(path)) {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	private static final char DEFAULT_SEPARATOR = ',';

	public static void writeLine(Writer w, List<String> values) throws IOException {
		writeLine(w, values, DEFAULT_SEPARATOR, ' ');
	}

	public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
		writeLine(w, values, separators, ' ');
	}

	// https://tools.ietf.org/html/rfc4180
	private static String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

		boolean first = true;

		// default customQuote is empty

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(separators);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());
	}

	public static boolean addToBlackList(String filename, String EAN) {
		try {
			FileWriter writer = new FileWriter(filename, true);
			writeLine(writer, Arrays.asList(EAN));

			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean processASINDataFeed(String filepath, String marketType) {
		try {
			List<String> linesList = readCSVData(filepath);
			// category comisionmap
			List<String> commisionLinesList = readCSVData("files/input/CommisionTable.csv");
			commisionLinesList.remove(0);
			Map<String,Integer> commissionMap=new HashMap<String, Integer>();
			for (String string : commisionLinesList) {
				String[] temp=string.split(",");
				commissionMap.put(temp[0], Integer.valueOf(temp[2]));
			}
			
			linesList.remove(0);
			//linesList.remove(0);
			List<String> blackList= new ArrayList<String>();
			// if incase blacklist is not created
			try {
				blackList = readCSVData("files/blacklist/" + marketType + "_BlackList.csv");
			} catch (Exception e) {
				System.out.println("blacklist Utility issue" + e);
			}
			List<List<String>> lineValuesInStockArray = new ArrayList<List<String>>();
			List<List<String>> lineValuesOutOfStockArray = new ArrayList<List<String>>();

			String amazonPropFilePath = "files/OutputFileConfig.properties";
			Properties prop = loadProps(amazonPropFilePath);
			String standard_minimum_fixed_profit_amount = prop.getProperty("standard_minimum_fixed_profit_amount");
			String standard_profit_margin = prop.getProperty("standard_profit_margin");
			String quantity = prop.getProperty("quantity");
			String comment = prop.getProperty("comment");
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
							finalAmount=finalAmount+(finalAmount*(commissionMap.get(category))/100);
						}else {
							finalAmount=finalAmount+((finalAmount*18)/100);
						}
						if (!(blackList.contains(code[0].toString()))) {
							lineValues.add(2, quantity);
							lineValues.add(1, df.format(finalAmount).toString());
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
			marketPlaceOutputFiles("files/output/" + marketType + "_output.txt", lineValuesInStockArray);
			marketPlaceOutputFiles("files/output/" + marketType + "_output_OUT OF STOCK.txt", lineValuesOutOfStockArray);
			return true;
		} catch (Exception e) {
			System.out.println("processASINDataFeed "+e);
			return false;
		}
		
	}

	public static void marketPlaceOutputFiles(String filepath, List<List<String>> linesList) {
		try {
			FileWriter writer = new FileWriter(filepath);
			
			for (List<String> lineValues : linesList) {
				CommonUtils.writeLine(writer, lineValues, '\t');
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void processCommisionTableData(String filepath) {
		List<String> salesData = readCSVData(filepath);
		List<String> perItemFee = readCSVData("files/input/PerItemFee.csv");
		Map<String,String> keyValuesMap=listToMap(perItemFee, ",");
		// remove first and 2 lines as there is no data
		salesData.remove(0);
		salesData.remove(0);
		
		HashMap<String, List<String>> categoryCommissionMap = new HashMap<String, List<String>>();
		for (String string : salesData) {
			String[] vals = string.split(";");
			
			List<String> tempList = new ArrayList<String>();
//			for (int i = 0; i < vals.length; i++) {
//				String string2 = vals[i];
//				System.out.println(string2);
//			}
			if (vals.length > 9) {
				String amount=vals[7];
				amount=amount.replace(",", ".");
				amount=amount.replace("-", "");
				Float amountInDecimals = null;
				if(!amount.trim().isEmpty()) {
					amountInDecimals=Float.valueOf(amount);
//					System.out.println("amount"+amountInDecimals);
				}
				
				String commision=vals[9].replace(",", ".");
				commision=commision.replace("-", "");
				Float commisionInDecimals=null;
				if(!commision.trim().isEmpty()) {
					commisionInDecimals=Float.valueOf(commision);
//					System.out.println("commision"+commisionInDecimals);
				}
				Float commInPercentage=null;
				if(commisionInDecimals!=null && amountInDecimals!=null) {
//					for (Map.Entry<String, String> e : keyValuesMap.entrySet()) {
//					    Map.Entry<String,String> next = keyValuesMap.higherEntry(e.getKey()); // next
////					    Map.Entry<String, String> prev = keyValuesMap.lowerEntry(e.getKey());  // previous
//					    if() {
//					    	
//					    }
//					    if(next!=null) {
//					    	System.out.println(next.getKey()+next.getValue());
//					    }
//					    break;
//					   // do work with next and prev
//					}
					Float perItemsFeeFinal=null;
					Float perItemsFeeCommFinal=null;
					Float perItemsFee=null;
					for (String thresholdAmount : keyValuesMap.keySet()) {
						perItemsFee=Float.valueOf(thresholdAmount);
						perItemsFeeFinal=perItemsFee;
						perItemsFeeCommFinal=Float.valueOf(keyValuesMap.get(thresholdAmount));
						if(perItemsFee > amountInDecimals) {
							break;
						}
						
					}
					if(perItemsFee <= amountInDecimals) {
						perItemsFeeCommFinal=(float) 5;
						System.out.println(" if actual "+amountInDecimals+" inside "+perItemsFeeFinal +" Commi "+perItemsFeeCommFinal);
					}else {
						System.out.println("else actual "+amountInDecimals+" inside "+perItemsFeeFinal +" Commi "+perItemsFeeCommFinal);
					}
					commInPercentage=((commisionInDecimals-perItemsFeeCommFinal)*100)/amountInDecimals;
					
//					System.out.println("commInPercentage"+commInPercentage);
					String EAN=vals[3].replace("\"", "");
					//change after debug over
					String finalValTemp=EAN+"; "+"Amount: "+amountInDecimals+ " ,Actual Comm: "+commisionInDecimals+ " ,Per Item comm: "+perItemsFeeCommFinal +" ,Final comm: "+commInPercentage.toString();
					if (categoryCommissionMap.containsKey(EAN)) {
						categoryCommissionMap.get(EAN).add(commInPercentage.toString());
					} else {
						tempList.add(commInPercentage.toString());
						categoryCommissionMap.put(EAN, tempList);
					}
				}
			}

		}
		HashMap<String, List<String>> categoriesEANListMap=categoriesEANListMap();
		HashMap<String, List<String>> categoriesCommisionMap=new HashMap<String, List<String>>();
		for (String EAN : categoryCommissionMap.keySet()) {
			for (String category : categoriesEANListMap.keySet()) {
				if(categoriesEANListMap.get(category).contains(EAN)) {
					if(categoriesCommisionMap.containsKey(category)) {
						categoriesCommisionMap.get(category).addAll(categoryCommissionMap.get(EAN));
					}else {
						categoriesCommisionMap.put(category, categoryCommissionMap.get(EAN));
					}
				}
				
			}
		}
		commisionTableDataFile(categoriesCommisionMap);
	}

	public static void commisionTableDataFile(HashMap<String, List<String>> categoryCommissionMap) {
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.CEILING);
		try {
			FileWriter writer = new FileWriter("files/temp.txt");
			for (String key : categoryCommissionMap.keySet()) {
				
				List<String> list = categoryCommissionMap.get(key);
				//List<Float> integerList=list.stream().map(s-> Float.valueOf(df.format(Float.valueOf(s)))).collect(Collectors.toList());
				
				list.add(0, key);
				//list.add(1, mostCommon(integerList).toString());
				CommonUtils.writeLine(writer, list, '\t');
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static HashMap<String, List<String>> categoriesEANListMap() {
		List<String> linesList = readCSVData("C:\\Users\\1023610\\Downloads\\HighCur.csv");
		linesList.remove(0);
		HashMap<String, List<String>> categoriesEANListMap = new HashMap<String, List<String>>();
		for (String string : linesList) {
			String firstColumn = string.split(",")[0];
			String[] vals = firstColumn.split("-");
			String EAN = vals[0];
			String category = vals[2].replace("_", "");
			List<String> tempList = new ArrayList<String>();
			if (categoriesEANListMap.containsKey(category)) {
				categoriesEANListMap.get(category).add(EAN);
			} else {
				tempList.add(EAN);
				categoriesEANListMap.put(category, tempList);
			}

		}
		return categoriesEANListMap;
	}
	
	public static Map<String,String> listToMap(List<String> list,String delimator){
		Map<String,String> map=new LinkedHashMap<String, String>();
		for (String line : list) {
			String[] vals=line.split(delimator);
			map.put(vals[0], vals[1]);
		}
		return map;
	}
	
	public static <T> T mostCommon(List<T> list) {
	    Map<T, Integer> map = new HashMap<>();

	    for (T t : list) {
	        Integer val = map.get(t);
	        map.put(t, val == null ? 1 : val + 1);
	    }

	    Entry<T, Integer> max = null;

	    for (Entry<T, Integer> e : map.entrySet()) {
	        if (max == null || e.getValue() > max.getValue())
	            max = e;
	    }

	    return max.getKey();
	}
}
