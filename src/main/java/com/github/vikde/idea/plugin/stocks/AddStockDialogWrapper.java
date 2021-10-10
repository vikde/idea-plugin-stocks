package com.github.vikde.idea.plugin.stocks;

import com.github.vikde.idea.plugin.stocks.common.MarketType;
import com.github.vikde.idea.plugin.stocks.config.StoredStock;
import com.github.vikde.idea.plugin.stocks.config.StoredConfigManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.panels.VerticalLayout;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author vikde
 * @date 2021/01/15
 */
public class AddStockDialogWrapper extends DialogWrapper {
    private final Pattern codePattern = Pattern.compile("^([0-9]{6})$");

    private JTextField codeTextField;
    private ButtonGroup marketButtonGroup;

    public AddStockDialogWrapper() {
        super(true);
        init();
        setTitle("请输入股票相关数据");
    }

    /**
     * 创建视图
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new VerticalLayout(0, SwingConstants.LEFT));

        JLabel codeLabel = new JLabel("代码:");
        codeTextField = new JTextField();
        codeTextField.setPreferredSize(new Dimension(150, 30));
        JPanel codePanel = new JPanel();
        codePanel.add(codeLabel);
        codePanel.add(codeTextField);
        mainPanel.add(codePanel, VerticalLayout.TOP);

        JLabel marketLabel = new JLabel("市场:");
        marketButtonGroup = new ButtonGroup();
        JRadioButton marketShRadioButton = new JRadioButton("SH", true);
        JRadioButton marketSzRadioButton = new JRadioButton("SZ");
        marketButtonGroup.add(marketShRadioButton);
        marketButtonGroup.add(marketSzRadioButton);
        JPanel marketPanel = new JPanel();
        marketPanel.add(marketLabel);
        marketPanel.add(marketShRadioButton);
        marketPanel.add(marketSzRadioButton);
        mainPanel.add(marketPanel, VerticalLayout.TOP);

        return mainPanel;
    }

    /**
     * 校验数据
     *
     * @return 通过必须返回null，不通过返回一个 ValidationInfo 信息
     */
    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String code = codeTextField.getText();
        if (StringUtils.isBlank(code)) {
            return new ValidationInfo("代码不能为空!");
        } else if (!codePattern.matcher(code).matches()) {
            return new ValidationInfo("代码格式错误!");
        }
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
        DialogWrapperAction okAction = new DialogWrapperAction("添加") {
            @Override
            protected void doAction(ActionEvent e) {
                // 点击ok的时候进行数据校验
                ValidationInfo validationInfo = doValidate();
                if (validationInfo != null) {
                    Messages.showMessageDialog(validationInfo.message, "校验不通过", Messages.getInformationIcon());
                } else {
                    String code = codeTextField.getText();
                    String market = "";
                    Enumeration<AbstractButton> marketButtonGroupElements = marketButtonGroup.getElements();
                    while (marketButtonGroupElements.hasMoreElements()) {
                        AbstractButton button = marketButtonGroupElements.nextElement();
                        if (button.isSelected()) {
                            market = button.getText();
                            break;
                        }
                    }
                    StoredStock stock = new StoredStock();
                    stock.setCode(code);
                    stock.setMarketType(MarketType.valueOf(market));
                    StoredConfigManager.addStoredStock(stock);
                    close(OK_EXIT_CODE);
                }
            }
        };
        //默认获得焦点事件
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{okAction};
    }
}
