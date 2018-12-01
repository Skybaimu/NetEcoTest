package com.yzsj.net.utils;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**读取excel工具类*/
public class POIUtil {
    private static Logger log = LoggerFactory.getLogger(POIUtil.class);
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";


    public static List<String[]> readExcel(String path) throws IOException {
        File excelFile = new File(path);
        checkFile(excelFile);
        Workbook workbook = getWorkBook(excelFile);
        List<String[]> list = new ArrayList<>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //当前工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //开始行
                int firstRowNum = sheet.getFirstRowNum();
                //结束行
                int lastRowNum = sheet.getLastRowNum();
                for(int rowNum = firstRowNum + 1;rowNum <= lastRowNum;rowNum++){
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获取当前行得开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获取当前行得列数
                    int lastCellNum = row.getPhysicalNumberOfCells();

                    String [] cells = new String[row.getPhysicalNumberOfCells()];
                    for(int cellNum = firstCellNum;cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return  list;
    }

    /**检查文件*/
    public static void checkFile(File file) throws IOException {
        if(null == file){
            log.error("文件不存在");
            throw new FileNotFoundException("文件不存在");
        }
        String fileName = file.getName();
        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
            log.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }
/**
 * 获取工作簿对象
 * */
    public static Workbook getWorkBook(File file){
        String fileName = file.getName();
        Workbook workbook = null;
        try {
            InputStream is = new FileInputStream(file);
            if(fileName.endsWith(xls)){
                //2003版本excel
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(xlsx)){
                //2007版本
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return workbook;
    }

    public static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return  cellValue;
        }
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }

        //数据类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return  cellValue;
    }

}
