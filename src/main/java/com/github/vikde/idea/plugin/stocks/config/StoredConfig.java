package com.github.vikde.idea.plugin.stocks.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vikde
 * @date 2021/01/15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoredConfig {
    /**
     * 简单版或复杂版
     */
    private boolean isSimple = true;
    /**
     * 配置的行情数据源
     */
    private String quoteProviderName;
    /**
     * 存储的股票
     */
    private List<StoredStock> stockList = new ArrayList<>();

    public boolean getIsSimple() {
        return isSimple;
    }

    public void setIsSimple(boolean isSimple) {
        this.isSimple = isSimple;
    }

    public String getQuoteProviderName() {
        return quoteProviderName;
    }

    public void setQuoteProviderName(String quoteProviderName) {
        this.quoteProviderName = quoteProviderName;
    }

    public List<StoredStock> getStockList() {
        return stockList;
    }

    public void setStockList(List<StoredStock> stockList) {
        this.stockList = stockList;
    }
}
