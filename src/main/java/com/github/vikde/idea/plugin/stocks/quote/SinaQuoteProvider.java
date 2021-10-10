package com.github.vikde.idea.plugin.stocks.quote;

import com.github.vikde.idea.plugin.stocks.common.util.HttpUtil;
import com.github.vikde.idea.plugin.stocks.common.MarketType;
import com.github.vikde.idea.plugin.stocks.common.Stock;
import com.github.vikde.idea.plugin.stocks.config.StoredStock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangwenqiang
 * @date 2020/12/30
 */
public class SinaQuoteProvider extends AbstractQuoteProvider {
    public SinaQuoteProvider() {
        super("新浪");
    }

    @Override
    public List<Stock> load(List<StoredStock> storedStockList) {
        List<Stock> stockList = new LinkedList<>();
        try {
            //请求参数转换
            String queryStr = storedStockList.stream().map(stock -> {
                if (stock.getMarketType() == MarketType.SZ) {
                    return "sz" + stock.getCode();
                } else {
                    return "sh" + stock.getCode();
                }
            }).collect(Collectors.joining(","));
            //请求
            String datas = HttpUtil.get("http://hq.sinajs.cn/list=" + queryStr);
            //var hq_str_sh600389="江山股份,21.550,21.650,21.650,21.950,21.360,21.650,21.680,1769450,38371044.000,28100,21.650,4800,21.640,4000,21.630,30000,21.610,28899,21.600,3900,21.680,2500,21.690,40300,21.700,100,21.710,2100,21.720,2020-12-25,15:00:03,00,";
            //var hq_str_sz300059="东方财富,26.380,26.530,26.830,27.150,26.040,26.830,26.840,207657292,5547026838.470,499837,26.830,593900,26.820,329700,26.810,213264,26.800,136100,26.790,128200,26.840,201600,26.850,92200,26.860,40800,26.870,106400,26.880,2020-12-25,15:36:00,00,D|7200|193176.000";
            datas = datas.replace("var hq_str_sh", "").replace("var hq_str_sz", "").replace("=\"", ",");
            int order = 1;
            for (String data : datas.split("\n")) {
                String[] strings = data.split(",");
                String code = strings[0];
                String name = strings[1];
                BigDecimal previousClosePrice = new BigDecimal(strings[3]).setScale(10, RoundingMode.HALF_UP);
                BigDecimal lastPrice = new BigDecimal(strings[4]).setScale(10, RoundingMode.HALF_UP);

                Stock stock = new Stock(order++, code, name, lastPrice, previousClosePrice);
                stockList.add(stock);
            }
        } catch (Exception exception) {
        }
        return stockList;
    }
}
