package cn.rieon.idea.plugin.AutoSwitchIm.ui;

import cn.rieon.idea.plugin.AutoSwitchIm.provider.ConfigurationProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import kotlin.Pair;

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

    JComboBox OutOfIdeaCbx;
    JComboBox IdeaFocusedCbx;
    JComboBox InVimInsertCbx;
    JComboBox OutVimInsertCbx;
    private ConfigurationProvider configurationProvider;


    /**
     * source key => value
     *        input source id => input source name
     */
    private ArrayList<Pair<String, String>> sources;
    private boolean isComboBoxModified = false;
    private JPanel rootPanel;

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

        String ideaFocusedInput = String.valueOf(IdeaFocusedCbx.getSelectedItem());
        String outOfIdeaInput = String.valueOf(OutOfIdeaCbx.getSelectedItem());
        String inVimInsertInput = String.valueOf(InVimInsertCbx.getSelectedItem());
        String outVimInsertInput = String.valueOf(OutVimInsertCbx.getSelectedItem());

        String ideaFocusedValue = exactValueFromInput(ideaFocusedInput);
        String outOfIdeaValue = exactValueFromInput(outOfIdeaInput);
        String inVimInsertValue = exactValueFromInput(inVimInsertInput);
        String outVimInsertValue = exactValueFromInput(outVimInsertInput);

        if (ideaFocusedValue != null){
            configurationProvider.setInIdeaConfig(ideaFocusedValue);
        }

        if (outOfIdeaValue != null){
            configurationProvider.setOutOfIdeaConfig(outOfIdeaValue);
        }
        if (inVimInsertValue != null){
            configurationProvider.setInVimInsertConfig(inVimInsertValue);
        }
        if (outVimInsertValue != null){
            configurationProvider.setOutVimInsertConfig(outVimInsertValue);
        }

    }

    private String exactValueFromInput(String ideaFocusedValue) {
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(ideaFocusedValue);

        if (m.find()){
            String selected = m.group(1);
            boolean selectedExists = false;
            for (Pair<String, String> pair : sources) {

                if (pair.getFirst().equals(selected)){
                    selectedExists = true;
                    break;
                }

            }

            if (selectedExists) {
                isComboBoxModified = false;
                LOG.info("NEW CONFIG APPLIED");
                return selected;
            }else {
                Messages.showMessageDialog( selected + "Not Found!", "ERROR", null);
            }

        }else {
            Messages.showMessageDialog( "Selected Input Source Not Found!", "ERROR", null);
        }
        return null;
    }

    private void setUpPanel(ArrayList<Pair<String, String>> sources) {

        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("IDEA focused");
        label1.setToolTipText("Switch to the specified input source while IDEA get focused");
        rootPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        rootPanel.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        IdeaFocusedCbx = new JComboBox();
        OutOfIdeaCbx = new JComboBox();
        InVimInsertCbx = new JComboBox();
        OutVimInsertCbx = new JComboBox();

        sources.forEach( pair -> {
            String item = pair.getSecond() + "(" + pair.getFirst()   + ")";

            IdeaFocusedCbx.addItem(item);
            OutOfIdeaCbx.addItem(item);
            InVimInsertCbx.addItem(item);
            OutVimInsertCbx.addItem(item);

            if (Objects.equals(configurationProvider.getInIdeaConfig(), pair.getFirst())) {
                IdeaFocusedCbx.setSelectedItem(item);
            }
            if (Objects.equals(configurationProvider.getOutOfIdeaConfig(), pair.getFirst())) {
                OutOfIdeaCbx.setSelectedItem(item);
            }
            if (Objects.equals(configurationProvider.getInVimInsertConfig(), pair.getFirst())) {
                InVimInsertCbx.setSelectedItem(item);
            }
            if (Objects.equals(configurationProvider.getOutVimInsertConfig(), pair.getFirst())) {
                OutVimInsertCbx.setSelectedItem(item);
            }

        });

        if (configurationProvider.getInIdeaConfig() == null) {
            IdeaFocusedCbx.setSelectedItem("ABC(com.apple.keylayout.ABC)");
        }
        if (configurationProvider.getOutOfIdeaConfig() == null) {
            OutOfIdeaCbx.setSelectedItem("ABC(com.apple.keylayout.ABC)");
        }
        if (configurationProvider.getOutVimInsertConfig() == null) {
            OutVimInsertCbx.setSelectedItem("ABC(com.apple.keylayout.ABC)");
        }
        if (configurationProvider.getInVimInsertConfig() == null){
            InVimInsertCbx.setSelectedItem("ABC(com.apple.keylayout.ABC)");
        }

        IdeaFocusedCbx.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isComboBoxModified = true;
            }
        });

        OutOfIdeaCbx.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isComboBoxModified = true;
            }
        });
        InVimInsertCbx.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isComboBoxModified = true;
            }
        });
        OutVimInsertCbx.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isComboBoxModified = true;
            }
        });


        rootPanel.add(IdeaFocusedCbx, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Out of IDEA");
        label2.setToolTipText("Switch to the specified input source while IDEA lost focused");
        rootPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rootPanel.add(OutOfIdeaCbx, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Vim in insert mode");
        label3.setToolTipText("Switch to the specified input source while entering vim insert mode");
        rootPanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rootPanel.add(InVimInsertCbx, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Vim exit insert mode");
        label4.setToolTipText("Switch to the specified input source while exiting vim insert mode");
        rootPanel.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rootPanel.add(OutVimInsertCbx, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

    }

}
