package core;

import beans.YandexSpellerAnswer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.LanguageEnum;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import java.util.*;

import static enums.ParametersEnum.*;
import static org.hamcrest.Matchers.lessThan;

public class YandexSpellerApi {

    private HashMap<String, String> params = new HashMap<>();
    private List<String> texts = new ArrayList<>();

    public static ApiBuilder with() {
        YandexSpellerApi api = new YandexSpellerApi();
        return new ApiBuilder(api);
    }

    //get ready Speller answers list form api response
    public static List<YandexSpellerAnswer> getYandexSpellerAnswers(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<YandexSpellerAnswer>>() {
        }.getType());
    }

    public static List<List<YandexSpellerAnswer>> getYandexSpellerAnswersCheckTexts(Response response){
        return new Gson().fromJson( response.asString().trim(), new TypeToken<List<List<YandexSpellerAnswer>>>() {}.getType());
    }

    //set base request and response specifications tu use in tests
    public static ResponseSpecification successResponse(){
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static RequestSpecification baseRequestConfiguration(){
        return new RequestSpecBuilder()
                .setAccept(ContentType.XML)
                .setRelaxedHTTPSValidation()
                .addHeader("My Header", "header.value")
                .addQueryParam("requestID", new Random().nextLong())
                .setBaseUri(YANDEX_SPELLER_API_URI.param)
                .build();
    }


    public static class ApiBuilder {
        YandexSpellerApi spellerApi;

        private ApiBuilder(YandexSpellerApi gcApi) {
            spellerApi = gcApi;
        }

        public ApiBuilder text(String text) {
            spellerApi.params.put(TEXT.param, text);
            return this;
        }

        public ApiBuilder texts(String[] texts) {
            spellerApi.texts.addAll(Arrays.asList(texts));
            return this;
        }

        public ApiBuilder options(String options) {
            spellerApi.params.put(OPTIONS.param, options);
            return this;
        }

        public ApiBuilder language(LanguageEnum language) {
            spellerApi.params.put(LANG.param, language.lang);
            return this;
        }

        public Response callApi() {
            return RestAssured.with()
                    .queryParams(spellerApi.params)
                    .log().all()
                    .get(YANDEX_SPELLER_API_URI.param).prettyPeek();
        }

        public Response callCheckTexts() {
            return RestAssured.with()
                    .param(TEXT.param, spellerApi.texts)
                    .queryParams(spellerApi.params)
                    .log().all()
                    .get(YANDEX_SPELLER_CHECK_TEXTS_URI.param).prettyPeek();
        }

    }
}
