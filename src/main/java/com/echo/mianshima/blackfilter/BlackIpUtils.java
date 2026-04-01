package com.echo.mianshima.blackfilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

@Slf4j
public class BlackIpUtils {
    private static BitMapBloomFilter bitMapBloomFilter;

    // 判断 ip 是否在黑名单中
    public static boolean isBlackIp(String ip){
        return bitMapBloomFilter.contains(ip);
    }

    // 重建 ip 黑名单
    public static void rebuildBlackIp(String configInfo){
        if(StrUtil.isBlank(configInfo)){
            configInfo = "{}";
        }
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        List<String> blackIpList = (List<String>) map.get("blackIpList");

        if(CollUtil.isNotEmpty(blackIpList)){
            BitMapBloomFilter bloomFilter = new BitMapBloomFilter(958506);
            for (String blackIp : blackIpList) {
                bloomFilter.add(blackIp);
            }

            bitMapBloomFilter = bloomFilter;
        }else{
            bitMapBloomFilter = new BitMapBloomFilter(100);
        }
    }
}
