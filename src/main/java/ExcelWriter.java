import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ExcelWriter{

    private HashMap<String, String> donorsList;
    private File fileToWorkOn = null;
    private String donationStatus;
    private boolean isNewReport;
    Workbook workbook;

    public ExcelWriter(File report, HashMap<String, String> donors, String donationStatus, boolean isNewReport){
        this.fileToWorkOn = report;
        this.donorsList = donors;
        this.donationStatus= donationStatus;
        this.isNewReport = isNewReport;
        this.workbook = new XSSFWorkbook();
    }

    private Sheet createNewSheet(){
        return donationStatus.equals(DonateStatusEnum.CASH.getEnumValue()) ?
                workbook.createSheet(DonateStatusEnum.CASH.getEnumValue()) :
                workbook.createSheet(DonateStatusEnum.GIFT.getEnumValue());
    }

    private Sheet getExistingSheet(){
        return donationStatus.equals(DonateStatusEnum.CASH.getEnumValue()) ?
                workbook.getSheet(DonateStatusEnum.CASH.getEnumValue()) :
                workbook.getSheet(DonateStatusEnum.GIFT.getEnumValue());
    }

    void perform(){

        Sheet sheet;
        if (isNewReport){
            sheet = createNewSheet();
        }else {
            try {
                sheet = getExistingSheet();
            }catch (Exception e){
                // is case there is a report but without this sheet
                sheet = createNewSheet();
            }
        }

        for (String key : donorsList.keySet()){
            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum);
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(donorsList.get(key));
        }

        try {
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(this.fileToWorkOn.getPath());
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        }catch (IOException e){

        }
    }
}