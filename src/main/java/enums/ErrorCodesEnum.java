package enums;

public enum ErrorCodesEnum {
    ERROR_UNKNOWN_WORD(1),
    ERROR_REPEAT_WORD(2),
    ERROR_CAPITALIZATION(3),
    ERROR_TOO_MANY_ERRORS(4);

    public String xmlValue;
    public int number;

    ErrorCodesEnum(int number) {
        this.number = number;

    }
}
