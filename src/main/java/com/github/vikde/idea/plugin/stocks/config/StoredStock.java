package com.github.vikde.idea.plugin.stocks.config;

import com.github.vikde.idea.plugin.stocks.common.MarketType;

/**
 * @author vikde
 * @date 2021/01/15
 */
public class StoredStock {
    /**
     * 代码
     */
    private String code;
    /**
     * 市场
     */
    private MarketType marketType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }
}
