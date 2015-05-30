package com.ymarq.eu.common;

/**
 * Created by eu on 12/30/2014.
 */
public class DataUrlContent {

    public String Url;
    public Object Content;
    public ResultArrayTypes ExpectedArrayResult;

    public Object getContent() {
        return Content;
    }

    public void setContent(Object content) {
        Content = content;
    }

    public ResultArrayTypes getExpectedArrayResult() {
        return ExpectedArrayResult;
    }

    public void setExpectedArrayResult(ResultArrayTypes expectedArrayResult) {
        ExpectedArrayResult = expectedArrayResult;
    }

    public DataUrlContent(String url, Object content) {
        Url = url;
        Content = content;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
