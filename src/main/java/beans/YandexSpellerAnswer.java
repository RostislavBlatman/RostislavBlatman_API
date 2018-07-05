package beans;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class YandexSpellerAnswer {

    @Expose
    public Integer code;
    @Expose
    public Integer pos;
    @Expose
    public Integer row;
    @Expose
    public Integer col;
    @Expose
    public Integer len;
    @Expose
    public String word;
    @Expose
    public List<String> s = new ArrayList<String>();
}
