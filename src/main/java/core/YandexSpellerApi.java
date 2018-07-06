package core;

import beans.YandexSpellerAnswer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.LanguageEnum;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static enums.ParametersEnum.*;

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


    public static class ApiBuilder {
        YandexSpellerApi spellerApi;

        private ApiBuilder(YandexSpellerApi gcApi) {
            spellerApi = gcApi;
        }

        public ApiBuilder text(String text) {
            spellerApi.params.put(TEXT.param, text);
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

    }
}
