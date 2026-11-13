package com.ggzed.agent.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WeatherTool {

    private static final Logger log = LoggerFactory.getLogger(WeatherTool.class);

    @Tool("获取指定城市的天气")
    public String getWeather(@P("城市名，如：成都") String city) {
        log.info("[TOOL CALLED] {}", city);
        return "city:"+city+","+"weather:"+"26°";
    }
}
