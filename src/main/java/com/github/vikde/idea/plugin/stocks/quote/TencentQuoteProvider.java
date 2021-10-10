package com.github.vikde.idea.plugin.stocks.quote;

import com.github.vikde.idea.plugin.stocks.common.MarketType;
import com.github.vikde.idea.plugin.stocks.common.Stock;
import com.github.vikde.idea.plugin.stocks.common.util.HttpUtil;
import com.github.vikde.idea.plugin.stocks.config.StoredStock;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vikde
 * @date 2021/01/15
 */
public class TencentQuoteProvider extends AbstractQuoteProvider {
    public TencentQuoteProvider() {
        super("腾讯");
    }

    @Override
    public List<Stock> load(List<StoredStock> storedStockList) {
        List<Stock> stockList = new LinkedList<>();
        try {
            //请求参数转换
            String queryStr = "r=" + RandomUtils.nextDouble() + "q=" + storedStockList.stream().map(stock -> {
                if (stock.getMarketType() == MarketType.SZ) {
                    return "s_sz" + stock.getCode();
                } else {
                    return "s_sh" + stock.getCode();
                }
            }).collect(Collectors.joining(","));
            //请求
            String datas = HttpUtil.get("http://qt.gtimg.cn/" + queryStr);
            //v_s_sz000559="51~万向钱潮~000559~5.27~-0.07~-1.31~108754~5752~~174.11~GP-A"; v_s_sz000913="51~钱江摩托~000913~25.31~-1.25~-4.71~30683~7967~~114.79~GP-A";
            int order = 1;
            for (String data : datas.split(";")) {
                String[] strings = data.split("~");
                String code = strings[2];
                String name = strings[1];
                BigDecimal lastPrice = new BigDecimal(strings[3]).setScale(10, RoundingMode.HALF_UP);
                BigDecimal changePrice = new BigDecimal(strings[4]).setScale(10, RoundingMode.HALF_UP);
                BigDecimal previousClosePrice = lastPrice.subtract(changePrice);
                Stock stock = new Stock(order++, code, name, lastPrice, previousClosePrice);
                stockList.add(stock);
            }
        } catch (Exception exception) {
        }
        return stockList;
    }
}
