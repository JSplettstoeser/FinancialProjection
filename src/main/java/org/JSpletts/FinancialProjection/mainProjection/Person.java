package org.JSpletts.FinancialProjection.mainProjection;

import org.JSpletts.FinancialProjection.calculationHelpers.AssumptionReader;
import org.JSpletts.FinancialProjection.calculationHelpers.TaxCalculator;

import java.io.IOException;
import java.util.*;

public class Person {
    private String name;
    private double InitialSalary;
    private int projectionYears;

    public double[] salary;
    public double[] SS_income;
    public int[] age;
    public int initialAge;
    public int retirementAge;
    public double[] expenses;
    public double[] healthCare;
    private double[][] max401k;
    private double[][] maxIRA;

    private ArrayList<double[]> retirementAssum;
    private double[] contribPct401k;
    private double[] contribPctOfMaxIRA;
    private double[] matchPct401k;
    private double[] rothIRAPct;
    private double[] roth401kPct;

    private double[][] taxes;
    private double[][] contributions;
    private double[][] employerMatch;
    private String[] AccountIDs;
    private HashMap<String,Integer> accountIDMap = new HashMap<String, Integer>();

    int numAccounts;
    public double[] disposibleIncome;
    private TaxCalculator tc;


    Map<String,String> PersonalAssum = new HashMap<String, String>();
    Map<String,String> PersonalAVs = new HashMap<String, String>();
    Map<String,String> GeneralAssumptions = new HashMap<String, String>();
    Map<String,String> SS_Assumption = new HashMap<String, String>();
    double SS_Annual_Income;
    int SS_beginAge = 1000;
    double SS_inflation;


    public Person(Map<String,String> personAssum, Map<String,String> personalAVs, Map<String,String> generalAssum, ArrayList<double[]> retirementAssum, Map<String, String> SS_Assum) {
        this.PersonalAssum = personAssum;
        this.PersonalAVs= personalAVs;
        this.GeneralAssumptions = generalAssum;
        this.retirementAssum = retirementAssum;
        this.SS_Assumption = SS_Assum;

        setValues();
        this.tc = new TaxCalculator(projectionYears);

        createArrays();
        setRetirementParams(retirementAssum.size());

    }
    private void setValues() {

        this.name = PersonalAssum.get("Name");
        this.initialAge = Integer.valueOf(PersonalAssum.get("Age"));
        this.retirementAge = Integer.valueOf(PersonalAssum.get("RetirementAge"));
        this.InitialSalary =Double.valueOf(PersonalAssum.get("InitialSalary"));
        this.projectionYears=Integer.valueOf(GeneralAssumptions.get("ProjectionYears"));


    }
    private void setRetirementParams(int years) {
        rothIRAPct=new double[years+1];
        roth401kPct=new double[years+1];
        contribPct401k=new double[years+1];
        contribPctOfMaxIRA=new double[years+1];
        matchPct401k=new double[years+1];
        max401k = new double[years+1][2];
        maxIRA = new double[years+1][2];
        double inflation = Double.valueOf(GeneralAssumptions.get("RetirementLimitInflation"));

        for(int i = 0; i<retirementAssum.size(); i++){
            double[] dataForYear = retirementAssum.get(i);
            rothIRAPct[i] = dataForYear[1];
            roth401kPct[i] = dataForYear[2];
            contribPct401k[i] = dataForYear[3];
            contribPctOfMaxIRA[i] = dataForYear[4];
            matchPct401k[i] = dataForYear[5];
            max401k[i][1] =Double.valueOf(GeneralAssumptions.get("401kMax")) * Math.pow(1+inflation, i);
            max401k[i][0] = Math.round(max401k[i][1] / 500.0) * 500.0;
            maxIRA[i][1] =Double.valueOf(GeneralAssumptions.get("IRAMax")) * Math.pow(1+inflation, i);
            maxIRA[i][0] = Math.round(maxIRA[i][1] / 500.0) * 500.0;
        }
    }

    private void createArrays(){
        salary = new double[projectionYears+1];
        SS_income = new double[projectionYears + 1];
        age = new int[projectionYears+1];
        expenses = new double[projectionYears+1];
        healthCare = new double[projectionYears+1];

        contribPct401k = new double[projectionYears+1];
        contribPctOfMaxIRA = new double[projectionYears+1];
        matchPct401k = new double[projectionYears+1];
        rothIRAPct = new double[projectionYears+1];
        roth401kPct = new double[projectionYears+1];

        taxes = new double[4][projectionYears+1];
        contributions = new double[numAccounts][projectionYears+1];
        employerMatch = new double[numAccounts][projectionYears+1];

        disposibleIncome = new double[projectionYears+1];

        for(int i = 0; i<=projectionYears; i++){
            age[i]=initialAge + i;
        }
    }

    public String getName() {
        return this.name;
    }

    public double getAge(int year){
        return age[year];
    }
    public double calcIncome(int year) {
        if(age[year]>=retirementAge)
            return 0;
        double inflation = Double.valueOf(PersonalAssum.get("SalaryInflation"));
        salary[year] = InitialSalary * Math.pow(1+inflation, year);
        return salary[year];
    }
    public double calc401kContrib(int year) {
        double inflation = Double.valueOf(PersonalAssum.get("SalaryInflation"));
        return InitialSalary * Math.pow(1+inflation, year);
    }

    public double getGrossIncome(int year) {
        return salary[year];
    }
    public double getInvestments(int year) {
        double investments =0;
        for(int j = 0; j<contributions.length; j++)
            investments += contributions[j][year];
        return investments;
    }
    public double getTaxPaid(int year) {
        double taxPaid = 0;
        for(int j = 0; j<taxes.length; j++)
            taxPaid += taxes[j][year];
        return taxPaid;
    }
    public double getExpensePaid(int year) {
        return expenses[year]+healthCare[year];
    }
    public double getDisposibleIncome(int year) {
        return disposibleIncome[year];
    }

    public TaxCalculator getTaxData() {
        return tc;
    }
    public int getProjectionYears() {
        return projectionYears;
    }
    public double calcRetirementContrib(int year, int acctNum, String accountName, double salary) {
        double returnValue = 0;
        if(salary ==0)
            return 0;
        if(accountName.equals("Roth IRA"))
            returnValue = rothIRAPct[year] * contribPctOfMaxIRA[year] * maxIRA[year][0];
        else if(accountName.equals("Trad IRA"))
            returnValue = (1 - rothIRAPct[year]) * contribPctOfMaxIRA[year] * maxIRA[year][0];
        else if(accountName.equals("Roth 401k"))
            returnValue = 0;
        else if(accountName.equals("Trad 401k"))
            returnValue = Math.min(salary * this.contribPct401k[year], this.max401k[year][0]);
        else
            returnValue = 0;

        return returnValue;
    }

    public double calcInitialRetirementBalance(int year, String accountName){
        if(year == 0)
            return Double.valueOf(PersonalAVs.get(accountName));
        else
            return 0;
    }
    public double calcRetirementContribEmployer(int year, int acctNum, String accountName, double salary, double contribution) {
        double returnValue = 0;

        if(accountName.equals("Roth IRA"))
            returnValue = 0;
        else if(accountName.equals("Trad IRA"))
            returnValue = 0;
        else if(accountName.equals("Roth 401k"))
            returnValue = Math.min(salary * this.matchPct401k[year], contribution) * (1- roth401kPct[year]);
        else if(accountName.equals("Trad 401k"))
            returnValue = Math.min(salary * this.matchPct401k[year], contribution) * (1- roth401kPct[year]);
        else
            returnValue = 0;

        return returnValue;
    }
    public double calcHealthcareCosts(int year) {

        double inflation = Double.valueOf(GeneralAssumptions.get("HealthcareInflation"));
        double initialExpenses = Double.valueOf(PersonalAssum.get("HealthInsurance"));
        double frequency = Double.valueOf(PersonalAssum.get("HealthInsuranceFreq"));

        double multiplier =1;
        if(age[year] >= retirementAge)
            multiplier = 1 + Double.valueOf(GeneralAssumptions.get("PostRetirementHealthcareJump"));
        if(age[year] >= Integer.valueOf(GeneralAssumptions.get("MedicareStartAge")))
            multiplier = multiplier * (1 - Double.valueOf(GeneralAssumptions.get("MedicareHealthAdj")));
        return frequency * initialExpenses * Math.pow(1+inflation, year) * multiplier;
    }
    public double calcPersonalExpenses(int year) {
        double inflation = Double.valueOf(GeneralAssumptions.get("ExpenseInflation"));
        double initialExpenses = Double.valueOf(PersonalAssum.get("PersonalExpenses_monthly"));
        double frequency = 12.0;

        return frequency * initialExpenses * Math.pow(1+inflation, year);
    }

    public double calcSSIncome(int year){
        return 0;
    }
    public void projectSSBenefits() throws IOException {
        int SS_Start_Age = Integer.valueOf(SS_Assumption.get("SS_BeginAge"));
        int SS_MinimumAge = Integer.valueOf(SS_Assumption.get("SS_MinimumAge"));
        int SS_UseExpectedDeferral = Integer.valueOf(SS_Assumption.get("SS_UseExpectedDeferralAdj"));
        int currentYear = Integer.valueOf(GeneralAssumptions.get("CurrentYear"));
        double SS_inflation = Double.valueOf(SS_Assumption.get("SS_Inflation"));
        int priorYears = Integer.valueOf(PersonalAssum.get("PriorWorkingYears"));
        double priorAvgSalary = Double.valueOf(PersonalAssum.get("PriorWorkingAvgSalary"));
        double[][] SS_wages = new double[priorYears + retirementAge - initialAge + 1][3];


        AssumptionReader ar = new AssumptionReader();
        ArrayList<double[]> SS_BendPoints = new ArrayList<double[]>();
        ar.loadDoubleArray(SS_BendPoints, "Data/SS_BendPoints.csv");

        ArrayList<double[]> SS_DeferralAdj = new ArrayList<double[]>();
        ar.loadDoubleArray(SS_DeferralAdj, "Data/SS_DeferralAdj.csv");

        int minYear = (int) SS_BendPoints.get(0)[0];
        int minAge = (int) SS_DeferralAdj.get(0)[0];

        int indexYear = currentYear + SS_MinimumAge - initialAge - 2;
        double incomeLevelIndexYear = SS_BendPoints.get(indexYear - minYear)[6];
        ArrayList<Double> sortedSalaries = new ArrayList<Double>();


        //calculate the inflation adjusted salary for each working year
        for(int i = 0; i<SS_wages.length; i++){
            SS_wages[i][2] = currentYear - priorYears + i;
            if(i<priorYears){
                SS_wages[i][0] = priorAvgSalary * Math.pow(1+SS_inflation, i-priorYears/2);
            }
            else {
                SS_wages[i][0] = salary[i - priorYears];
            }

            double incomeLevel = SS_BendPoints.get((int)SS_wages[i][2] - minYear)[6];;
            double inflationFactor = incomeLevelIndexYear / incomeLevel;
            SS_wages[i][1] = SS_wages[i][0] * inflationFactor;

            sortedSalaries.add(SS_wages[i][1]);
        }

        Collections.sort(sortedSalaries);
        Collections.reverse(sortedSalaries);
        double runningSum = 0;
        int SS_incomeYrs = 35;
        for(int i = 0; i<SS_incomeYrs; i++){
            if(i<sortedSalaries.size()){
                runningSum+=sortedSalaries.get(i);
            }
        }

        double aime  = runningSum / SS_incomeYrs / 12.0;
        double bend_1 = SS_BendPoints.get(indexYear - minYear + 2)[1];
        double bend_2 = SS_BendPoints.get(indexYear - minYear + 2)[2];
        double pct_bend_1 = Double.valueOf(SS_Assumption.get("PIABend_1"));
        double pct_bend_2 = Double.valueOf(SS_Assumption.get("PIABend_2"));
        double pct_bend_3 = Double.valueOf(SS_Assumption.get("PIABend_3"));
        double pia = 0;

        double pia1=0;
        double pia2=0;
        double pia3=0;
        if(aime < bend_1)
            pia1 += pct_bend_1 * aime;
        else
            pia1 += pct_bend_1  * bend_1;

        if(aime < bend_2)
            pia2 += pct_bend_2 * (aime - bend_1);
        else
            pia2 += pct_bend_2  * (bend_2 - bend_1);

        if(aime > bend_2)
            pia3 += pct_bend_3 * (aime - bend_2);

        pia = pia1 + pia2+pia3;

        double deferralAdj = 1;
        double SS_fundingAdj = Double.valueOf(SS_Assumption.get("SS_Adjustment"));

        if(SS_UseExpectedDeferral == 1){
            deferralAdj = SS_DeferralAdj.get(SS_Start_Age - minAge)[2];
        }
        else{
            deferralAdj = SS_DeferralAdj.get(SS_Start_Age - minAge)[1];
        }



        double SS_MonthlyPmt = pia * deferralAdj * SS_fundingAdj;

        SS_Annual_Income = SS_MonthlyPmt * 12;
        SS_beginAge = SS_Start_Age;
        this.SS_inflation = SS_inflation;

    }

    public int retirementYear(){
        return retirementAge - initialAge;
    }

    public double calcNonTaxableIncome(int year) throws IOException {
        if(year < retirementAge - initialAge){
            return 0;
        }
        if(year == retirementAge - initialAge){
            projectSSBenefits();
        }

        if(year >= SS_beginAge - initialAge){
            int inflationYrs = year - (SS_beginAge - initialAge);
            return SS_Annual_Income * Math.pow(1+SS_inflation, inflationYrs);
        }
        return 0;
    }
}