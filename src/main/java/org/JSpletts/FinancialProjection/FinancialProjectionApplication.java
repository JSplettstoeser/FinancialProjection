package org.JSpletts.FinancialProjection;

import org.JSpletts.FinancialProjection.calculationHelpers.*;
import org.JSpletts.FinancialProjection.mainProjection.FinancialData;
import org.JSpletts.FinancialProjection.mainProjection.Person;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

public class FinancialProjectionApplication {
    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {


        HashMap<String,String> Person1Assum = new HashMap<String, String>();
        HashMap<String,String> Person1AVs = new HashMap<String, String>();
        HashMap<String,String> Person2Assum = new HashMap<String, String>();
        HashMap<String,String> Person2AVs = new HashMap<String, String>();
        HashMap<String,String> JointAssum = new HashMap<String, String>();
        HashMap<String,String> JointAVs = new HashMap<String, String>();
        HashMap<String,String> InvestmentAssum = new HashMap<String, String>();
        HashMap<String,String> SSAssum = new HashMap<String, String>();
        ArrayList<String[]> AccountTypes = new ArrayList<String[]>();
        HashMap<String,String> AllAssum_UI = new HashMap<String, String>();



        HashMap<String,String> GeneralAssumptions = new HashMap<String, String>();

        AssumptionReader ar = new AssumptionReader();
        ArrayList<Person> pList = new ArrayList<Person>();

        ar.load(Person1Assum, "Data/Joe_Assum.txt");
        ar.load(Person1AVs, "Data/Joe_AV.txt");
        ar.load(Person2Assum, "Data/Britt_Assum.txt");
        ar.load(Person2AVs, "Data/Britt_AV.txt");
        ar.load(JointAssum, "Data/Joint_Assum.txt");
        ar.load(JointAVs, "Data/Joint_AV.txt");
        ar.load(GeneralAssumptions, "Data/General_Assum.txt");
        ar.load(InvestmentAssum, "Data/InvestmentAssum.txt");
        ar.load(SSAssum, "Data/SS_Assum.txt");
        ar.loadStringArray(AccountTypes, "Data/Account_Types.txt");



        ArrayList<double[]> Person1RetirementAssum = new ArrayList<double[]>();
        ArrayList<double[]> Person2RetirementAssum = new ArrayList<double[]>();

        ar.loadDoubleArray(Person1RetirementAssum, "Data/Joe_RetirementAssum.csv");
        ar.loadDoubleArray(Person2RetirementAssum, "Data/Britt_RetirementAssum.csv");

        int projectionYears = Integer.valueOf(GeneralAssumptions.get("ProjectionYears"));
        double taxBracketInflation = Double.valueOf(GeneralAssumptions.get("TaxBracketInflation"));

        Person p1 = new Person(Person1Assum, Person1AVs, GeneralAssumptions, Person1RetirementAssum,SSAssum);
        Person p2 = new Person(Person2Assum, Person2AVs, GeneralAssumptions, Person2RetirementAssum,SSAssum);
        pList.add(p1);
        pList.add(p2);

        Person[] pList2 = new Person[pList.size()];
        pList2[0] = p1;
        pList2[1] = p2;

        FinancialData fd = new FinancialData(JointAssum, JointAVs ,GeneralAssumptions,InvestmentAssum,pList2);
        fd.writeJointData();
        fd.writePerson1AVs();
        fd.writeAVs();

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Finances Unchained");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame("Finances Unchained");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        JComponent panel_GeneralAssum = new JPanel(new GridBagLayout());
        JComponent panel_AssumMain = new JPanel(new GridBagLayout());
        JComponent panel_AssumSpouse = new JPanel(new GridBagLayout());
        JComponent panel_AssumAV = new JPanel(new GridBagLayout());
        JComponent panel_Housing = new JPanel(new GridBagLayout());
        JComponent panel_InvestmentReturns = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        ArrayList<JComboBox> input_comboBoxes = new ArrayList<JComboBox>();
        ArrayList<JSlider> input_sliders = new ArrayList<JSlider>();
        ArrayList<JTextField> input_textField = new ArrayList<JTextField>();
        ArrayList<JComponent> input_values = new ArrayList<JComponent>();

        HashMap<JComponent, String> input_mapping = new HashMap<JComponent, String>();


        /*
        General Assumption Panel
        */

        //Create GUI objects
        JLabel generalAssum_CurrentYr, generalAssum_ProjYrs, generalAssum_InflExpense,generalAssum_InflTaxBrackets,generalAssum_RetirementAssumptionLabel,
                generalAssum_HealthCareAssumptions,generalAssum_HealthCare_Inflation,generalAssum_HealthCare_PostRetirementJump,generalAssum_HealthCare_MedicareAge,generalAssum_HealthCare_MedicareCostAdj,generalAssum_State;
        JComboBox generalAssum_CurrentYr_Response,generalAssum_ProjYrs_Response,generalAssum_HC_RetJump,generalAssum_HC_MedicareAge,generalAssum_HC_MedicareRed, generalAssum_State_Response;
        JTextField generalAssum_InflExpense_Response,generalAssum_InflTaxBrackets_Response,generalAssum_Ret_Infl,generalAssum_HC_Infl;
        JSlider generalAssum_InflExpense_Response_slider,generalAssum_InflTaxBrackets_Response_slider,generalAssum_Ret_Infl_slider,generalAssum_HC_Infl_slider;
        JLabel avAssum_mainLabel,avAssum_account_Label,avAssum_person1_Label,avAssum_person2_Label,avAssum_joint_Label;

        JButton generalAssum_nextButton;
        //Set Labels
        generalAssum_CurrentYr = new JLabel("Current Year:");
        generalAssum_ProjYrs = new JLabel("Projection Years:");
        generalAssum_State = new JLabel("State:");
        generalAssum_InflExpense = new JLabel("Expense Inflation:");
        generalAssum_InflTaxBrackets = new JLabel("Tax Bracket Inflation:");
        generalAssum_RetirementAssumptionLabel = new JLabel("Retirement Account Assumptions");
        generalAssum_HealthCareAssumptions = new JLabel("Health Care Assumptions");
        generalAssum_HealthCare_Inflation = new JLabel("Health Care Inflation:");
        generalAssum_HealthCare_PostRetirementJump = new JLabel("Employer Health Care Subsidy Percent:");
        generalAssum_HealthCare_MedicareAge = new JLabel("Medicare Start Age:");
        generalAssum_HealthCare_MedicareCostAdj = new JLabel("Medicare Subsidy Percent:");
        generalAssum_RetirementAssumptionLabel.setFont(new Font("LucidaGrande", Font.BOLD, 16));
        generalAssum_HealthCareAssumptions.setFont(new Font("LucidaGrande", Font.BOLD, 16));

        //Current Year
        String[] years = new String[100];
        for(int i = 0; i<years.length; i++){
            years[i] = ""+(2000 + i);
        }
        generalAssum_CurrentYr_Response = new JComboBox(years);
        generalAssum_CurrentYr_Response.setSelectedIndex(10);


        //Projection Years
        String[] projYears = new String[51];
        for(int i = 0; i<projYears.length; i++){
            projYears[i] = ""+(50+i);
        }
        generalAssum_ProjYrs_Response = new JComboBox(projYears);
        generalAssum_ProjYrs_Response.setSelectedIndex(25);

        //State
        generalAssum_State_Response = new JComboBox(getStates());
        generalAssum_State_Response.setSelectedIndex(22);

        //Expense Inflation
        Dictionary dict = new Hashtable();
        for (int i=0; i<=1000; i+=100) {
            dict.put(i, new JLabel(Double.toString(i / 100.0) + "%"));
        }

        generalAssum_InflExpense_Response = new JTextField(10);
        generalAssum_InflExpense_Response_slider = new JSlider(0, 1000, 200);

        setPreferredSliders(generalAssum_InflExpense_Response_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(generalAssum_InflExpense_Response,generalAssum_InflExpense_Response_slider,100.0);

        //Tax Bracket Inflation
        generalAssum_InflTaxBrackets_Response = new JTextField(10);
        generalAssum_InflTaxBrackets_Response_slider = new JSlider(0, 1000, 200);

        setPreferredSliders(generalAssum_InflTaxBrackets_Response_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(generalAssum_InflTaxBrackets_Response,generalAssum_InflTaxBrackets_Response_slider,100.0);
        
        //HealthCare Inflation
        generalAssum_HC_Infl = new JTextField(10);
        generalAssum_HC_Infl_slider = new JSlider(0, 1000, 400);

        setPreferredSliders(generalAssum_HC_Infl_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(generalAssum_HC_Infl,generalAssum_HC_Infl_slider,100.0);

        //Post Retirement HC Jump
        String[] jump = new String[41];
        for(int i = 0; i<jump.length; i++){
            jump[i] = ""+(i * 5) + "%";
        }
        generalAssum_HC_RetJump = new JComboBox(jump);
        generalAssum_HC_RetJump.setSelectedIndex(10);

        //Medicare Age
        String[] ages = new String[41];
        for(int i = 0; i<ages.length; i++){
            ages[i] = ""+(60+i);
        }
        generalAssum_HC_MedicareAge = new JComboBox(ages);
        generalAssum_HC_MedicareAge.setSelectedIndex(5);

        //Medicare HC Reduction
        String[] pcts = new String[101];
        for(int i = 0; i<pcts.length; i++){
            pcts[i] = "" + i + "%";
        }
        generalAssum_HC_MedicareRed = new JComboBox(pcts);
        generalAssum_HC_MedicareRed.setSelectedIndex(85);

        //Button
        generalAssum_nextButton = new JButton("Next Tab");

        Insets WEST_INSETS = new Insets(5, 0, 5, 5);
        Insets EAST_INSETS = new Insets(5, 5, 5, 0);

        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        //c.fill = GridBagConstraints.BOTH;
        c.insets = EAST_INSETS;

        int y_count =0;

        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_CurrentYr,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_ProjYrs,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_State,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_InflExpense,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_InflTaxBrackets,c);
        y_count++;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HealthCareAssumptions,c);
        y_count++;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HealthCare_Inflation,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HealthCare_PostRetirementJump,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HealthCare_MedicareAge,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HealthCare_MedicareCostAdj,c);


        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = EAST_INSETS;

        y_count = 0;
        int y_infl_exp, y_infl_tax, y_infl_retLim, y_infl_HC, y_infl_SS;

        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_CurrentYr_Response,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_ProjYrs_Response,c);
        y_count++;
        c.gridy =y_count;
        panel_GeneralAssum.add(generalAssum_State_Response,c);
        y_count++;
        c.gridy =y_count;
        panel_GeneralAssum.add(generalAssum_InflExpense_Response_slider,c);
        y_infl_exp = y_count;
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_InflTaxBrackets_Response_slider,c);
        y_infl_tax = y_count;

        y_count++;
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HC_Infl_slider,c);
        y_infl_HC   = y_count;
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HC_RetJump,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HC_MedicareAge,c);
        y_count++;
        c.gridy = y_count;
        panel_GeneralAssum.add(generalAssum_HC_MedicareRed,c);

        c.gridx = 2;

        c.gridy = y_infl_exp;
        panel_GeneralAssum.add(generalAssum_InflExpense_Response,c);
        c.gridy = y_infl_tax;
        panel_GeneralAssum.add(generalAssum_InflTaxBrackets_Response,c);
        c.gridy = y_infl_HC;
        panel_GeneralAssum.add(generalAssum_HC_Infl,c);

        tabbedPane.addTab("General Assumptions", panel_GeneralAssum);


        /*
        Add AV Pane
         */

        /*
        Person 1 Assumption Panel
        */
        //Create GUI objects
        JLabel mainAssum_Name, mainAssum_Age, mainAssum_PriorWorkingLabel, mainAssum_PriorWorkingYr,mainAssum_PriorWorkingAvg,mainAssum_currentSalary,
                mainAssum_currentSalaryInfl,mainAssum_HC,mainAssum_HCFreq, mainAssum_MonthlyExpenses,mainAssum_RetirementAge;
        JComboBox mainAssum_Age_Response,mainAssum_PriorWorkingYr_Response,mainAssum_HCFreq_Response,mainAssum_RetirementAge_Response;
        JTextField mainAssum_Name_Response,mainAssum_PriorWorkingAvg_Response,mainAssum_currentSalary_Response,mainAssum_currentSalaryInfl_response,mainAssum_HC_Response,mainAssum_MonthlyExpenses_Response;
        JSlider mainAssum_currentSalaryInfl_slider;


        //Set Labels
        mainAssum_Name = new JLabel("Name:");
        mainAssum_Age = new JLabel("Current Age:");
        mainAssum_PriorWorkingLabel = new JLabel("Social Security Projection Inputs");
        mainAssum_PriorWorkingYr = new JLabel("Prior Working Years:");
        mainAssum_PriorWorkingAvg = new JLabel("Average Annual Income in Prior Years:");
        mainAssum_currentSalary = new JLabel("Current Salary:");
        mainAssum_currentSalaryInfl = new JLabel("Salary Inflation:");
        mainAssum_HC = new JLabel("Health Insurance Cost:");
        mainAssum_HCFreq = new JLabel("Health Insurance Frequency");
        mainAssum_MonthlyExpenses = new JLabel("Monthly Personal Expenses:");
        mainAssum_RetirementAge = new JLabel("Retirement Age:");
        mainAssum_PriorWorkingLabel.setFont(new Font("LucidaGrande", Font.BOLD, 16));

        //Name
        mainAssum_Name_Response = new JTextField(10);


        //Current Age
        ages = new String[100];
        for(int i = 0; i<ages.length; i++){
            ages[i] = ""+(18+i);
        }
        mainAssum_Age_Response = new JComboBox(ages);
        mainAssum_Age_Response.setSelectedIndex(7);

        //Prior Working Avg
        mainAssum_PriorWorkingAvg_Response = new JTextField(10);
        mainAssum_PriorWorkingAvg_Response.setText("50000");

        //Prior Working Yrs
        years = new String[61];
        for(int i = 0; i<years.length; i++){
            years[i] = ""+(i);
        }
        mainAssum_PriorWorkingYr_Response = new JComboBox(years);
        mainAssum_PriorWorkingYr_Response.setSelectedIndex(5);

        //Salary
        mainAssum_currentSalary_Response = new JTextField(10);
        mainAssum_currentSalary_Response.setText("100000");

        //Salary Inflation
        mainAssum_currentSalaryInfl_response = new JTextField(10);
        mainAssum_currentSalaryInfl_slider = new JSlider(0, 1000, 200);

        setPreferredSliders(mainAssum_currentSalaryInfl_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(mainAssum_currentSalaryInfl_response,mainAssum_currentSalaryInfl_slider,100.0);


        //HC
        mainAssum_HC_Response = new JTextField(10);
        mainAssum_HC_Response.setText("180");

        //HC Frequency
        String[] freq = {"Annual","Monthly","Biweekly","Semi-Monthly","Weekly"};
        mainAssum_HCFreq_Response = new JComboBox(freq);
        mainAssum_HCFreq_Response.setSelectedIndex(2);

        //Expenses
        mainAssum_MonthlyExpenses_Response = new JTextField(10);
        mainAssum_MonthlyExpenses_Response.setText("1000");

        //Retirement Age
        ages = new String[60];
        for(int i = 0; i<ages.length; i++){
            ages[i] = ""+(30+i);
        }
        mainAssum_RetirementAge_Response = new JComboBox(ages);
        mainAssum_RetirementAge_Response.setSelectedIndex(25);

        y_count =0;
        int y_infl_sal = 0;
        
        c2.gridx = 0;
        c2.anchor = GridBagConstraints.EAST;
        //c2.fill = GridBagConstraints.BOTH;
        c2.insets = EAST_INSETS;

        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_Name,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_Age,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_RetirementAge,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_currentSalary,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_currentSalaryInfl,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_HC,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_HCFreq,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_MonthlyExpenses,c2);
        c2.gridwidth = 3;
        c2.anchor = GridBagConstraints.CENTER;
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_PriorWorkingLabel,c2);
        c2.gridwidth = 1;
        c2.anchor = GridBagConstraints.EAST;
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_PriorWorkingYr,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_PriorWorkingAvg,c2);
        
        c2.gridx = 1;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = EAST_INSETS;

        y_count = 0;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_Name_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_Age_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_RetirementAge_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_currentSalary_Response,c2);
        y_count++;
        c2.gridy = y_count;
        y_infl_sal = y_count;
        panel_AssumMain.add(mainAssum_currentSalaryInfl_slider,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_HC_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_HCFreq_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_MonthlyExpenses_Response,c2);
        y_count++;
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_PriorWorkingYr_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumMain.add(mainAssum_PriorWorkingAvg_Response,c2);

        c2.gridx = 2;
        c2.gridy = y_infl_sal;
        panel_AssumMain.add(mainAssum_currentSalaryInfl_response,c2);

        tabbedPane.addTab("Spouse 1 Assumptions", panel_AssumMain);



        /*
        Person 2 Assumption Panel
        */
        //Create GUI objects
        JLabel secAssum_Name, secAssum_Age, secAssum_PriorWorkingLabel, secAssum_PriorWorkingYr,secAssum_PriorWorkingAvg,secAssum_currentSalary,
                secAssum_currentSalaryInfl,secAssum_HC,secAssum_HCFreq, secAssum_MonthlyExpenses,secAssum_RetirementAge;
        JComboBox secAssum_Age_Response,secAssum_PriorWorkingYr_Response,secAssum_HCFreq_Response,secAssum_RetirementAge_Response;
        JTextField secAssum_Name_Response,secAssum_PriorWorkingAvg_Response,secAssum_currentSalary_Response,secAssum_currentSalaryInfl_response,secAssum_HC_Response,secAssum_MonthlyExpenses_Response;
        JSlider secAssum_currentSalaryInfl_slider;


        //Set Labels
        secAssum_Name = new JLabel("Name:");
        secAssum_Age = new JLabel("Current Age:");
        secAssum_PriorWorkingLabel = new JLabel("Social Security Projection Inputs");
        secAssum_PriorWorkingYr = new JLabel("Prior Working Years:");
        secAssum_PriorWorkingAvg = new JLabel("Average Annual Income in Prior Years:");
        secAssum_currentSalary = new JLabel("Current Salary:");
        secAssum_currentSalaryInfl = new JLabel("Salary Inflation:");
        secAssum_HC = new JLabel("Health Insurance Cost:");
        secAssum_HCFreq = new JLabel("Health Insurance Frequency");
        secAssum_MonthlyExpenses = new JLabel("Monthly Personal Expenses:");
        secAssum_RetirementAge = new JLabel("Retirement Age:");
        secAssum_PriorWorkingLabel.setFont(new Font("LucidaGrande", Font.BOLD, 16));

        //Name
        secAssum_Name_Response = new JTextField(10);


        //Current Age
        ages = new String[100];
        for(int i = 0; i<ages.length; i++){
            ages[i] = ""+(18+i);
        }
        secAssum_Age_Response = new JComboBox(ages);
        secAssum_Age_Response.setSelectedIndex(7);

        //Prior Working Avg
        secAssum_PriorWorkingAvg_Response = new JTextField(10);
        secAssum_PriorWorkingAvg_Response.setText("30000");

        //Prior Working Yrs
        years = new String[61];
        for(int i = 0; i<years.length; i++){
            years[i] = ""+(i);
        }
        secAssum_PriorWorkingYr_Response = new JComboBox(years);
        secAssum_PriorWorkingYr_Response.setSelectedIndex(5);

        //Salary
        secAssum_currentSalary_Response = new JTextField(10);
        secAssum_currentSalary_Response.setText("45000");

        //Salary Inflation
        secAssum_currentSalaryInfl_response = new JTextField(10);
        secAssum_currentSalaryInfl_slider = new JSlider(0, 1000, 200);

        setPreferredSliders(secAssum_currentSalaryInfl_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(secAssum_currentSalaryInfl_response,secAssum_currentSalaryInfl_slider,100.0);


        //HC
        secAssum_HC_Response = new JTextField(10);
        secAssum_HC_Response.setText("0");

        //HC Frequency
        secAssum_HCFreq_Response = new JComboBox(freq);
        secAssum_HCFreq_Response.setSelectedIndex(2);

        //Expenses
        secAssum_MonthlyExpenses_Response = new JTextField(10);
        secAssum_MonthlyExpenses_Response.setText("1000");

        //Retirement Age
        ages = new String[60];
        for(int i = 0; i<ages.length; i++){
            ages[i] = ""+(30+i);
        }
        secAssum_RetirementAge_Response = new JComboBox(ages);
        secAssum_RetirementAge_Response.setSelectedIndex(25);

        y_count =0;
        y_infl_sal = 0;

        c2.gridx = 0;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = 0;
        c2.insets = EAST_INSETS;

        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_Name,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_Age,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_RetirementAge,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_currentSalary,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_currentSalaryInfl,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_HC,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_HCFreq,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_MonthlyExpenses,c2);
        c2.gridwidth = 3;
        c2.anchor = GridBagConstraints.CENTER;
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_PriorWorkingLabel,c2);
        c2.gridwidth = 1;
        c2.anchor = GridBagConstraints.EAST;
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_PriorWorkingYr,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_PriorWorkingAvg,c2);

        c2.gridx = 1;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = EAST_INSETS;

        y_count = 0;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_Name_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_Age_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_RetirementAge_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_currentSalary_Response,c2);
        y_count++;
        c2.gridy = y_count;
        y_infl_sal = y_count;
        panel_AssumSpouse.add(secAssum_currentSalaryInfl_slider,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_HC_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_HCFreq_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_MonthlyExpenses_Response,c2);
        y_count++;
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_PriorWorkingYr_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_AssumSpouse.add(secAssum_PriorWorkingAvg_Response,c2);

        c2.gridx = 2;
        c2.gridy = y_infl_sal;
        panel_AssumSpouse.add(secAssum_currentSalaryInfl_response,c2);


        tabbedPane.addTab("Spouse 2 Assumptions", panel_AssumSpouse);



        /*
        AV Tab
         */
        JLabel[] avAssum_labels;
        JTextField[] person1AV_response, person2AV_response, jointAV_response;

        avAssum_mainLabel = new JLabel("Account Value Balances");
        avAssum_account_Label = new JLabel("Account Type");
        avAssum_person1_Label = new JLabel("Spouse 1");
        avAssum_person2_Label = new JLabel("Spouse 2");
        avAssum_joint_Label = new JLabel("Joint");

        mainAssum_Name_Response.addFocusListener(new TextBox_LabelFocus(mainAssum_Name_Response, avAssum_person1_Label));
        secAssum_Name_Response.addFocusListener(new TextBox_LabelFocus(secAssum_Name_Response, avAssum_person2_Label));

        avAssum_mainLabel.setFont(new Font("LucidaGrande", Font.BOLD, 16));
        avAssum_account_Label.setFont(new Font("LucidaGrande", Font.BOLD, 12));
        avAssum_person1_Label.setFont(new Font("LucidaGrande", Font.BOLD, 12));
        avAssum_person2_Label.setFont(new Font("LucidaGrande", Font.BOLD, 12));
        avAssum_joint_Label.setFont(new Font("LucidaGrande", Font.BOLD, 12));
        avAssum_person1_Label.setHorizontalAlignment(JLabel.CENTER);
        avAssum_person2_Label.setHorizontalAlignment(JLabel.CENTER);
        avAssum_joint_Label.setHorizontalAlignment(JLabel.CENTER);

        c2.gridx = 0;
        c2.anchor = GridBagConstraints.WEST;
        c2.insets = WEST_INSETS;
        c2.gridy = 0;
        c2.gridwidth = 4;
        panel_AssumAV.add(avAssum_mainLabel,c2);
        c2.anchor = GridBagConstraints.CENTER;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridwidth = 1;

        c2.gridx = 0;
        c2.gridy = 1;
        panel_AssumAV.add(avAssum_account_Label,c2);
        c2.gridx = 1;
        panel_AssumAV.add(avAssum_person1_Label,c2);
        c2.gridx = 2;
        panel_AssumAV.add(avAssum_person2_Label,c2);
        c2.gridx = 3;
        panel_AssumAV.add(avAssum_joint_Label,c2);




        person1AV_response = new JTextField[AccountTypes.size()];
        person2AV_response = new JTextField[AccountTypes.size()];
        jointAV_response = new JTextField[AccountTypes.size()];
        avAssum_labels = new JLabel[AccountTypes.size()];
        y_count = 2;
        for(int i = 0; i< AccountTypes.size(); i++){
            avAssum_labels[i] = new JLabel(AccountTypes.get(i)[0]);
            person1AV_response[i] = new JTextField(10);
            person2AV_response[i] = new JTextField(10);
            jointAV_response[i] = new JTextField(10);

            person1AV_response[i].setText("0");
            person2AV_response[i].setText("0");
            jointAV_response[i].setText("0");

            c2.gridx = 0;
            c2.anchor = GridBagConstraints.EAST;
            c2.insets = EAST_INSETS;
            c2.gridy = 2+i;
            panel_AssumAV.add(avAssum_labels[i],c2);

            c2.gridx = 1;
            c2.anchor = GridBagConstraints.EAST;
            c2.fill = GridBagConstraints.HORIZONTAL;
            c2.insets = EAST_INSETS;
            panel_AssumAV.add(person1AV_response[i],c2);

            c2.gridx = 2;
            panel_AssumAV.add(person2AV_response[i],c2);

            c2.gridx = 3;
            panel_AssumAV.add(jointAV_response[i],c2);
            y_count++;
        }
        int y_count_start = y_count;

        JLabel assumAV_RetirementAssumptions_Label, assumAV_SS_BeginAge, assumAV_SS_FundingAdj, assumAV_SS_Inflation,assumAV_Current401kMax,assumAV_CurrentIRAMax,assumAV_retirementLimitInflation;
        JComboBox assumAV_SS_BeginAge_Response,assumAV_SS_FundingAdj_Response;
        JTextField assumAV_Ret_401kMax,assumAV_Ret_IRAMax,assumAV_SS_Inflation_Response,assumAV_Ret_Infl;
        JSlider assumAV_SS_Inflation_slider, assumAV_Ret_Infl_slider;

        assumAV_RetirementAssumptions_Label = new JLabel("Retirement Account Assumptions");
        assumAV_RetirementAssumptions_Label.setFont(new Font("LucidaGrande", Font.BOLD, 16));
        assumAV_Current401kMax = new JLabel("401k Max Contribution");
        assumAV_CurrentIRAMax = new JLabel("IRA Max Contribution");
        assumAV_retirementLimitInflation = new JLabel("Retirement Limit Inflation");
        assumAV_SS_BeginAge = new JLabel("Social Security Start Age");
        assumAV_SS_FundingAdj = new JLabel("Social Security Funding Adj");
        assumAV_SS_Inflation = new JLabel("Social Security Inflation");


        //401k Max
        assumAV_Ret_401kMax = new JTextField(10);
        assumAV_Ret_401kMax.setText("19000");

        //IRA Max
        assumAV_Ret_IRAMax = new JTextField(10);
        assumAV_Ret_IRAMax.setText("6000");

        //Retirement Limit Inflation Inflation
        assumAV_Ret_Infl = new JTextField(10);
        assumAV_Ret_Infl_slider = new JSlider(0, 1000, 150);

        setPreferredSliders(assumAV_Ret_Infl_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(assumAV_Ret_Infl,assumAV_Ret_Infl_slider,100.0);


        //Social Security Begin Age

        assumAV_SS_BeginAge_Response = new JComboBox(ages);
        assumAV_SS_BeginAge_Response.setSelectedIndex(9);

        //Social Security Funding Adjustment

        assumAV_SS_FundingAdj_Response = new JComboBox(jump);
        assumAV_SS_FundingAdj_Response.setSelectedIndex(10);


        //Social Security Inflation
        assumAV_SS_Inflation_Response = new JTextField(10);
        assumAV_SS_Inflation_slider = new JSlider(0, 1000, 200);

        setPreferredSliders(assumAV_SS_Inflation_slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(assumAV_SS_Inflation_Response,assumAV_SS_Inflation_slider,100.0);


        y_count = y_count_start;
        c.gridwidth = 4;
        c.gridx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_RetirementAssumptions_Label,c);
        y_count++;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_Current401kMax,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_CurrentIRAMax,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_retirementLimitInflation,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_SS_BeginAge,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_SS_FundingAdj,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_SS_Inflation,c);
        y_count++;

        y_count = y_count_start;
        y_count++;
        c.gridx = 1;
        c.gridwidth =3;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_Ret_401kMax,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_Ret_IRAMax,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_Ret_Infl_slider,c);
        y_infl_retLim = y_count;
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_SS_BeginAge_Response,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_SS_FundingAdj_Response,c);
        y_count++;
        c.gridy = y_count;
        panel_AssumAV.add(assumAV_SS_Inflation_slider,c);
        y_infl_SS = y_count;


        c.gridx = 4;
        c.gridy = y_infl_retLim;
        panel_AssumAV.add(assumAV_Ret_Infl,c);
        c.gridy = y_infl_SS;
        panel_AssumAV.add(assumAV_SS_Inflation_Response,c);

        tabbedPane.addTab("Retirement Account Assumptions", panel_AssumAV);


        /*
        Housing
         */


        JLabel housing_JointExpenses, housing_RentExpense, housing_CurrentSituation,housing_mortgagePayment, housing_HomeValue, housing_MaintExp;
        JComboBox housing_currentSituation_Response;
        JTextField housing_JointExpenses_Response,housing_RentExpense_Response,housing_mortgagePayment_Response, housing_HomeValue_Response, housing_MaintExp_Response;
        JSlider housing_MaintExp_Slider;

        //Set Labels
        housing_JointExpenses = new JLabel("Joint Household Expenses:");
        housing_RentExpense = new JLabel("Current Rent Expense:");
        housing_CurrentSituation = new JLabel("Current Housing Situation:");
        housing_mortgagePayment = new JLabel("Current Mortgage Expense:");
        housing_HomeValue = new JLabel("Home Value:");
        housing_MaintExp = new JLabel("Annual Maintenance Expenses:");

        //Housing Options
        String[] housingOptions = {"Renter","Homeowner","Eventual Homeowner"};
        housing_currentSituation_Response = new JComboBox(housingOptions);
        housing_currentSituation_Response.setSelectedIndex(1);

        //Joint Expenses
        housing_JointExpenses_Response = new JTextField(10);
        housing_JointExpenses_Response.setText("2000");

        //Rent Expense
        housing_RentExpense_Response = new JTextField(10);
        housing_RentExpense_Response.setText("1800");

        //Mortgage
        housing_mortgagePayment_Response = new JTextField(10);
        housing_mortgagePayment_Response.setText("2500");

        //Home Value
        housing_HomeValue_Response = new JTextField(10);
        housing_HomeValue_Response.setText("500000");

        //Maintenance Expenses
        housing_MaintExp_Response = new JTextField(10);
        housing_MaintExp_Slider = new JSlider(0, 500, 100);

        setPreferredSliders(housing_MaintExp_Slider, dict, 100, 100, true, true, 400, 50);
        linkTextFieldAndSlider(housing_MaintExp_Response,housing_MaintExp_Slider,100.0);


        y_count =0;
        y_infl_sal = 0;

        c2.gridx = 0;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = 0;
        c2.insets = EAST_INSETS;

        c2.gridy = y_count;
        panel_Housing.add(housing_CurrentSituation,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_JointExpenses,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_RentExpense,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_mortgagePayment,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_HomeValue,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_MaintExp,c2);
        y_count++;

        c2.gridx = 1;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = EAST_INSETS;

        int y_MaintExp=0;
        y_count = 0;
        c2.gridy = y_count;
        panel_Housing.add(housing_currentSituation_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_JointExpenses_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_RentExpense_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_mortgagePayment_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_HomeValue_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_Housing.add(housing_MaintExp_Slider,c2);
        y_MaintExp = y_count;
        y_count++;

        c2.gridx = 2;
        c2.gridy = y_MaintExp;
        panel_Housing.add(housing_MaintExp_Response,c2);

        tabbedPane.addTab("Housing Assumptions", panel_Housing);


        /*
        Investment Return Assumptions
         */
        JLabel invAssum_equityPctCurrent, invAssum_equityPctRetirement, invAssum_equityPctGrading, invAssum_equityReturnMean,invAssum_equityReturnSD,
                invAssum_bondReturnSD,invAssum_bondReturnMean,invAssum_scenarioCount,invAssum_seed;
        JComboBox invAssum_equityPctGrading_Response;
        JTextField invAssum_scenarioCount_Response,invAssum_seed_Response,
                invAssum_equityPctCurrent_Response,invAssum_equityPctRetirement_Response,invAssum_equityReturnMean_Response,invAssum_equityReturnSD_Response,invAssum_bondReturnSD_Response,invAssum_bondReturnMean_Response;
        JSlider invAssum_equityPctCurrent_Slider,invAssum_equityPctRetirement_Slider,invAssum_equityReturnMean_Slider,invAssum_equityReturnSD_Slider,invAssum_bondReturnSD_Slider,invAssum_bondReturnMean_Slider;


        //Set Labels
        invAssum_equityPctCurrent = new JLabel("Current Equity Allocation:");
        invAssum_equityPctRetirement = new JLabel("Equity Allocation in Retirement:");
        invAssum_equityPctGrading = new JLabel("Allocation Grading Years");
        invAssum_equityReturnMean = new JLabel("Expected Equity Return:");
        invAssum_equityReturnSD = new JLabel("Equity Return - Standard Deviation:");
        invAssum_bondReturnMean = new JLabel("Expected Bond Return:");
        invAssum_bondReturnSD = new JLabel("Bond Return - Standard Deviation:");
        invAssum_scenarioCount = new JLabel("Scenario Count:");
        invAssum_seed = new JLabel("Random Seed");

        //Current Allocation Pct
        invAssum_equityPctCurrent_Response = new JTextField(10);
        invAssum_equityPctCurrent_Slider = new JSlider(0, 100, 100);

        Dictionary dict_pct = new Hashtable();
        for (int i=0; i<=100; i+=10) {
            dict_pct.put(i, new JLabel(i+ "%"));
        }
        setPreferredSliders(invAssum_equityPctCurrent_Slider, dict_pct, 10, 10, true, true, 400, 50);
        linkTextFieldAndSlider_NoDecimal(invAssum_equityPctCurrent_Response,invAssum_equityPctCurrent_Slider,1);

        //Retirement Allocation Pct
        invAssum_equityPctRetirement_Response = new JTextField(10);
        invAssum_equityPctRetirement_Slider = new JSlider(0, 100, 100);
        setPreferredSliders(invAssum_equityPctRetirement_Slider, dict_pct, 10, 10, true, true, 400, 50);
        linkTextFieldAndSlider_NoDecimal(invAssum_equityPctRetirement_Response,invAssum_equityPctRetirement_Slider,1);


        //Grading Period
        invAssum_equityPctGrading_Response = new JComboBox(years);
        invAssum_equityPctGrading_Response.setSelectedIndex(8);

        Dictionary dict_500 = new Hashtable();
        for (int i=0; i<=3000; i+=500) {
            dict_500.put(i, new JLabel(Double.toString(i / 100.0) + "%"));
        }
        //Equity Return Mean
        invAssum_equityReturnMean_Response = new JTextField(10);
        invAssum_equityReturnMean_Slider = new JSlider(0, 2000, 700);
        setPreferredSliders(invAssum_equityReturnMean_Slider, dict_500, 500, 500, true, true, 400, 50);
        linkTextFieldAndSlider(invAssum_equityReturnMean_Response,invAssum_equityReturnMean_Slider,100.0);

        //Equity Return SD
        invAssum_equityReturnSD_Response = new JTextField(10);
        invAssum_equityReturnSD_Slider = new JSlider(0, 3000, 1500);
        setPreferredSliders(invAssum_equityReturnSD_Slider, dict_500, 500, 500, true, true, 400, 50);
        linkTextFieldAndSlider(invAssum_equityReturnSD_Response,invAssum_equityReturnSD_Slider,100.0);

        //Bond Return Mean
        invAssum_bondReturnMean_Response = new JTextField(10);
        invAssum_bondReturnMean_Slider = new JSlider(0, 2000, 300);
        setPreferredSliders(invAssum_bondReturnMean_Slider, dict_500, 500, 500, true, true, 400, 50);
        linkTextFieldAndSlider(invAssum_bondReturnMean_Response,invAssum_bondReturnMean_Slider,100.0);

        //Bond Return SD
        invAssum_bondReturnSD_Response = new JTextField(10);
        invAssum_bondReturnSD_Slider = new JSlider(0, 3000, 500);
        setPreferredSliders(invAssum_bondReturnSD_Slider, dict_500, 500, 500, true, true, 400, 50);
        linkTextFieldAndSlider(invAssum_bondReturnSD_Response,invAssum_bondReturnSD_Slider,100.0);

        //Scenarios
        invAssum_scenarioCount_Response = new JTextField(10);
        invAssum_scenarioCount_Response.setText("1000");

        //Seed
        invAssum_seed_Response = new JTextField(10);
        invAssum_seed_Response.setText("1234");

        y_count =0;
        int y_eq_curr = 0;
        int y_eq_ret = 0;
        int y_eq_mean = 0;
        int y_eq_sd = 0;
        int y_bond_mean = 0;
        int y_bond_sd = 0;

        c2.gridx = 0;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = 0;
        c2.insets = EAST_INSETS;

        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_equityPctCurrent,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_equityPctRetirement,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_equityPctGrading,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_equityReturnMean,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_equityReturnSD,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_bondReturnMean,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_bondReturnSD,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_scenarioCount,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_seed,c2);

        c2.gridx = 1;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = EAST_INSETS;

        y_count = 0;
        c2.gridy = y_count;
        y_eq_curr = y_count;
        panel_InvestmentReturns.add(invAssum_equityPctCurrent_Slider,c2);
        y_count++;
        c2.gridy = y_count;
        y_eq_ret = y_count;
        panel_InvestmentReturns.add(invAssum_equityPctRetirement_Slider,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_equityPctGrading_Response,c2);
        y_count++;
        c2.gridy = y_count;
        y_eq_mean = y_count;
        panel_InvestmentReturns.add(invAssum_equityReturnMean_Slider,c2);
        y_count++;
        c2.gridy = y_count;
        y_eq_sd = y_count;
        panel_InvestmentReturns.add(invAssum_equityReturnSD_Slider,c2);
        y_count++;
        c2.gridy = y_count;
        y_bond_mean = y_count;
        panel_InvestmentReturns.add(invAssum_bondReturnMean_Slider,c2);
        y_count++;
        c2.gridy = y_count;
        y_bond_sd = y_count;
        panel_InvestmentReturns.add(invAssum_bondReturnSD_Slider,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_scenarioCount_Response,c2);
        y_count++;
        c2.gridy = y_count;
        panel_InvestmentReturns.add(invAssum_seed_Response,c2);

        c2.gridx = 2;
        c2.gridy = y_eq_curr;
        panel_InvestmentReturns.add(invAssum_equityPctCurrent_Response,c2);
        c2.gridy = y_eq_ret;
        panel_InvestmentReturns.add(invAssum_equityPctRetirement_Response,c2);
        c2.gridy = y_eq_mean;
        panel_InvestmentReturns.add(invAssum_equityReturnMean_Response,c2);
        c2.gridy = y_eq_sd;
        panel_InvestmentReturns.add(invAssum_equityReturnSD_Response,c2);
        c2.gridy = y_bond_mean;
        panel_InvestmentReturns.add(invAssum_bondReturnMean_Response,c2);
        c2.gridy = y_bond_sd;
        panel_InvestmentReturns.add(invAssum_bondReturnSD_Response,c2);

        tabbedPane.addTab("Investment Assumptions", panel_InvestmentReturns);

        /*
        List Setups
         */
        input_values.add(generalAssum_InflExpense_Response_slider);
        input_values.add(generalAssum_InflTaxBrackets_Response_slider);
        input_values.add(generalAssum_HC_Infl_slider);
        input_values.add(mainAssum_currentSalaryInfl_slider);
        input_values.add(secAssum_currentSalaryInfl_slider);
        input_values.add(assumAV_Ret_Infl_slider);
        input_values.add(assumAV_SS_Inflation_slider);
        input_values.add(invAssum_equityPctCurrent_Slider);
        input_values.add(invAssum_equityPctRetirement_Slider);
        input_values.add(invAssum_equityReturnMean_Slider);
        input_values.add(invAssum_equityReturnSD_Slider);
        input_values.add(invAssum_bondReturnMean_Slider);
        input_values.add(invAssum_bondReturnSD_Slider);

        input_values.add(mainAssum_Name_Response);
        input_values.add(mainAssum_currentSalary_Response);
        input_values.add(mainAssum_HC_Response);
        input_values.add(mainAssum_MonthlyExpenses_Response);
        input_values.add(mainAssum_PriorWorkingAvg_Response);
        input_values.add(secAssum_Name_Response);
        input_values.add(secAssum_currentSalary_Response);
        input_values.add(secAssum_HC_Response);
        input_values.add(secAssum_MonthlyExpenses_Response);
        input_values.add(secAssum_PriorWorkingAvg_Response);
        for(int i=0; i<AccountTypes.size(); i++){
            input_values.add(person1AV_response[i]);
            input_values.add(person2AV_response[i]);
            input_values.add(jointAV_response[i]);
        }
        input_values.add(assumAV_Ret_401kMax);
        input_values.add(assumAV_Ret_IRAMax);
        input_values.add(invAssum_scenarioCount_Response);
        input_values.add(invAssum_seed_Response);

        input_values.add(generalAssum_CurrentYr_Response);
        input_values.add(generalAssum_ProjYrs_Response);
        input_values.add(generalAssum_State_Response);
        input_values.add(generalAssum_HC_MedicareRed);
        input_values.add(generalAssum_HC_RetJump);
        input_values.add(generalAssum_HC_MedicareAge);
        input_values.add(mainAssum_Age_Response);
        input_values.add(mainAssum_RetirementAge_Response);
        input_values.add(mainAssum_HCFreq_Response);
        input_values.add(mainAssum_PriorWorkingYr_Response);
        input_values.add(secAssum_Age_Response);
        input_values.add(secAssum_RetirementAge_Response);
        input_values.add(secAssum_HCFreq_Response);
        input_values.add(secAssum_PriorWorkingYr_Response);
        input_values.add(assumAV_SS_BeginAge_Response);
        input_values.add(assumAV_SS_FundingAdj_Response);
        input_values.add(invAssum_equityPctGrading_Response);

        input_mapping.put(generalAssum_CurrentYr_Response,"CurrentYear");
        input_mapping.put(generalAssum_ProjYrs_Response,"ProjectionYears");
        input_mapping.put(generalAssum_State_Response,"State");
        input_mapping.put(generalAssum_InflExpense_Response_slider,"ExpenseInflation");
        input_mapping.put(generalAssum_InflTaxBrackets_Response_slider,"TaxBracketInflation");
        input_mapping.put(generalAssum_HC_Infl_slider,"HealthcareInflation");
        input_mapping.put(generalAssum_HC_RetJump,"PostRetirementHealthcareJump");
        input_mapping.put(generalAssum_HC_MedicareAge,"MedicareStartAge");
        input_mapping.put(generalAssum_HC_MedicareRed,"MedicareHealthAdj");

        input_mapping.put(mainAssum_Name_Response,"Person1_Name");
        input_mapping.put(mainAssum_Age_Response,"Person1_Age");
        input_mapping.put(mainAssum_RetirementAge_Response,"Person1_RetirementAge");
        input_mapping.put(mainAssum_currentSalary_Response,"Person1_InitialSalary");
        input_mapping.put(mainAssum_currentSalaryInfl_slider,"Person1_SalaryInflation");
        input_mapping.put(mainAssum_HC_Response,"Person1_HealthInsurance");
        input_mapping.put(mainAssum_HCFreq_Response,"Person1_HealthInsuranceFreq");
        input_mapping.put(mainAssum_MonthlyExpenses_Response,"Person1_PersonalExpenses_monthly");
        input_mapping.put(mainAssum_PriorWorkingYr_Response,"Person1_PriorWorkingYears");
        input_mapping.put(mainAssum_PriorWorkingAvg_Response,"Person1_PriorWorkingAvgSalary");

        input_mapping.put(secAssum_Name_Response,"Person2_Name");
        input_mapping.put(secAssum_Age_Response,"Person2_Age");
        input_mapping.put(secAssum_RetirementAge_Response,"Person2_RetirementAge");
        input_mapping.put(secAssum_currentSalary_Response,"Person2_InitialSalary");
        input_mapping.put(secAssum_currentSalaryInfl_slider,"Person2_SalaryInflation");
        input_mapping.put(secAssum_HC_Response,"Person2_HealthInsurance");
        input_mapping.put(secAssum_HCFreq_Response,"Person2_HealthInsuranceFreq");
        input_mapping.put(secAssum_MonthlyExpenses_Response,"Person2_PersonalExpenses_monthly");
        input_mapping.put(secAssum_PriorWorkingYr_Response,"Person2_PriorWorkingYears");
        input_mapping.put(secAssum_PriorWorkingAvg_Response,"Person2_PriorWorkingAvgSalary");

        for(int i=0; i<AccountTypes.size(); i++){
            input_mapping.put(person1AV_response[i],"AV_"+AccountTypes.get(i)[0]+"_Person1");
            input_mapping.put(person2AV_response[i],"AV_"+AccountTypes.get(i)[0]+"_Person2");
            input_mapping.put(jointAV_response[i],"AV_"+AccountTypes.get(i)[0]+"_Joint");
        }
        input_mapping.put(assumAV_Ret_401kMax,"401kMax");
        input_mapping.put(assumAV_Ret_IRAMax,"IRAMax");
        input_mapping.put(assumAV_Ret_Infl_slider,"RetirementLimitInflation");
        input_mapping.put(assumAV_SS_BeginAge_Response,"SS_BeginAge");
        input_mapping.put(assumAV_SS_FundingAdj_Response,"SS_Adjustment");
        input_mapping.put(assumAV_SS_Inflation_slider,"SS_Inflation");

        input_mapping.put(invAssum_equityPctCurrent_Slider,"InvAssum_InitialAllocationPct_Equity");
        input_mapping.put(invAssum_equityPctRetirement_Slider,"InvAssum_RetirementAllocationPct_Equity");
        input_mapping.put(invAssum_equityPctGrading_Response,"InvAssum_GradingPeriod");
        input_mapping.put(invAssum_equityReturnMean_Slider,"InvAssum_EquityReturn_Mean");
        input_mapping.put(invAssum_equityReturnSD_Slider,"InvAssum_EquityReturn_SD");
        input_mapping.put(invAssum_bondReturnMean_Slider,"InvAssum_BondReturn_Mean");
        input_mapping.put(invAssum_bondReturnSD_Slider,"InvAssum_BondReturn_SD");
        input_mapping.put(invAssum_scenarioCount_Response,"InvAssum_ScenarioCount");
        input_mapping.put(invAssum_seed_Response,"InvAssum_Seed");


        /*
        Create Buttons
         */
        JButton button_loadInputs = new JButton("Load Saved Inputs");
        button_loadInputs.setPreferredSize(new Dimension(200,20));
        JButton button_saveInputs = new JButton("Save Inputs");
        button_saveInputs.setPreferredSize(new Dimension(200,20));
        JButton button_runProjection = new JButton("Run Projection");
        button_runProjection.setPreferredSize(new Dimension(200,20));
        JButton button_defaultInputs = new JButton("Reset Inputs");
        button_defaultInputs.setPreferredSize(new Dimension(200,20));


        button_saveInputs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AllAssum_UI.clear();
                for(JComponent x: input_values)
                    saveJComponent(AllAssum_UI, x, input_mapping);


                JFileChooser saveFile = new JFileChooser();
                saveFile.setSelectedFile(new File( "Inputs.csv"));
                if (saveFile.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File xyz = saveFile.getSelectedFile();
                    try {
                        OutputWriter.saveInputs(AllAssum_UI, xyz.toString());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });

        button_loadInputs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AllAssum_UI.clear();


                JFileChooser loadFile = new JFileChooser();
                if (loadFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File xyz = loadFile.getSelectedFile();
                    try {
                        ar.load(AllAssum_UI, xyz.toString());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                for(JComponent x: input_values)
                    loadJComponent(AllAssum_UI, x, input_mapping);

            }
        });

        button_defaultInputs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AllAssum_UI.clear();

                try {
                    ar.load(AllAssum_UI, "Data/Defaults.csv");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


                for(JComponent x: input_values)
                    loadJComponent(AllAssum_UI, x, input_mapping);

            }
        });

        button_runProjection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeAssumptionsToArrays(AllAssum_UI,Person1Assum,Person1AVs,Person2Assum,Person2AVs,JointAssum,JointAVs,GeneralAssumptions,InvestmentAssum,AccountTypes);
            }

        });

        JPanel buttons = new JPanel(new GridBagLayout());
        GridBagConstraints button_gbc = new GridBagConstraints();

        button_gbc.gridx = 0;
        buttons.add(button_defaultInputs, button_gbc);
        button_gbc.gridx = 1;
        buttons.add(button_loadInputs, button_gbc);
        button_gbc.gridx = 2;
        buttons.add(button_saveInputs, button_gbc);
        button_gbc.gridx = 3;
        buttons.add(button_runProjection, button_gbc);


        button_defaultInputs.doClick();
        /*
        Create Full frame
         */

        JPanel fullFrame = new JPanel(new BorderLayout());
        fullFrame.add(tabbedPane, BorderLayout.NORTH);
        fullFrame.add(buttons, BorderLayout.SOUTH);

        frame.getContentPane().add(fullFrame);
        frame.setVisible(true);

    }
    private static void writeAssumptionsToArrays(HashMap<String, String> allAssum_ui, HashMap<String, String> person1Assum, HashMap<String, String> person1AVs, HashMap<String, String> person2Assum, HashMap<String, String> person2AVs, HashMap<String, String> jointAssum, HashMap<String, String> jointAVs, HashMap<String, String> generalAssumptions, HashMap<String, String> investmentAssum, ArrayList<String[]> accountTypes)
    {
        String[] personAssum_inputs = {"Name","Age","PriorWorkingYears","PriorWorkingAvgSalary","InitialSalary","SalaryInflation","HealthInsurance","HealthInsuranceFreq","PersonalExpenses_monthly","RetirementAge"};
        for(String assum: personAssum_inputs){
            person1Assum.put(assum,allAssum_ui.get("Person1_" + assum));
            person2Assum.put(assum,allAssum_ui.get("Person2_" + assum));
        }

        ArrayList<String> accts = new ArrayList<String>();
        for(String[] accountString: accountTypes){
            accts.add(accountString[0]);
        }

        for(String acct: accts){
            person1AVs.put(acct,allAssum_ui.get("AV_" + acct + "_Person1"));
            person2AVs.put(acct,allAssum_ui.get("AV_" + acct + "_Person2"));
            jointAVs.put(acct,allAssum_ui.get("AV_" + acct + "_Joint"));
        }

        String[] generalAssum_inputs = {"CurrentYear","ProjectionYears","ExpenseInflation","TaxBracketInflation","401kMax","IRAMax","RetirementLimitInflation","HealthcareInflation","PostRetirementHealthcareJump","MedicareStartAge","MedicareHealthAdj"};
        for(String assum: generalAssum_inputs){
            generalAssumptions.put(assum,allAssum_ui.get(assum));
        }

        String[] invAssum_inputs = {"InitialAllocationPct_Equity","RetirementAllocationPct_Equity","GradingPeriod","EquityReturn_Mean","EquityReturn_SD","BondReturn_Mean","BondReturn_SD","ScenarioCount","Seed"};
        for(String assum: invAssum_inputs){
            investmentAssum.put(assum,allAssum_ui.get("InvAssum_"+assum));
        }

    }
    public static void saveJComponent(HashMap<String, String> assum, JComponent x, HashMap<JComponent, String> saveFileNames){
        if(x instanceof JComboBox)
            assum.put(saveFileNames.get(x), ((JComboBox) x).getSelectedItem().toString());
        else if(x instanceof JTextField)
            assum.put(saveFileNames.get(x), ((JTextField) x).getText());
        else if(x instanceof JSlider)
            assum.put(saveFileNames.get(x), String.valueOf(((JSlider) x).getValue()));
    }
    public static void loadJComponent(HashMap<String, String> assum, JComponent x, HashMap<JComponent, String> saveFileNames){
        if(x instanceof JComboBox)
            ((JComboBox) x).setSelectedIndex(reverseLookup(((JComboBox) x),assum.get(saveFileNames.get(x))));
        else if(x instanceof JTextField)
            ((JTextField) x).setText(assum.get(saveFileNames.get(x)));
        else if(x instanceof JSlider)
            ((JSlider) x).setValue(Integer.parseInt(assum.get(saveFileNames.get(x))));
    }

    public static void setPreferredSliders(JSlider s, Dictionary d, int main_Tick, int minor_tick, boolean paint_labels, boolean paint_ticks, int width, int height){
        s.setLabelTable(d);
        s.setMajorTickSpacing(main_Tick);
        s.setMinorTickSpacing(minor_tick);
        s.setPaintLabels(paint_labels);
        s.setPaintTicks(paint_ticks);
        s.setPreferredSize(new Dimension(width,height));
    }
    public static void linkTextFieldAndSlider_NoDecimal(JTextField t, JSlider s, double scaling){
        int maxSliderValue = s.getMaximum();
        t.setText(String.valueOf(s.getValue() / scaling)+"%");
        s.addChangeListener(new DecimalSliderChangeListener_NoDecimal(t,s));
        t.addFocusListener(new TextBox_SliderFocus_NoDecimal(t, s, maxSliderValue));
    }
    public static void linkTextFieldAndSlider(JTextField t, JSlider s, double scaling){
        int maxSliderValue = s.getMaximum();
        t.setText(String.valueOf(s.getValue() / scaling)+"%");
        s.addChangeListener(new DecimalSliderChangeListener(t,s));
        t.addFocusListener(new TextBox_SliderFocus(t, s, maxSliderValue));
    }
    public static int reverseLookup(JComboBox j, String lookupValue){
        int returnValue = 0;
        for(int i = 0; i<j.getItemCount(); i++){
            if(j.getItemAt(i).equals(lookupValue))
                returnValue = i;
        }
        return returnValue;
    }

    public static String[] getStates(){
        String[] states = new String[50];
        states[0] = "AL - Alabama";
        states[1] = "AK - Alaska";
        states[2] = "AZ - Arizona";
        states[3] = "AR - Arkansas";
        states[4] = "CA - California";
        states[5] = "CO - Colorado";
        states[6] = "CT - Connecticut";
        states[7] = "DE - Delaware";
        states[8] = "FL - Florida";
        states[9] = "GA - Georgia";
        states[10] = "HI - Hawaii";
        states[11] = "ID - Idaho";
        states[12] = "IL - Illinois";
        states[13] = "IN - Indiana";
        states[14] = "IA - Iowa";
        states[15] = "KS - Kansas";
        states[16] = "KY - Kentucky";
        states[17] = "LA - Louisiana";
        states[18] = "ME - Maine";
        states[19] = "MD - Maryland";
        states[20] = "MA - Massachusetts";
        states[21] = "MI - Michigan";
        states[22] = "MN - Minnesota";
        states[23] = "MS - Mississippi";
        states[24] = "MO - Missouri";
        states[25] = "MT - Montana";
        states[26] = "NE - Nebraska";
        states[27] = "NV - Nevada";
        states[28] = "NH - New Hampshire";
        states[29] = "NJ - New Jersey";
        states[30] = "NM - New Mexico";
        states[31] = "NY - New York";
        states[32] = "NC - North Carolina";
        states[33] = "ND - North Dakota";
        states[34] = "OH - Ohio";
        states[35] = "OK - Oklahoma";
        states[36] = "OR - Oregon";
        states[37] = "PA - Pennsylvania";
        states[38] = "RI - Rhode Island";
        states[39] = "SC - South Carolina";
        states[40] = "SD - South Dakota";
        states[41] = "TN - Tennessee";
        states[42] = "TX - Texas";
        states[43] = "UT - Utah";
        states[44] = "VT - Vermont";
        states[45] = "VA - Virginia";
        states[46] = "WA - Washington";
        states[47] = "WV - West Virginia";
        states[48] = "WI - Wisconsin";
        states[49] = "WY - Wyoming";
        return states;
    }

}
