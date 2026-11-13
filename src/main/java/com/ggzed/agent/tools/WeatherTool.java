package com.ggzed.agent.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class WeatherTool {

    @Tool("获取指定城市的天气")
    public String getWeather(@P String city) {
        return "city:"+city+","+"weather:"+"26°";
    }
}
