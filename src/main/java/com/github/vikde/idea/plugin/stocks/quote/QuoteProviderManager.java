package com.github.vikde.idea.plugin.stocks.quote;

import com.github.vikde.idea.plugin.stocks.config.StoredConfig;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vikde
 * @date 2021/01/15
 */
public final class QuoteProviderManager {
    private QuoteProviderManager() {
    }

    private static final List<AbstractQuoteProvider> QUOTE_PROVIDER_LIST =
            Arrays.asList(new SinaQuoteProvider(),
                          new TencentQuoteProvider());

    /**
     * 获取行情适配器
     */
    public static AbstractQuoteProvider getQuoteProvider(StoredConfig storedConfig) {
        for (AbstractQuoteProvider provider : QUOTE_PROVIDER_LIST) {
            if (provider.getName().equals(storedConfig.getQuoteProviderName())) {
                return provider;
            }
        }
        return QUOTE_PROVIDER_LIST.get(0);
    }

    /**
     * 获取行情名称列表
     */
    public static List<String> getQuoteProviderNameList() {
        return QUOTE_PROVIDER_LIST.stream()
                .map(AbstractQuoteProvider::getName)
                .collect(Collectors.toList());
    }
}
