import java.util.HashMap;

public class DonorsResponse {

    HashMap<String, Integer> donorsMap;
    boolean isPassedMapping;

    public DonorsResponse(HashMap<String, Integer> donorsMap, boolean isPassedMapping){
        this.donorsMap = donorsMap;
        this.isPassedMapping = isPassedMapping;
    }

    public HashMap<String, Integer> getDonorsMap() {
        return donorsMap;
    }

    public boolean getIsPassedMapping() {
        return isPassedMapping;
    }

}
