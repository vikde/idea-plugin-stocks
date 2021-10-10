package com.github.vikde.idea.plugin.stocks.quote;

import com.github.vikde.idea.plugin.stocks.common.Stock;
import com.github.vikde.idea.plugin.stocks.config.StoredStock;

import java.util.List;

/**
 * 行情提供者
 *
 * @author vikde
 * @date 2021/01/15
 */
public abstract class AbstractQuoteProvider {
    /**
     * 名称
     */
    private final String name;

    protected AbstractQuoteProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 加载行情
     *
     * @param storedStockList 储存的股票列表
     * @return 需要显示的行情
     */
    public abstract List<Stock> load(List<StoredStock> storedStockList);
}
