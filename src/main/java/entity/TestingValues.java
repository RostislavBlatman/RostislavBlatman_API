package entity;

public class TestingValues {
    public static final TestingValues WORD_WITH_RANDOMIZED_ALPHABET = new TestingValues("FEfdsff", null);
    public static final TestingValues DATA_IGNORE_DIGITS = new TestingValues("c1ar", "car");
    public static final TestingValues DATA_IGNORE_URL = new TestingValues("www.Finally.com", "Finally");
    public static final TestingValues DATA_FIND_REPEAT_WORDS = new TestingValues("I live live in Saint-Petersburg",
            "can");
    public static final TestingValues DATA_IGNORE_CAPITALIZATION = new TestingValues("москва",
            "Москва");
    public static final TestingValues DATA_WRONG_CAPITALIZATION = new TestingValues("москва",
            "Москва");
    private String wrongWord;
    private String rightWord;

    private TestingValues(String wrongWord, String rightWord) {
        this.wrongWord = wrongWord;
        this.rightWord = rightWord;
    }

    public String getWrongWord() {
        return wrongWord;
    }

    public String getRightWord() {
        return rightWord;
    }


}
