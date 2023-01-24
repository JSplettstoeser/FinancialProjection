package org.JSpletts.FinancialProjection.calculationHelpers;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DecimalSliderChangeListener_NoDecimal implements ChangeListener {

    JSlider s;
    JTextField t;
    public DecimalSliderChangeListener_NoDecimal(JTextField t, JSlider s){
        this.s = s;
        this.t = t;
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        int value = s.getValue();
        t.setText(String.valueOf(value) + "%");
    }
}
