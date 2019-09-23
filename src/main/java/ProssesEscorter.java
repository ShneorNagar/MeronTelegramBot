public class ProssesEscorter {

    boolean donationTypeCompleted;
    boolean fileReportTypeCompleted;
    boolean reportNameCompleted;


    public ProssesEscorter(){
        donationTypeCompleted = false;
        fileReportTypeCompleted = false;
        reportNameCompleted = false;
    }

    public void completeDonationType(){
        donationTypeCompleted = true;
    }

    public void completeFileReportType(){
        fileReportTypeCompleted = true;
    }

    public void completeReportName(){
        reportNameCompleted = true;
    }

    public void unCompleteReportName(){
        reportNameCompleted = false;
    }

    public void unCompleteDonationType(){
        donationTypeCompleted = false;
    }

    public void unCompleteFileReportType(){
        fileReportTypeCompleted = false;
    }

}
