package entity;

import java.util.Arrays;
import java.util.List;

public enum CheckTextsDataa {

    TEXTS_WITH_MISTAKES(new String[]{"у нас была праблема","мы не могли найти выхад"}, Arrays.asList("проблема","выход"));

    public String[] texts;
    public List<String> rightWords;

    CheckTextsDataa(String[] texts, List<String> rightWords ){

        this.texts = texts;
        this.rightWords = rightWords;

    }

}
