public enum DonateStatusEnum {

    CASH("מזומן"),
    GIFT("מתנה");

    private String enumValue;

    DonateStatusEnum(String value) {
        this.enumValue = value;
    }

    public String getEnumValue() {
        return this.enumValue;
    }
}
