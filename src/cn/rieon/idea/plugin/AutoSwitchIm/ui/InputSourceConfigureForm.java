package cn.rieon.idea.plugin.AutoSwitchIm.ui;

import cn.rieon.idea.plugin.AutoSwitchIm.provider.ConfigurationProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rieon Ke <rieon@rieon.cn>
 * @version 1.0.0
 * @since 2017/6/9
 */
class InputSourceConfigureForm {

    private static final Logger LOG = Logger.getInstance(InputSourceConfigureForm.class);

    private JComboBox inputSourceCombo;
    private JPanel rootPanel;
    private ConfigurationProvider configurationProvider;
    private ArrayList<Pair<String, String>> sources;
    private boolean isComboBoxModified = false;

    JPanel createPanel(ConfigurationProvider provider, ArrayList<Pair<String, String>> sources) {

        configurationProvider = provider;
        this.sources = sources;

        setUpPanel(sources);

        return rootPanel;
    }

    boolean isModified(){

        return isComboBoxModified;

    }

    void apply() {


        String x = String.valueOf(inputSourceCombo.getSelectedItem());
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(x);

        if (m.find()){
            String selected = m.group(1);
            boolean selectedExists = false;
            for (Pair<String, String> pair : sources) {

                if (pair.getKey().equals(selected)){
                    selectedExists = true;
                }

            }

            if (selectedExists) {
                configurationProvider.setSelectedInputSource(selected);
                isComboBoxModified = false;
                LOG.info("NEW CONFIG APPLIED");
            }else {
                Messages.showMessageDialog( selected + "Not Found!", "ERROR", null);
            }

        }else {
            Messages.showMessageDialog( "Selected Input Source Not Found!", "ERROR", null);
        }

    }

    private void setUpPanel(ArrayList<Pair<String, String>> sources) {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        JLabel switchToLabel = new JLabel();
        switchToLabel.setText("Switch To");
        rootPanel.add(switchToLabel, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        rootPanel.add(spacer1, new GridConstraints(3, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        rootPanel.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        inputSourceCombo = new JComboBox();

        inputSourceCombo.addActionListener (new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                isComboBoxModified = true;
            }

        });

        sources.forEach( pair -> {
            String item = pair.getValue() + "(" + pair.getKey() + ")";

            inputSourceCombo.addItem(item);

            if (Objects.equals(configurationProvider.getSelectedInputSource(), pair.getKey())){
                inputSourceCombo.setSelectedItem(item);
            }
        });

        if (configurationProvider.getSelectedInputSource() == null) {
            inputSourceCombo.setSelectedItem("ABC(com.apple.keylayout.ABC)");
        }

        rootPanel.add(inputSourceCombo, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("<html>Switch to the specified input resource while state changed,<br> ABC(com.apple.keylayout.ABC) is recommend.<br>Notice that some of the input sources are invalid!<html>");
        rootPanel.add(label1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

}
