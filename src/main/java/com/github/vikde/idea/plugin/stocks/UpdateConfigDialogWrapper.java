package com.github.vikde.idea.plugin.stocks;

import com.github.vikde.idea.plugin.stocks.config.StoredConfig;
import com.github.vikde.idea.plugin.stocks.config.StoredConfigManager;
import com.github.vikde.idea.plugin.stocks.quote.QuoteProviderManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.panels.VerticalLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author vikde
 * @date 2021/01/15
 */
public class UpdateConfigDialogWrapper extends DialogWrapper {
    private JCheckBox isSimpleCheckBox;
    private ComboBox<String> quoteAdapterComboBox;

    public UpdateConfigDialogWrapper() {
        super(true);
        init();
        setTitle("设置");
    }

    /**
     * 创建视图
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new VerticalLayout(0, SwingConstants.LEFT));

        StoredConfig storedConfig = StoredConfigManager.getStoredConfig();

        //简单版或复杂版
        JLabel isSimpleLabel = new JLabel("是否简单版:");
        isSimpleCheckBox = new JCheckBox("是");
        isSimpleCheckBox.setSelected(storedConfig.getIsSimple());
        JPanel isSimplePanel = new JPanel();
        isSimplePanel.add(isSimpleLabel);
        isSimplePanel.add(isSimpleCheckBox);
        mainPanel.add(isSimplePanel, VerticalLayout.TOP);

        //配置的行情数据源
        JLabel quoteAdapterLabel = new JLabel("行情数据源:");
        DefaultComboBoxModel<String> quoteAdapterModel = new DefaultComboBoxModel<>();
        for (String quoteAdapterName : QuoteProviderManager.getQuoteProviderNameList()) {
            quoteAdapterModel.addElement(quoteAdapterName);
        }
        quoteAdapterComboBox = new ComboBox<>(quoteAdapterModel);
        quoteAdapterComboBox.setSelectedItem(storedConfig.getQuoteProviderName());
        JPanel quoteAdapterPanel = new JPanel();
        quoteAdapterPanel.add(quoteAdapterLabel);
        quoteAdapterPanel.add(quoteAdapterComboBox);
        mainPanel.add(quoteAdapterPanel, VerticalLayout.TOP);

        return mainPanel;
    }

    /**
     * 校验数据
     */
    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return null;
    }

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        DialogWrapperAction okAction = new DialogWrapperAction("修改") {
            @Override
            protected void doAction(ActionEvent e) {
                StoredConfig storedConfig = StoredConfigManager.getStoredConfig();
                storedConfig.setIsSimple(isSimpleCheckBox.isSelected());
                storedConfig.setQuoteProviderName((String) quoteAdapterComboBox.getSelectedItem());
                StoredConfigManager.updateStoredConfig(storedConfig);
                close(OK_EXIT_CODE);
            }
        };
        //默认获得焦点事件
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{okAction};
    }
}
