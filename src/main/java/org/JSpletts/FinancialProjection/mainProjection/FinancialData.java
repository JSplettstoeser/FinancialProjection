package org.JSpletts.FinancialProjection.mainProjection;


import org.JSpletts.FinancialProjection.calculationHelpers.AssumptionReader;
import org.JSpletts.FinancialProjection.calculationHelpers.OutputWriter;
import org.JSpletts.FinancialProjection.calculationHelpers.TaxCalculator;
import org.JSpletts.FinancialProjection.constants.Parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinancialData {

    Person[] pList;
    Map<String,String> GeneralAssumptions = new HashMap<String, String>();
    Map<String,String> JointAssum = new HashMap<String, String>();
    Map<String,String> JointAVs = new HashMap<String, String>();
    Map<String,String> InvestmentAssumptions = new HashMap<String, String>();

    TaxCalculator tc;
    private int projectionYears;
    private int numIndividuals;
    Parameters params = new Parameters();
    private int numScenarios;

    private double[][][] individualCalcs;
    private double[][] jointCalcs;
    private double[][][] accountContrib;
    private double[][][] accountContribEmployer;
    private double[][][][] accountValuesIndiv; //[indiv][account][column][year], columns should be: BOY Value, Contribution, ContributionTiming, Return, EOY Value
    private double[][][] accountValuesJoint; //[account][column][year], same columns
    private double[][][] scenarios;
    private double[][] jointIncomeAllocation;

    ArrayList<String[]> accountMetaData = new ArrayList<String[]>();

    public FinancialData(HashMap<String, String> jointAssum, HashMap<String, String> jointAVs,
                         HashMap<String, String> generalAssumptions,HashMap<String, String> InvestmentAssumptions, Person[] pList) throws IOException {
        this.pList = pList;
        this.GeneralAssumptions = generalAssumptions;
        this.JointAssum=jointAssum;
        this.JointAVs = jointAVs;
        this.InvestmentAssumptions =InvestmentAssumptions;

        this.projectionYears=Integer.valueOf(GeneralAssumptions.get("ProjectionYears"));
        this.numIndividuals=pList.length;
        tc = new TaxCalculator(Double.valueOf(GeneralAssumptions.get("TaxBracketInflation")));
        numScenarios = Integer.valueOf(InvestmentAssumptions.get("ScenarioCount"));
        //scenarios = ScenarioGen.GenerateScenarios(numScenarios, projectionYears, InvestmentAssumptions);

        individualCalcs = new double[numIndividuals][21][projectionYears+1];
        jointCalcs = new double[21][projectionYears+1];
        accountContrib = new double[numIndividuals+1][10][projectionYears+1];
        accountContribEmployer = new double[numIndividuals+1][10][projectionYears+1];
        accountValuesIndiv = new double[numIndividuals][7][6][projectionYears+1];
        accountValuesJoint = new double[7][6][projectionYears+1];
        jointIncomeAllocation = new double[8][projectionYears+1];

        AssumptionReader ar = new AssumptionReader();
        ar.loadStringArray(accountMetaData, "Data/Account_Types.txt");


        long start =0;
        long end = 0;

        //start = System.currentTimeMillis();
        //for(int j = 0; j<1000; j++) {
            for (int i = 0; i <= projectionYears; i++) {
                jointCalcs[0][i] = i;
                calcValues(i);
            }
        //}
        //end = System.currentTimeMillis();
        //System.out.println(end - start);


        //start = System.currentTimeMillis();
        //for(int j = 0; j<1000; j++){
            for(int i = 0; i<=projectionYears; i++) {
                calcAVs(i);
            }
        //}
        //end = System.currentTimeMillis();
        //System.out.println(end - start);

    }

    private void calcAVs(int year){
        loadPriorAVs(year);
        allocateJointExcessIncome(year);
        allocateJointExcessExpense(year);
        projectAVs(year);
    }
    private void calcValues(int year) throws IOException {
        calcAge(year,0,0);
        calcSalary(year,params.SALARY_COL,params.SALARY_COL);
        calcSSTax(year,2,2);
        calcMedicareTax(year,3,3);
        calcRetirementContrib(year, 4,4);
        calcPreTaxRetirementContrib(year,5,5);
        calcPostTaxRetirementContrib(year,6,6);
        calcRetirementContribEmployer(year, 7, 7);
        calcHealthCareExpense(year, 8, 8);
        calcFedTax(year, 11, 9);
        calcStateTax(year, 12, 10);
        calcAdjFedTax(year, 9, 0);
        calcAdjStateTax(year, 10, 0);
        calcNonTaxableIncome(year, 13, 13);
        calcDisposableIncome(year, 14, 14);
        calcPersonalExpenses(year, 15, 15);
        calcJointIncomeContrib(year, 16, 16);
        calcJointExpenses(year, 17, 17);
        calcRentExpense(year, 18, 18);
        calcMortgageExpense(year, 19, 19);
        calcJointExcessIncome(year, 20, 20);

    }
    private void projectSS() throws IOException {
        for(int i = 0; i<numIndividuals; i++) {
            pList[i].projectSSBenefits();
        }
    }

    private void calcAge(int year, int c1, int c2) {
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = pList[i].getAge(year);
        }
    }
    private void calcSalary(int year, int c1, int c2) {
        double rollingTotal =0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = pList[i].calcIncome(year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcSSTax(int year, int c1, int c2) {
        double rollingTotal =0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = tc.calcSSTax(individualCalcs[i][params.SALARY_COL][year], year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcMedicareTax(int year, int c1, int c2) {
        double rollingTotal =0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = tc.calcMedicareTax(individualCalcs[i][params.SALARY_COL][year], year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }
    private void calcRetirementContrib(int year, int c1, int c2) {
        double rollingTotal =0;
        double subTotal = 0;
        int lastJ = 0;
        for(int i = 0; i<=numIndividuals; i++) {
            if(i == numIndividuals)
                accountContrib[i][lastJ][year]=  subTotal;
            else {
                subTotal = 0;
                for(int j =0; j<7; j++) {
                    accountContrib[i][j][year] = pList[i].calcRetirementContrib(year, j, accountMetaData.get(j)[0], individualCalcs[i][params.SALARY_COL][year]);
                    subTotal+=accountContrib[i][j][year];
                    lastJ = j;
                }
                individualCalcs[i][c1][year] = subTotal;
                rollingTotal+=subTotal;
            }
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcPreTaxRetirementContrib(int year, int c1, int c2) {
        double rollingTotal =0;
        double subTotal = 0;
        for(int i = 0; i<numIndividuals; i++) {
            subTotal =0;
            for(int j =0; j<7; j++) {
                if(accountMetaData.get(j)[1].equals("0") && accountMetaData.get(j)[2].equals("1")) {
                    subTotal+=accountContrib[i][j][year];
                }
            }
            individualCalcs[i][c1][year] = subTotal;
            rollingTotal+=subTotal;
        }
        jointCalcs[c2][year] = rollingTotal;
    }
    private void calcPostTaxRetirementContrib(int year, int c1, int c2) {
        double rollingTotal =0;
        double subTotal = 0;
        for(int i = 0; i<numIndividuals; i++) {
            subTotal =0;
            for(int j =0; j<7; j++) {
                if(accountMetaData.get(j)[1].equals("1") && accountMetaData.get(j)[2].equals("1")) {
                    subTotal+=accountContrib[i][j][year];
                }
            }
            individualCalcs[i][c1][year] = subTotal;
            rollingTotal+=subTotal;
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcRetirementContribEmployer(int year, int c1, int c2) {
        double rollingTotal =0;
        double subTotal = 0;
        int lastJ = 0;
        for(int i = 0; i<=numIndividuals; i++) {
            if(i == numIndividuals)
                accountContribEmployer[i][lastJ][year]=  subTotal;
            else {
                subTotal = 0;
                for(int j =0; j<7; j++) {
                    accountContribEmployer[i][j][year] = pList[i].calcRetirementContribEmployer(year, j,accountMetaData.get(j)[0], individualCalcs[i][params.SALARY_COL][year], accountContrib[i][j][year]);
                    subTotal+=accountContribEmployer[i][j][year];
                    lastJ = j;
                }
                individualCalcs[i][c1][year] = subTotal;
                rollingTotal+=subTotal;
            }
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcHealthCareExpense(int year, int c1, int c2) {
        double rollingTotal =0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = pList[i].calcHealthcareCosts(year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcFedTax(int year, int c1, int c2) {
        double rollingTotal =0;
        double taxableIncome = 0;
        for(int i = 0; i<numIndividuals; i++) {
            taxableIncome = individualCalcs[i][params.SALARY_COL][year] - individualCalcs[i][5][year]-individualCalcs[i][8][year];
            individualCalcs[i][c1][year] = tc.calcFederalTaxSingle(taxableIncome, year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        taxableIncome = jointCalcs[params.SALARY_COL][year] - jointCalcs[5][year]-jointCalcs[8][year];
        jointCalcs[c2][year] = tc.calcFederalTaxJoint(taxableIncome, year);
    }

    private void calcStateTax(int year, int c1, int c2) {
        double rollingTotal =0;
        double taxableIncome = 0;
        for(int i = 0; i<numIndividuals; i++) {
            taxableIncome = individualCalcs[i][params.SALARY_COL][year] - individualCalcs[i][5][year]-individualCalcs[i][8][year];
            individualCalcs[i][c1][year] = tc.calcStateTaxSingle(taxableIncome, year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        taxableIncome = jointCalcs[params.SALARY_COL][year] - jointCalcs[5][year]-jointCalcs[8][year];
        jointCalcs[c2][year] = tc.calcStateTaxJoint(taxableIncome, year);
    }

    private void calcAdjFedTax(int year, int c1, int c2) {
        double IndividualTaxTotal =0;
        double JointTaxTotal = jointCalcs[11][year];


        for(int i = 0; i<numIndividuals; i++) {
            IndividualTaxTotal += individualCalcs[i][11][year];
        }

        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = individualCalcs[i][11][year] * JointTaxTotal /  IndividualTaxTotal;
        }
    }
    private void calcAdjStateTax(int year, int c1,  int c2) {
        double IndividualTaxTotal =0;
        double JointTaxTotal = jointCalcs[12][year];


        for(int i = 0; i<numIndividuals; i++) {
            IndividualTaxTotal += individualCalcs[i][12][year];
        }

        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = individualCalcs[i][12][year] * JointTaxTotal /  IndividualTaxTotal;
        }
    }
    private void calcNonTaxableIncome(int year, int c1, int c2) throws IOException {
        double rollingTotal = 0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = pList[i].calcNonTaxableIncome(year);
            rollingTotal += individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcDisposableIncome(int year, int c1, int c2) {
        double income = 0;
        double investment = 0;
        double taxes = 0;
        double preTaxExpense = 0;

        double totalIndividualTaxFed = 0;
        double totalIndividualTaxState = 0;
        for(int i = 0; i<numIndividuals; i++) {
            totalIndividualTaxFed += individualCalcs[i][11][year];
            totalIndividualTaxState += individualCalcs[i][12][year];
        }
        for(int i = 0; i<numIndividuals; i++) {
            if(totalIndividualTaxFed == 0)
                individualCalcs[i][9][year] = 0;
            else
                individualCalcs[i][9][year] = individualCalcs[i][11][year] * jointCalcs[9][year] / totalIndividualTaxFed;
            if(totalIndividualTaxState == 0)
                individualCalcs[i][10][year] = 0;
            else
                individualCalcs[i][10][year] = individualCalcs[i][12][year] * jointCalcs[10][year] / totalIndividualTaxState;
        }
        for(int i = 0; i<numIndividuals; i++) {
            income = individualCalcs[i][1][year] + individualCalcs[i][13][year];
            investment = individualCalcs[i][4][year];
            taxes = individualCalcs[i][2][year] + individualCalcs[i][3][year]+individualCalcs[i][9][year] + individualCalcs[i][10][year];
            preTaxExpense = individualCalcs[i][8][year];
            individualCalcs[i][c1][year] = income-investment-taxes-preTaxExpense;
        }
        income = jointCalcs[1][year] + jointCalcs[13][year];
        investment = jointCalcs[4][year];
        taxes = jointCalcs[2][year] + jointCalcs[3][year]+jointCalcs[9][year] + jointCalcs[10][year];
        preTaxExpense = jointCalcs[8][year];

        jointCalcs[c2][year] = income-investment-taxes-preTaxExpense;
    }

    private void calcPersonalExpenses(int year, int c1, int c2) {
        double rollingTotal = 0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = pList[i].calcPersonalExpenses(year);
            rollingTotal+=individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }

    private void calcJointIncomeContrib(int year, int c1, int c2) {
        double rollingTotal = 0;
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = individualCalcs[i][14][year]-individualCalcs[i][15][year];
            rollingTotal+=individualCalcs[i][c1][year];
        }
        jointCalcs[c2][year] = rollingTotal;
    }
    private void calcJointExpenses(int year, int c1, int c2) {
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = 0;
        }

        double inflation = Double.valueOf(GeneralAssumptions.get("ExpenseInflation"));
        double initialExpenses = Double.valueOf(JointAssum.get("LivingExpenses_monthly"));
        double frequency = 12.0;
        jointCalcs[c2][year]= frequency * initialExpenses * Math.pow(1+inflation, year);
    }
    private void calcRentExpense(int year, int c1, int c2) {
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = 0;
        }
        double inflation = Double.valueOf(GeneralAssumptions.get("ExpenseInflation"));
        double initialExpenses = Double.valueOf(JointAssum.get("HousingExpenses_monthly"));
        double frequency = 12.0;
        jointCalcs[c2][year]= frequency * initialExpenses * Math.pow(1+inflation, year);
    }
    private void calcMortgageExpense(int year, int c1, int c2) {
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = 0;
        }
        jointCalcs[c2][year]= 0;
    }
    private void calcJointExcessIncome(int year, int c1, int c2) {
        for(int i = 0; i<numIndividuals; i++) {
            individualCalcs[i][c1][year] = 0;
        }
        double expenses = jointCalcs[17][year]+jointCalcs[18][year]+jointCalcs[19][year];
        jointCalcs[c2][year]= jointCalcs[16][year]-expenses;
    }

    //0 - Joint Debt Paydown
    //1 - Joint Savings
    //2 - Joint Taxable Investment
    //3 - Expense overflow


    private void allocateJointExcessIncome(int year) {
        //if negative excess, allocate to excess expense
        //if debt > 0, paydown debt
        //if target savings < actual savings, put in savings
        //if leftover, put in taxable

        double excess_income = jointCalcs[20][year];
        double alloc_debt = 0;
        double alloc_savings = 0;
        double alloc_taxable = 0;
        double alloc_expense = 0;

        if(excess_income<0) {
            alloc_expense = excess_income;
        }

        double targetSavings = jointCalcs[1][year] * 6.0 / 12; //change to expenses

        int debt_acct_num = 6;
        int savings_acct_num = 4;


        if(excess_income > 0 && accountValuesJoint[debt_acct_num][0][year] > 0)
            alloc_debt = Math.min(excess_income, accountValuesJoint[debt_acct_num][0][year]);
        excess_income -= alloc_debt;

        if(excess_income > 0 && accountValuesJoint[savings_acct_num][0][year] < targetSavings)
            alloc_savings = Math.min(excess_income, targetSavings - accountValuesJoint[savings_acct_num][0][year]);
        excess_income -= alloc_savings;

        if(excess_income > 0)
            alloc_taxable = excess_income;

        jointIncomeAllocation[6][year] = -alloc_debt;
        jointIncomeAllocation[4][year] = alloc_savings;
        jointIncomeAllocation[5][year] = alloc_taxable;
        jointIncomeAllocation[7][year] = -alloc_expense;
    }
    private void allocateJointExcessExpense(int year){
        double excess_expense = jointIncomeAllocation[7][year];
        double etrpb = 0.25; //effective tax rate post retirement

        //Take from accounts in this order:
        //Savings
        //Roth IRA
        //Roth 401k
        //Trad IRA
        //Trad 401k
        //Taxable
        //If still excess, add to debt

        double alloc_savings = 0;
        double alloc_roth_ira = 0;
        double alloc_roth_401k = 0;
        double alloc_trad_ira = 0;
        double alloc_trad_401k = 0;
        double alloc_taxable = 0;
        double alloc_debt = 0;

        if(excess_expense > 0){
            if(excess_expense > 0)
                alloc_trad_401k = Math.min(accountValuesJoint[3][0][year] / (1+ etrpb), excess_expense);
            excess_expense -= alloc_trad_401k;
            if(excess_expense > 0)
                alloc_roth_ira = Math.min(accountValuesJoint[0][0][year], excess_expense);
            excess_expense -= alloc_roth_ira;
            if(excess_expense > 0)
                alloc_savings = Math.min(accountValuesJoint[4][0][year], excess_expense);
            excess_expense -= alloc_savings;
            if(excess_expense > 0)
                alloc_roth_401k = Math.min(accountValuesJoint[2][0][year], excess_expense);
            excess_expense -= alloc_roth_401k;
            if(excess_expense > 0)
                alloc_trad_ira = Math.min(accountValuesJoint[1][0][year] / (1+ etrpb), excess_expense);
            excess_expense -= alloc_trad_ira;
            if(excess_expense > 0)
                alloc_taxable = Math.min(accountValuesJoint[5][0][year] / (1+ etrpb), excess_expense);
            excess_expense -= alloc_taxable;

            if(excess_expense > 0)
                alloc_debt = excess_expense;


            //This is wrong - fix later
            jointIncomeAllocation[0][year] = -alloc_roth_ira;
            jointIncomeAllocation[1][year] = -alloc_trad_ira / (1 - etrpb);
            jointIncomeAllocation[2][year] = -alloc_roth_401k;
            jointIncomeAllocation[3][year] = -alloc_trad_401k / (1 - etrpb);
            jointIncomeAllocation[4][year] = -alloc_savings;
            jointIncomeAllocation[5][year] = -alloc_taxable / (1 - etrpb);
            jointIncomeAllocation[6][year] = alloc_debt;
        }

    }

    private void loadPriorAVs(int year){
        for(int i = 0; i<numIndividuals; i++) {
            for(int j =0; j<7; j++) {

                double prior_value = 0;
                if(year == 0)
                    prior_value = pList[i].calcInitialRetirementBalance(year,accountMetaData.get(j)[0]);
                else
                    prior_value = accountValuesIndiv[i][j][5][year-1];

                accountValuesIndiv[i][j][0][year] = prior_value;
            }
        }

        for(int j =0; j<7; j++) {

            double prior_value = 0;
            if(year == 0)
                prior_value= Double.valueOf(JointAVs.get(accountMetaData.get(j)[0]));
            else
                prior_value = accountValuesJoint[j][5][year-1];

            accountValuesJoint[j][0][year] = prior_value;
        }
    }
    private double getAccountReturn(String acctName, double eq_pct_initial, double eq_pct_retirement, int current_yr, int retirement_yr, int grading_yrs){
        double equity_return = Double.valueOf(InvestmentAssumptions.get("EquityReturn_Mean"));
        double bond_return = Double.valueOf(InvestmentAssumptions.get("BondReturn_Mean"));
        double total_return = 0;
        double pct_equity = eq_pct_initial;
        int yrs_untiL_retire = retirement_yr - current_yr;

        if(yrs_untiL_retire <= 0)
            pct_equity = eq_pct_retirement;
        else if(yrs_untiL_retire < grading_yrs)
            pct_equity = eq_pct_retirement + (eq_pct_initial - eq_pct_retirement) * yrs_untiL_retire / grading_yrs;

        if(acctName.equals("Savings"))
            total_return = 0;
        else if(acctName.equals("Debt"))
            total_return = 0.15;
        else
            total_return = equity_return * pct_equity + bond_return * (1-pct_equity);
        return total_return;
    }

    //0 = Prior
    //1 = Contribution
    //2 = Timing (0 = BOY, 1 = Mid, 2 = EOY)
    //3 = Return
    //4 = Transfers (always EOY)
    //5 = Ending
    private void projectAVs(int year) {
        for(int i = 0; i<numIndividuals; i++) {
            for(int j =0; j<7; j++) {

                double eq_pct_initial = Double.valueOf(InvestmentAssumptions.get("InitialAllocationPct_Equity"));
                double eq_pct_retirement = Double.valueOf(InvestmentAssumptions.get("RetirementAllocationPct_Equity"));
                int retirement_yr = pList[i].retirementYear();
                int grading_yrs = Integer.valueOf(InvestmentAssumptions.get("GradingPeriod"));


                double prior_value = accountValuesIndiv[i][j][0][year];
                double contribution = accountContrib[i][j][year]+accountContribEmployer[i][j][year];
                int timing = 1;
                double account_return = getAccountReturn(accountMetaData.get(j)[0], eq_pct_initial, eq_pct_retirement, year, retirement_yr, grading_yrs);
                double transfer = 0;
                double ending_value = 0;

                if(timing == 0)
                    ending_value = prior_value * (1+account_return) + contribution * (1+account_return);
                else if(timing== 1)
                    ending_value = prior_value * (1+account_return) + contribution * Math.pow(1+account_return, 0.5);
                else
                    ending_value = prior_value * (1+account_return) + contribution;

                if(year == pList[i].retirementYear()-1){
                    transfer = ending_value;
                    ending_value = 0;
                }

                accountValuesIndiv[i][j][1][year] = contribution;
                accountValuesIndiv[i][j][2][year] = timing;
                accountValuesIndiv[i][j][3][year] = account_return;
                accountValuesIndiv[i][j][4][year] = transfer;
                accountValuesIndiv[i][j][5][year] = ending_value;
            }
        }

        for(int j =0; j<7; j++) {

            double prior_value = accountValuesJoint[j][0][year];
            double contribution = jointIncomeAllocation[j][year];
            int timing = 1; //use BOY for Joint. This is because prior year values are used as inputs to contribution. Without BOY, account values could go negative unintentionally.
            double account_return = 0;
            double transfer = 0;
            double ending_value = 0;

            double wgt_return = 0;
            double total_Av = 0;
            for(int i = 0; i<numIndividuals; i++) {
                total_Av+= accountValuesIndiv[i][j][5][year];
                wgt_return+= accountValuesIndiv[i][j][5][year] * accountValuesIndiv[i][j][3][year];
            }

            //This logic isn't right. Need to take the weighted average of what would be in the joint acct
            if(total_Av == 0)
                account_return = accountValuesIndiv[0][j][3][year];
            else
                account_return = wgt_return / total_Av;

            for(int i = 0; i<numIndividuals; i++) {
                transfer += accountValuesIndiv[i][j][4][year];
            }


            if(accountMetaData.get(j)[0].equals("Trad 401k") && year == 30){
                int x = 0;
            }
            if(timing == 0)
                ending_value = prior_value * (1+account_return) + contribution * (1+account_return) + transfer;
            else if(timing== 1)
                ending_value = prior_value * (1+account_return) + contribution * Math.pow(1+account_return, 0.5) + transfer;
            else
                ending_value = prior_value * (1+account_return) + contribution + transfer;

            accountValuesJoint[j][1][year] = contribution;
            accountValuesJoint[j][2][year] = timing;
            accountValuesJoint[j][3][year] = account_return;
            accountValuesJoint[j][4][year] = transfer;
            accountValuesJoint[j][5][year] = ending_value;
        }


    }

    public String printData(int year) {
        String toRet = ""+year;
        for(int c1 = 1; c1<=7; c1++) {
            toRet = toRet.concat(","+jointCalcs[c1][year]);
        }
        return toRet;
    }

    public String printDataIndiv(int year, int indNum) {
        String toRet = ""+year;
        for(int c1 = 1; c1<=7; c1++) {
            toRet = toRet.concat(","+individualCalcs[indNum][c1][year]);
        }
        return toRet;
    }

    public void writeJointData() throws IOException {
        OutputWriter ow = new OutputWriter();
        String[] fieldNames = {"Year","Salary","Tax_SS","Tax_Medicare","RetirementContrib","RetirementContrib_PreTax","RetirementContrib_PostTax","RetirementContrib_Employer","Expense_Healthcare","Tax_Federal","Tax_State","Tax_FedAdj","Tax_StateAdj","Income_NonTaxable","Income_Disposable","Expenses_Personal","Income_JointContrib","Expenses_Joint","Expenses_Rent","Expenses_Mortgage","Income_JointExcess"};
        ow.writeJointData(jointCalcs, "/Users/joesplettstoeser/Desktop/Repos/FinancialProjection/src/main/Output/Output_Joint.csv",fieldNames);
    }

    public void writePerson1AVs() throws IOException {
        OutputWriter ow = new OutputWriter();
        String[] fieldNames = {"Year","Prior","Contribution","Timing","Return","Transfers","Ending"};
        ow.writePersonAVs(accountValuesIndiv, 0, "/Users/joesplettstoeser/Desktop/Repos/FinancialProjection/src/main/Output/Output_Person1_AVs.csv",fieldNames);
    }

    public void writeAVs() throws IOException {
        OutputWriter ow = new OutputWriter();
        String[] fieldNames = {"Year","Roth IRA","Trad IRA","Roth 401k","Trad 401k","Savings","Taxable","Debt"};
        ow.writeAVs(accountValuesIndiv, accountValuesJoint, "/Users/joesplettstoeser/Desktop/Repos/FinancialProjection/src/main/Output/Output_AVs.csv",fieldNames);
    }



}