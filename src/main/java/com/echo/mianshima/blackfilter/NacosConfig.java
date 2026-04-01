package com.echo.mianshima.blackfilter;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class NacosConfig implements InitializingBean {
    @NacosInjected
    private ConfigService configService;

    @Value("${nacos.config.data-id}")
    private String dataId;

    @Value("${nacos.config.group}")
    private String group;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("监听器再次启动");
        String config = null;
        try {
            config = configService.getConfigAndSignListener(dataId, group, 3000L, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String s) {
                    log.info("监听到配置信息变化，{}", s);
                    BlackIpUtils.rebuildBlackIp(s);
                }
            });
        } catch (NacosException e) {
            log.error("拉取配置失败");
        }

        BlackIpUtils.rebuildBlackIp(config);
    }
}
