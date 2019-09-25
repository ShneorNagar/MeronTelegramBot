import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;

public class ExcelWriter {

    private HashMap<String, String> donorsList;
    private File fileToWorkOn;
    private String donationStatus;
    private boolean isNewReport;
    Workbook workbook;

    public ExcelWriter(File report, HashMap<String, String> donors, String donationStatus, boolean isNewReport) {
        this.fileToWorkOn = report;
        this.donorsList = donors;
        this.donationStatus = donationStatus;
        this.isNewReport = isNewReport;
    }


    void perform() {

        Sheet sheet;
        if (isNewReport) {
            workbook = new XSSFWorkbook();
            sheet = createNewSheet();
        } else {
            workbook = getExistingWorkbook(fileToWorkOn);
            sheet = getExistingSheet() == null ? createNewSheet() : getExistingSheet();
        }

        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.createRow(lastRowNum);

        for (String key : donorsList.keySet()) {
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(donorsList.get(key));
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        }

        try {
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(this.fileToWorkOn.getPath());
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (IOException e) {

        }
    }

    private XSSFWorkbook getExistingWorkbook(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file.getPath());
            return new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Sheet createNewSheet() {
        return donationStatus.equals(DonateStatusEnum.CASH.getEnumValue()) ?
                workbook.createSheet(DonateStatusEnum.CASH.getEnumValue()) :
                workbook.createSheet(DonateStatusEnum.GIFT.getEnumValue());
    }

    private Sheet getExistingSheet() {
        return donationStatus.equals(DonateStatusEnum.CASH.getEnumValue()) ?
                workbook.getSheet(DonateStatusEnum.CASH.getEnumValue()) :
                workbook.getSheet(DonateStatusEnum.GIFT.getEnumValue());
    }
}
