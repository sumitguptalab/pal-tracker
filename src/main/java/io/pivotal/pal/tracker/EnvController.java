package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {


    private Map<String,String> envMap;

    public EnvController(
            @Value("${PORT:NOT SET}")
            String port,
            @Value("${MEMORY_LIMIT:NOT SET}")
            String mem_limit,
            @Value("${CF_INSTANCE_INDEX:NOT SET}")
            String index,
            @Value("${CF_INSTANCE_ADDR:NOT SET}")
            String addr) {

        envMap = new HashMap<String,String>();
        envMap.put("PORT", port);
        envMap.put("MEMORY_LIMIT",mem_limit);
        envMap.put("CF_INSTANCE_INDEX", index);
        envMap.put("CF_INSTANCE_ADDR", addr);
    }

    @GetMapping("/env")
    public Map<String,String> getEnv() {
        return this.envMap;
    }
}
