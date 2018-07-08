package enums;

public enum ParametersEnum {
    YANDEX_SPELLER_API_URI("https://speller.yandex.net/services/spellservice.json/checkText"),
    YANDEX_SPELLER_CHECK_TEXTS_URI("https://speller.yandex.net/services/spellservice.json/checkTexts"),
    TEXT("text"),
    OPTIONS("options"),
    LANG("lang");

    public String param;

    ParametersEnum(String param) {
        this.param = param;
    }
}
