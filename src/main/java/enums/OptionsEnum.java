package enums;

public enum OptionsEnum {
    DEFAULT_OPTION("0"),
    IGNORE_DIGITS("2"),
    IGNORE_URLS("4"),
    FIND_REPEAT_WORDS("8"),
    IGNORE_CAPITALIZATION("512");

    public String options;

    OptionsEnum(String options) {
        this.options = options;
    }
}
