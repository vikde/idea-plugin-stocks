package com.github.vikde.idea.plugin.stocks;

import com.github.vikde.idea.plugin.stocks.common.Stock;
import com.github.vikde.idea.plugin.stocks.config.StoredConfig;
import com.github.vikde.idea.plugin.stocks.config.StoredConfigManager;
import com.github.vikde.idea.plugin.stocks.quote.AbstractQuoteProvider;
import com.github.vikde.idea.plugin.stocks.quote.QuoteProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vikde
 * @date 2021/01/15
 */
public class StocksToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //按钮栏
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.setPreferredSize(new Dimension(45, 30));
        JButton addBtn = new JButton("新增");
        addBtn.setPreferredSize(new Dimension(45, 30));
        JButton deleteBtn = new JButton("删除");
        deleteBtn.setPreferredSize(new Dimension(45, 30));
        JButton upBtn = new JButton("上移");
        upBtn.setPreferredSize(new Dimension(45, 30));
        JButton downBtn = new JButton("下移");
        downBtn.setPreferredSize(new Dimension(45, 30));
        JButton configBtn = new JButton("设置");
        configBtn.setPreferredSize(new Dimension(45, 30));
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new HorizontalLayout(5, SwingConstants.CENTER));
        btnPanel.add(refreshBtn, HorizontalLayout.LEFT);
        btnPanel.add(addBtn, HorizontalLayout.LEFT);
        btnPanel.add(deleteBtn, HorizontalLayout.LEFT);
        btnPanel.add(upBtn, HorizontalLayout.LEFT);
        btnPanel.add(downBtn, HorizontalLayout.LEFT);
        btnPanel.add(configBtn, HorizontalLayout.LEFT);
        btnPanel.setBounds(5, 5, 300, 30);
        //数据表格栏
        TableView<Stock> tableView = new TableView<>();
        JBScrollPane jbScrollPane = new JBScrollPane(tableView);
        jbScrollPane.setBounds(0, 40, 1, 1);
        //主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.add(btnPanel);
        mainPanel.add(jbScrollPane);
        //主面板大小变动事件
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                Dimension dimension = mainPanel.getSize();
                jbScrollPane.setSize(new Dimension(dimension.width, Math.max(dimension.height - 40, 0)));
            }
        });
        //新增事件
        addBtn.addActionListener(e -> {
            boolean isOk = new AddStockDialogWrapper().showAndGet();
            if (isOk) {
                updateData(tableView);
            }
        });
        //删除事件
        deleteBtn.addActionListener(e -> {
            List<Stock> stockList = tableView.getSelectedObjects();
            if (CollectionUtils.isEmpty(stockList)) {
                Messages.showMessageDialog("选择需要删除的股票", "删除异常", Messages.getErrorIcon());
            } else {
                String str = stockList.stream().map(Stock::getName).collect(Collectors.joining(","));
                int status = Messages.showOkCancelDialog(str, "是否删除自选股?", "是", "否", Messages.getWarningIcon());
                if (status == Messages.OK) {
                    StoredConfigManager.deleteStoredStock(stockList.stream().map(Stock::getCode).collect(Collectors.toList()));
                    updateData(tableView);
                }
            }
        });

        //事件注册
        refreshBtn.addActionListener(e -> updateData(tableView));

        //事件注册
        upBtn.addActionListener(e -> {
            List<Stock> stockList = tableView.getSelectedObjects();
            if (CollectionUtils.isEmpty(stockList)) {
                Messages.showMessageDialog("选择需要上移的股票", "异常", Messages.getErrorIcon());
            } else if (stockList.size() > 1) {
                Messages.showMessageDialog("每次只能选择一只股票", "异常", Messages.getErrorIcon());
            }
            Stock stock = stockList.stream().findFirst().orElse(null);
            StoredConfigManager.upStock(stock == null ? "" : stock.getCode());
            updateData(tableView);
        });

        //事件注册
        downBtn.addActionListener(e -> {
            List<Stock> stockList = tableView.getSelectedObjects();
            if (CollectionUtils.isEmpty(stockList)) {
                Messages.showMessageDialog("选择需要下移的股票", "异常", Messages.getErrorIcon());
            } else if (stockList.size() > 1) {
                Messages.showMessageDialog("每次只能选择一只股票", "异常", Messages.getErrorIcon());
            }
            Stock stock = stockList.stream().findFirst().orElse(null);
            StoredConfigManager.downStock(stock == null ? "" : stock.getCode());
            updateData(tableView);
        });
        //配置
        configBtn.addActionListener(e -> {
            boolean isOk = new UpdateConfigDialogWrapper().showAndGet();
            if (isOk) {
                updateData(tableView);
            }
        });

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "我的自选", false);
        toolWindow.getContentManager().addContent(content);
        updateData(tableView);
    }

    private void updateData(TableView<Stock> tableView) {
        StoredConfig storedConfig = StoredConfigManager.getStoredConfig();
        AbstractQuoteProvider quoteAdapter = QuoteProviderManager.getQuoteProvider(storedConfig);

        List<Stock> stockList = quoteAdapter.load(storedConfig.getStockList());

        ListTableModel<Stock> tableModel = new ListTableModel<>(StockColumnInfoGenerator.generate(storedConfig), stockList);
        tableView.setModelAndUpdateColumns(tableModel);
    }
}
