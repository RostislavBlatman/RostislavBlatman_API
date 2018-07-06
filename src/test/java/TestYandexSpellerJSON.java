import beans.YandexSpellerAnswer;
import core.YandexSpellerApi;
import entity.TestingValues;
import enums.OptionsEnum;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.CustomReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static entity.TestingValues.*;
import static enums.ErrorCodesEnum.*;
import static enums.ErrorsEnum.*;
import static enums.LanguageEnum.EN;
import static enums.LanguageEnum.RU;
import static enums.OptionsEnum.*;
import static enums.ParametersEnum.*;
import static org.hamcrest.Matchers.lessThan;

public class TestYandexSpellerJSON {


    @DataProvider(name = "positive testing")
    public Object[][] positiveTestingData() {
        return new Object[][]{
                {"Виктор"},
                {"он живёт на крыше"},
                {"у него есть кошка, собака и стол",},
                {"Он не знает, чем заняться на выходных",},
                {"Поэтому он учит английский",},
                {"My name is Виктор",},
                {"I live on the roof",},
                {"This is 7 floor"},
        };
    }

    @DataProvider(name = "repeat words with ignore capitalization")
    public Object[][] repeatWords() {
        Map<String, TestingValues> dataMap = CustomReader.readJson("dataTestYandexSpellerJSON.json");
        Object[] values = dataMap.values().toArray();
        Object[][] newData = new Object[values.length][1];
        for (int i = 0; i < values.length; i++) {
            newData[i][0] = values[i];
            System.out.println(values[i].getClass().getName());
        }
        return newData;
    }

    // 1 Positive checkText test with data provider
    @Test(dataProvider = "positive testing")
    public void noMistakesTest(String text) {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(text)
                        .callApi());
        assert answers.size() == 0 : WRONG_ANSWERS_SIZE;
    }

    @Test
    public void useIgnoreDigitOption() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_DIGITS.getWrongWord())
                        .language(EN)
                        .options(OptionsEnum.IGNORE_DIGITS.options)
                        .callApi());
        assert answers.size() == 0 : WRONG_ANSWERS_SIZE;
    }

    @Test
    public void checkSpellerWithDigitInWord() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_DIGITS.getWrongWord())
                        .language(EN)
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        assert answers.get(0).s.contains(DATA_IGNORE_DIGITS.getRightWord());

    }

    @Test
    public void useIgnoreUrlsOption() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_URL.getWrongWord())
                        .language(EN)
                        .options(IGNORE_URLS.options)
                        .callApi());
        assert answers.size() == 0 : WRONG_ANSWERS_SIZE;

    }

    //bug
    @Test
    public void checkFixWordWithUrl() {
        RestAssured
                .given()
                .queryParam(TEXT.param, DATA_IGNORE_URL.getWrongWord())
                .params(LANG.param, EN.lang, "CustomParameter", "valueOfParam")
                .accept(ContentType.JSON)
                .log().everything()
                .get(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body(Matchers.allOf(
                        Matchers.stringContainsInOrder(Arrays.asList(DATA_IGNORE_URL.getWrongWord(), DATA_IGNORE_URL.getRightWord())),
                        Matchers.containsString("\"code\":1")))
                .contentType(ContentType.JSON)
                .time(lessThan(20000L));
    }

    @Test
    public void useIgnoreCapitalization() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_CAPITALIZATION.getWrongWord())
                        .language(RU)
                        .options(IGNORE_CAPITALIZATION.options)
                        .callApi());
        assert answers.size() == 0 : WRONG_ANSWERS_SIZE;
    }

    //bug
    @Test
    public void useFixCapitalization() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_CAPITALIZATION.getWrongWord())
                        .language(RU)
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        answers.forEach(answer -> {
            assert answer.code == ERROR_CAPITALIZATION.number : WRONG_ERROR_CODE;
            assert answer.s.contains(DATA_IGNORE_CAPITALIZATION.getRightWord()) : WRONG_VARIANTS;
        });
    }

    //find bug
    @Test
    public void useFindRepeatsOption() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_FIND_REPEAT_WORDS.getWrongWord())
                        .language(EN)
                        .options(OptionsEnum.FIND_REPEAT_WORDS.options)
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        answers.forEach(answer -> {
            assert answer.code == ERROR_REPEAT_WORD.number : WRONG_ERROR_CODE;
            assert answer.s.contains(DATA_FIND_REPEAT_WORDS.getRightWord()) : WRONG_VARIANTS;
        });
    }

    // bug
    @Test
    public void checkUrlIgnoringWithWrongWordExceptions() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_URL.getWrongWord())
                        .language(EN)
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        answers.forEach(answer -> {
            assert answer.code == ERROR_UNKNOWN_WORD.number : WRONG_ERROR_CODE;
            assert answer.s.contains(DATA_IGNORE_URL.getRightWord()) : WRONG_VARIANTS;
        });
    }


    @Test
    public void checkServiceExceptions() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(WORD_WITH_RANDOMIZED_ALPHABET.getWrongWord())
                        .language(EN)
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        answers.forEach(answer -> {
            assert answer.code == ERROR_UNKNOWN_WORD.number : WRONG_ERROR_CODE;
        });
    }

    //bug
    @Test(dataProvider = "repeat words with ignore capitalization")
    public void checkRepeatExceptionAndIgnoreCapitalozation(TestingValues testingValues) {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(testingValues.getWrongWord())
                        .language(EN)
                        .options(Integer.toString(Integer.parseInt(FIND_REPEAT_WORDS.options)
                                + Integer.parseInt(IGNORE_CAPITALIZATION.options)))
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        answers.forEach(answer -> {
            assert answer.s.contains(testingValues.getRightWord()) : WRONG_VARIANTS;
            assert answer.code == ERROR_REPEAT_WORD.number : WRONG_ERROR_CODE;
        });
    }

    //and again a bug
    @Test
    public void checkWrongCapitalizationExceptions() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_WRONG_CAPITALIZATION.getWrongWord())
                        .language(RU)
                        .callApi());
        assert answers.size() > 0 : WRONG_ANSWERS_SIZE;
        answers.forEach(answer -> {
            assert answer.code == ERROR_CAPITALIZATION.number : WRONG_ERROR_CODE;
            assert answer.s.contains(DATA_WRONG_CAPITALIZATION.getRightWord()) : WRONG_VARIANTS;
        });
    }


}