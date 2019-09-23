import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ReportsManager {

    private File[] reportsFolder;
    private String reportsFolderPath;

    public ReportsManager(){
        reportsFolderPath = getClass().getClassLoader().getResource("reports").getPath();
        reportsFolder = new File(reportsFolderPath).listFiles();
    }

    File[] getReportsFolder(){
        return reportsFolder;
    }

    public List<String> getAllReportsNames(){
        List<String> reports = new ArrayList<>();
        for(File report : getReportsFolder()){
            reports.add(report.getName());
        }
        return reports;
    }

    public List<String> getAllReportsNamesWithSuffix(){
        List<String> reports = new ArrayList<>();
        for(File report : getReportsFolder()){
            reports.add(report.getName() + ".xlsx");
        }
        return reports;
    }

    File getExistingReportByName(BotUtils botUtils, Long chatId, String reportName){

        for (File currentReport : reportsFolder){
            if (currentReport.getName().contains(reportName))
            {
                return currentReport;
            }
        }
        botUtils.sendTextMessage(chatId, "report " + reportName + " dose not exist");
        return null;
    }

    File getReportToSave(BotUtils botUtils, Long chatId, String reportName){

        String fixedReportName = addSuffix(reportName);
        if(isReportAlreadyExist(fixedReportName)){
            botUtils.sendTextMessage(chatId, "report " +reportName + " already exist");
            return null;
        }
        File newReport = new File(reportsFolderPath + File.separatorChar + fixedReportName);
        botUtils.sendTextMessage(chatId, "report will be written to " + fixedReportName);
        return newReport;
    }

    private boolean isReportAlreadyExist(String reportName){
        for(File currentReport : getReportsFolder()){
            if (currentReport.getName().equals(reportName)){
                return true;
            }
        }
        return false;
    }

    private String addSuffix(String reportName){
        return reportName + ".xlsx";
    }

}