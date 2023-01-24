package org.JSpletts.FinancialProjection.calculationHelpers;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DecimalSliderChangeListener implements ChangeListener {

    JSlider s;
    JTextField t;
    public DecimalSliderChangeListener(JTextField t, JSlider s){
        this.s = s;
        this.t = t;
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        int value = s.getValue();
        t.setText(String.valueOf(value/100.0) + "%");
    }
}
