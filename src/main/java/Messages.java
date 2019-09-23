public enum Messages {

    FILE_TYPE_TO_WRITE_ON("and the report will be written to?..."),
    REQUEST_FOR_NEW_REPORT("please give me the report name " +
            "i want to know that is new report so please write at the beginning" +
            "'report'");

    private String enumValue;

    Messages(String value) {
        this.enumValue = value;
    }

    public String getEnumValue() {
        return this.enumValue;
    }
}
