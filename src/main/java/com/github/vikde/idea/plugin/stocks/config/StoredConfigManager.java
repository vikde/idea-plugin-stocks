package com.github.vikde.idea.plugin.stocks.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author vikde
 * @date 2021/01/15
 */
public final class StoredConfigManager {
    private StoredConfigManager() {
    }

    private static StoredConfig loadConfig() {
        String userHome = System.getProperties().get("user.home").toString();
        String fileName = userHome + "/.stocks/config.json";
        try {
            Files.createDirectories(Paths.get(userHome + "/.stocks/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            String configJsonStr = lines.collect(Collectors.joining("\n"));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(configJsonStr, StoredConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new StoredConfig();
    }

    private static void updateConfig(StoredConfig storedConfig) {
        String userHome = System.getProperties().get("user.home").toString();
        String fileName = userHome + "/.stocks/config.json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Files.write(Paths.get(fileName), objectMapper.writeValueAsBytes(storedConfig));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置
     */
    public static StoredConfig getStoredConfig() {
        StoredConfig storedConfig = loadConfig();
        if (storedConfig == null) {
            storedConfig = new StoredConfig();
        }
        return storedConfig;
    }

    /**
     * 更新配置
     */
    public static void updateStoredConfig(StoredConfig storedConfig) {
        if (storedConfig == null) {
            storedConfig = new StoredConfig();
        }
        updateConfig(storedConfig);
    }

    /**
     * 添加股票
     */
    public static void addStoredStock(StoredStock storedStock) {
        StoredConfig storedConfig = loadConfig();
        if (storedConfig == null) {
            storedConfig = new StoredConfig();
        }
        //删除重复的股票
        storedConfig.getStockList().removeIf(stock -> storedStock.getCode().equalsIgnoreCase(stock.getCode()));

        //将新加的股票放到前面
        List<StoredStock> storedStockList = new ArrayList<>();
        storedStockList.add(storedStock);
        storedStockList.addAll(storedConfig.getStockList());
        storedConfig.setStockList(storedStockList);
        updateConfig(storedConfig);
    }

    /**
     * 删除股票
     */
    public static void deleteStoredStock(List<String> stockCodeList) {
        StoredConfig storedConfig = loadConfig();
        if (storedConfig == null) {
            storedConfig = new StoredConfig();
        }
        //删除重复的股票
        storedConfig.getStockList().removeIf(stock -> stockCodeList.contains(stock.getCode()));
        updateConfig(storedConfig);
    }

    public static void upStock(String stockCode) {
        StoredConfig storedConfig = loadConfig();
        if (storedConfig == null) {
            storedConfig = new StoredConfig();
        }
        List<StoredStock> stockList = storedConfig.getStockList();
        int preIndex = -1;
        for (int i = 0; i < stockList.size(); i++) {
            if (stockCode.equals(stockList.get(i).getCode())) {
                preIndex = i;
                break;
            }
        }
        if (preIndex >= 1) {
            StoredStock preStoredStock = stockList.get(preIndex);
            stockList.set(preIndex, stockList.get(preIndex - 1));
            stockList.set(preIndex - 1, preStoredStock);
        }
        storedConfig.setStockList(stockList);
        updateConfig(storedConfig);
    }

    public static void downStock(String stockCode) {
        StoredConfig storedConfig = loadConfig();
        if (storedConfig == null) {
            storedConfig = new StoredConfig();
        }
        List<StoredStock> stockList = storedConfig.getStockList();
        int preIndex = -1;
        for (int i = 0; i < stockList.size(); i++) {
            if (stockCode.equals(stockList.get(i).getCode())) {
                preIndex = i;
                break;
            }
        }
        if (preIndex >= 0 && preIndex < stockList.size() - 1) {
            StoredStock preStoredStock = stockList.get(preIndex);
            stockList.set(preIndex, stockList.get(preIndex + 1));
            stockList.set(preIndex + 1, preStoredStock);
        }
        storedConfig.setStockList(stockList);
        updateConfig(storedConfig);
    }
}
