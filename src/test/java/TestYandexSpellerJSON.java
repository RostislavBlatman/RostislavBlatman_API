import beans.YandexSpellerAnswer;
import core.YandexSpellerApi;
import entity.TestingValues;
import enums.OptionsEnum;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.CustomReader;

import java.util.List;
import java.util.Map;

import static entity.TestingValues.*;
import static enums.ErrorCodesEnum.*;
import static enums.LanguageEnum.EN;
import static enums.LanguageEnum.RU;
import static enums.OptionsEnum.*;
import static enums.ParametersEnum.YANDEX_SPELLER_API_URI;

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
        assert answers.size() == 0 : "wrong size of answers";
    }

    @Test
    public void makeBasicRequestTest() {
        //get
        RestAssured
                .given()
                .log().everything()
                .get(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
        //post
        RestAssured
                .given()
                .log().everything()
                .post(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
        //head
        RestAssured
                .given()
                .log().everything()
                .head(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
        //options
        RestAssured
                .given()
                .log().everything()
                .options(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
        //patch
        RestAssured
                .given()
                .log().everything()
                .patch(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
        //put
        RestAssured
                .given()
                .log().everything()
                .put(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);

        //delete
        RestAssured
                .given()
                .log().everything()
                .delete(YANDEX_SPELLER_API_URI.param)
                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test
    public void useIgnoreDigitOption() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_DIGITS.getWrongWord())
                        .language(EN.lang)
                        .options(OptionsEnum.IGNORE_DIGITS.options)
                        .callApi());
        assert answers.size() == 0 : "wrong size of answers";
    }

    @Test
    public void useIgnoreUrlsOption() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_URL.getWrongWord())
                        .language(EN.lang)
                        .options(IGNORE_URLS.options)
                        .callApi());
        assert answers.size() == 0 : "wrong size of answers";

    }

    @Test
    public void useIgnoreCapitalization() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_CAPITALIZATION.getWrongWord())
                        .language(EN.lang)
                        .options(IGNORE_CAPITALIZATION.options)
                        .callApi());
        assert answers.size() == 0 : "wrong size of answers";
    }

    //find bug
    @Test
    public void useFindRepeatsOption() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_FIND_REPEAT_WORDS.getWrongWord())
                        .language(EN.lang)
                        .options(OptionsEnum.FIND_REPEAT_WORDS.options)
                        .callApi());
        assert answers.size() > 0 : "wrong size of answers";
    }

    // bug
    @Test
    public void checkUrlIgnoringWithWrongWordExceptions() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_IGNORE_URL.getWrongWord())
                        .language(EN.lang)
                        .callApi());
        assert answers.size() > 0 : "expected number of answers is wrong";
        answers.forEach(answer -> {
            assert answer.code == ERROR_UNKNOWN_WORD.number : "Wrong error code";
            assert answer.s.contains(DATA_IGNORE_URL.getRightWord()) : "Wrong variants";
        });
    }


    @Test
    public void checkServiceExceptions() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(WORDS_WITH_RANDOMIZED_ALPHABET.getWrongWord())
                        .language(EN.lang)
                        .callApi());
        assert answers.size() > 0 : "expected number of answers is wrong.";
        answers.forEach(answer -> {
            assert answer.code == ERROR_UNKNOWN_WORD.number : "Wrong error code";
        });
    }

    //bug
    @Test(dataProvider = "repeat words with ignore capitalization")
    public void checkRepeatExceptionAndIgnoreCapitalozation(TestingValues testingValues) {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(testingValues.getWrongWord())
                        .language(EN.lang)
                        .options(Integer.toString(Integer.parseInt(FIND_REPEAT_WORDS.options) + Integer.parseInt(IGNORE_CAPITALIZATION.options)))
                        .callApi());
        assert answers.size() > 0 : "expected number of answers is wrong.";
        answers.forEach(answer -> {
            assert answer.s.contains(testingValues.getRightWord()) : "Wrong variant";
            assert answer.code == ERROR_REPEAT_WORD.number : "Wrong error code";
        });
    }

    //and again a bug
    @Test
    public void checkWrongCapitalizationExceptions() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(DATA_WRONG_CAPITALIZATION.getWrongWord())
                        .language(RU.lang)
                        .callApi());
        assert answers.size() > 0 : "expected number of answers is wrong.";
        answers.forEach(answer -> {
            assert answer.code == ERROR_CAPITALIZATION.number : "Wrong error code";
        });
    }


}