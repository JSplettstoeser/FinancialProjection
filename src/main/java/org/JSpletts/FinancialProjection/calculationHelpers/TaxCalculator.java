package org.JSpletts.FinancialProjection.calculationHelpers;

import java.util.ArrayList;

public class TaxCalculator {

    ArrayList<Double> FedBracketsSingle = new ArrayList<Double>();
    ArrayList<Double> FedRatesSingle = new ArrayList<Double>();
    ArrayList<Double> FedBracketsJoint = new ArrayList<Double>();
    ArrayList<Double> FedRatesJoint = new ArrayList<Double>();
    ArrayList<Double> StateBracketsSingle = new ArrayList<Double>();
    ArrayList<Double> StateRatesSingle = new ArrayList<Double>();
    ArrayList<Double> StateBracketsJoint = new ArrayList<Double>();
    ArrayList<Double> StateRatesJoint = new ArrayList<Double>();
    ArrayList<Double> SSBrackets = new ArrayList<Double>();
    ArrayList<Double> SSRates = new ArrayList<Double>();
    ArrayList<Double> MedicareBrackets = new ArrayList<Double>();
    ArrayList<Double> MedicareRates = new ArrayList<Double>();
    private double taxBracketInflation;


    public TaxCalculator(double taxBracketInflation) {
        this.taxBracketInflation = taxBracketInflation;
        loadBrackets();
    }

    private double calcTax(ArrayList<Double> originalBrackets, ArrayList<Double> originalRates, double stdDeduction, double TaxableIncome, int projectionYear, double bracketInflation) {
        ArrayList<Double> projBrackets = new ArrayList<Double>();

        double taxBracketInflation = bracketInflation;
        double taxableIncome = TaxableIncome;
        double tax;
        double remainingIncome;
        double taxableInBracket;
        double standardDeduction = stdDeduction * Math.pow(1+taxBracketInflation, projectionYear);

        for(int j = 0; j<originalBrackets.size(); j++)
            projBrackets.add(originalBrackets.get(j)* Math.pow(1+taxBracketInflation, projectionYear));

        tax = 0.0;
        taxableIncome = Math.max(0, taxableIncome-standardDeduction);
        remainingIncome = taxableIncome;

        int j = 0;
        while(remainingIncome > 0) {
            if(j == projBrackets.size()-1)
                taxableInBracket = remainingIncome;
            else
                taxableInBracket = Math.min(remainingIncome, projBrackets.get(j+1)-projBrackets.get(j));

            tax += originalRates.get(j)* taxableInBracket;
            remainingIncome -= taxableInBracket;
            j+=1;
        }

        return tax;
    }



    public double calcFederalTaxSingle(double TaxableIncome, int projectionYear) {
        return calcTax(FedBracketsSingle, FedRatesSingle, 12000, TaxableIncome, projectionYear, taxBracketInflation);
    }
    public double calcFederalTaxJoint(double TaxableIncome, int projectionYear) {
        return calcTax(FedBracketsJoint, FedRatesJoint, 24000, TaxableIncome, projectionYear, taxBracketInflation);
    }
    public double calcStateTaxSingle(double TaxableIncome, int projectionYear) {
        return calcTax(StateBracketsSingle, StateRatesSingle, 12000, TaxableIncome, projectionYear, taxBracketInflation);
    }
    public double calcStateTaxJoint(double TaxableIncome, int projectionYear) {
        return calcTax(StateBracketsJoint, StateRatesJoint, 24000, TaxableIncome, projectionYear, taxBracketInflation);
    }
    public double calcSSTax(double TaxableIncome, int projectionYear) {
        return calcTax(SSBrackets, SSRates, 0, TaxableIncome, projectionYear, taxBracketInflation);
    }
    public double calcMedicareTax(double TaxableIncome, int projectionYear) {
        return calcTax(MedicareBrackets, MedicareRates, 0, TaxableIncome, projectionYear, taxBracketInflation);

    }










    private void loadBrackets() {
        loadFedBracketsSingle();
        loadFedBracketsJoint();
        loadStateBracketsSingle();
        loadStateBracketsJoint();
        loadSSBrackets();
        loadMedicareBrackets();

    }


    private void loadFedBracketsSingle() {
        FedBracketsSingle.add((double) 0);
        FedBracketsSingle.add((double) 9950);
        FedBracketsSingle.add((double) 40525);
        FedBracketsSingle.add((double) 86375);
        FedBracketsSingle.add((double) 164925);
        FedBracketsSingle.add((double) 209425);
        FedBracketsSingle.add((double) 523600);

        FedRatesSingle.add(0.1);
        FedRatesSingle.add(0.12);
        FedRatesSingle.add(0.22);
        FedRatesSingle.add(0.24);
        FedRatesSingle.add(0.32);
        FedRatesSingle.add(0.35);
        FedRatesSingle.add(0.37);
    }
    private void loadFedBracketsJoint() {
        FedBracketsJoint.add((double) 0);
        FedBracketsJoint.add((double) 19900);
        FedBracketsJoint.add((double) 81050);
        FedBracketsJoint.add((double) 172750);
        FedBracketsJoint.add((double) 329850);
        FedBracketsJoint.add((double) 418850);
        FedBracketsJoint.add((double) 628300);

        FedRatesJoint.add(0.1);
        FedRatesJoint.add(0.12);
        FedRatesJoint.add(0.22);
        FedRatesJoint.add(0.24);
        FedRatesJoint.add(0.32);
        FedRatesJoint.add(0.35);
        FedRatesJoint.add(0.37);
    }
    private void loadStateBracketsSingle() {
        StateBracketsSingle.add((double) 0);
        StateBracketsSingle.add((double) 27230);
        StateBracketsSingle.add((double) 89400);
        StateBracketsSingle.add((double) 166040);

        StateRatesSingle.add(0.0535);
        StateRatesSingle.add(0.068);
        StateRatesSingle.add(0.0785);
        StateRatesSingle.add(0.0985);
    }
    private void loadStateBracketsJoint() {
        StateBracketsJoint.add((double) 0);
        StateBracketsJoint.add((double) 39810);
        StateBracketsJoint.add((double) 158140);
        StateBracketsJoint.add((double) 276200);

        StateRatesJoint.add(0.0535);
        StateRatesJoint.add(0.068);
        StateRatesJoint.add(0.0785);
        StateRatesJoint.add(0.0985);
    }
    private void loadSSBrackets() {
        SSBrackets.add((double) 0);
        SSBrackets.add((double) 130000);

        SSRates.add(0.062);
        SSRates.add(0.0);
    }
    private void loadMedicareBrackets() {
        MedicareBrackets.add((double) 0);
        MedicareBrackets.add((double) 200000);

        MedicareRates.add(0.014);
        MedicareRates.add(0.023);
    }

}
