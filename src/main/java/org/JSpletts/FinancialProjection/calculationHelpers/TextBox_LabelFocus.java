package org.JSpletts.FinancialProjection.calculationHelpers;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextBox_LabelFocus implements FocusListener {
    JLabel l;
    JTextField t;
    String textboxValue;
    public TextBox_LabelFocus(JTextField t, JLabel l){
        this.l = l;
        this.t = t;
    }
    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        textboxValue = this.t.getText();
        if(!textboxValue.equals(""))
            l.setText(textboxValue);
    }

}
