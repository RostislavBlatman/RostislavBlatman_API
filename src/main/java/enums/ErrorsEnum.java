package enums;

public enum ErrorsEnum {

    WRONG_ANSWERS_SIZE("wrong size of answers"),
    WRONG_ERROR_CODE("Wrong error code"),
    WRONG_VARIANTS("Wrong variants");

    public String description;

    ErrorsEnum(String description) {
        this.description = description;
    }
}
