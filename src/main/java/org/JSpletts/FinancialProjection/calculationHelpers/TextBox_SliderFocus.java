package org.JSpletts.FinancialProjection.calculationHelpers;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextBox_SliderFocus implements FocusListener {

    JSlider s;
    JTextField t;
    String textboxValue;
    int maxSliderValue;

    public TextBox_SliderFocus(JTextField t, JSlider s,int maxSliderValue){
        this.s = s;
        this.t = t;
        this.maxSliderValue = maxSliderValue;

    }
    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        textboxValue = this.t.getText();
        if(textboxValue.substring(textboxValue.length()-1).equals("%")){
            String value2 = textboxValue.substring(0,textboxValue.length()-1);
            if(isDouble(value2)){
                double double_value = Double.valueOf(value2);
                if(double_value > maxSliderValue / 100)
                    double_value = maxSliderValue / 100;
                if(double_value < 0)
                    double_value =0;
                int slider_value = (int) Math.round(double_value * 100);
                s.setValue(slider_value);
                t.setText(String.valueOf(s.getValue() / 100.0) + "%");

            }
        }
        else if(isDouble(textboxValue)){
            double double_value = Double.valueOf(textboxValue);
            if(double_value > maxSliderValue / 100)
                double_value = maxSliderValue / 100;
            if(double_value < 0)
                double_value =0;
            int slider_value = (int) Math.round(double_value * 100);
            s.setValue(slider_value);
            t.setText(String.valueOf(s.getValue() / 100.0) + "%");

        }
    }

    public static boolean isDouble(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
