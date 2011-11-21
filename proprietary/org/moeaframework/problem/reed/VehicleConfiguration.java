package org.moeaframework.problem.reed;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class VehicleConfiguration extends AbstractProblem {

	public VehicleConfiguration() {
		super(11, 5);
	}

	public void evaluate(Solution solution) {
		final float VarA = (float)((RealVariable)solution.getVariable(0))
				.getValue();
		final float VarB = (float)((RealVariable)solution.getVariable(1))
				.getValue();
		final float VarC = (float)((RealVariable)solution.getVariable(2))
				.getValue();
		final float VarD = (float)((RealVariable)solution.getVariable(3))
				.getValue();
		final float VarE = (float)((RealVariable)solution.getVariable(4))
				.getValue();
		final float VarF = (float)((RealVariable)solution.getVariable(5))
				.getValue();
		final float VarG = (float)((RealVariable)solution.getVariable(6))
				.getValue();
		final int VarH = (int)((RealVariable)solution.getVariable(7))
				.getValue();
		final float VarI = (float)((RealVariable)solution.getVariable(8))
				.getValue();
		final float VarJ = (float)((RealVariable)solution.getVariable(9))
				.getValue();
		final float VarK = (float)((RealVariable)solution.getVariable(10))
				.getValue();

		// Define Baseline Performance Values
		final double BaselineCurbWeight = 3440.0;
		final double BaselineTa = 5.172;
		final double BaselineFc = 23.283;
		final double BaselineW3 = 1481.2;
		final double BaselineH61 = 975.56;
		final double BaselineV1 = 15.648;

		// Define Input Value Ranges & Scale Inputs
		final double DrXLow = 3050.0;
		final double DrXHigh = 3200.0;
		final double DrZLow = 220.0;
		final double DrZHigh = 250.0;
		final double FrontAxleHorLow = 73.0;
		final double FrontAxleHorHigh = 80.0;
		final double FrontTrackLow = 1450.0;
		final double FrontTrackHigh = 1600.0;
		final double PsXLow = 3920.0;
		final double PsXHigh = 4020.0;
		final double PsZLow = 220.0;
		final double PsZHigh = 250.0;
		final double RearAxleHorLow = 47.0;
		final double RearAxleHorHigh = 70.0;
		final double RearOverhangLow = 700.0;
		final double RearOverhangHigh = 1300.0;
		final double VehicleHeightLow = 1400.0;
		final double VehicleHeightHigh = 1550.0;
		final double WheelbaseLow = 2500.0;
		final double WheelbaseHigh = 3100.0;

		double DrX = DrXLow + VarE * (DrXHigh - DrXLow);
		double DrZ = DrZLow + VarF * (DrZHigh - DrZLow);
		double Engine = VarH;
		double FrontAxleHor = FrontAxleHorLow + VarC
				* (FrontAxleHorHigh - FrontAxleHorLow);
		double FrontTrack = FrontTrackLow + VarJ
				* (FrontTrackHigh - FrontTrackLow);
		double PsX = PsXLow + VarG * (PsXHigh - PsXLow);
		double PsZ = PsZLow + VarI * (PsZHigh - PsZLow);
		double RearAxleHor = RearAxleHorLow + VarD
				* (RearAxleHorHigh - RearAxleHorLow);
		double RearOverhang = RearOverhangLow + VarB
				* (RearOverhangHigh - RearOverhangLow);
		double VehicleHeight = VehicleHeightLow + VarK
				* (VehicleHeightHigh - VehicleHeightLow);
		double Wheelbase = WheelbaseLow + VarA * (WheelbaseHigh - WheelbaseLow);

		// Constraint Information

		// Compute Diag Head Clearance Constraint

		double violation2 = 0.0;

		double B28 = 24.0;
		double B24 = 75.0;
		double B25 = 38.0;

		double B19 = VehicleHeight;

		double B20 = DrZ + 270.0;

		double B23 = VehicleHeight - B20 - 100.0;

		double DiagHeadClearance;
		DiagHeadClearance = (B23 - (899.16 * Math.cos(B28 * 3.14159 / 180.0)))
				/ Math.sin(30 * 3.141519 / 180.0);

		if (DiagHeadClearance < 25.0)
			violation2 = 25.0 - DiagHeadClearance;

		// Compute Engine Constraint

		double violation1 = 0;

		if (Engine == 1) {
			if (Wheelbase < 2500)
				violation1 = 2500 - Wheelbase;
			if (Wheelbase > 2880)
				violation1 = Wheelbase - 2880;
		}
		if (Engine == 2) {
			if (Wheelbase < 2660)
				violation1 = 2660 - Wheelbase;
			if (Wheelbase > 2880)
				violation1 = Wheelbase - 2880;
		}
		if (Engine == 3) {
			if (Wheelbase < 2660)
				violation1 = 2660 - Wheelbase;
			if (Wheelbase > 2880)
				violation1 = Wheelbase - 2880;
		}
		if (Engine == 4) {
			if (Wheelbase < 2660)
				violation1 = 2660 - Wheelbase;
			if (Wheelbase > 2880)
				violation1 = Wheelbase - 2880;
		}
		if (Engine == 5) {
			if (Wheelbase < 2760)
				violation1 = 2760 - Wheelbase;
			if (Wheelbase > 3000)
				violation1 = Wheelbase - 3000;
		}
		if (Engine == 6) {
			if (Wheelbase < 2840)
				violation1 = 2840 - Wheelbase;
			if (Wheelbase > 3100)
				violation1 = Wheelbase - 3100;
		}

		// Compute WidthLB constraint

		double reartrack;
		reartrack = 1.0904 * FrontTrack - 143.94;

		double width;
		width = 78.5145 + 0.089 * FrontTrack + 1.0575 * reartrack;

		double violation3 = 0.0;
		double lowerb;

		lowerb = 0.526 * Wheelbase + 303.41;

		double testp;

		testp = lowerb - width;

		if (testp > 0)
			violation3 = testp;

		// Compute WidthUB constraint

		reartrack = 1.0904 * FrontTrack - 143.94;
		width = 78.5145 + 0.089 * FrontTrack + 1.0575 * reartrack;

		double violation4 = 0;
		double upperb;

		upperb = 0.629 * Wheelbase + 185.515;

		testp = width - upperb;

		if (testp > 0)
			violation4 = testp;

		double TotalViolation = violation1 + violation2 + violation3
				+ violation4;

		// Objective Information

		// Compute Accelaration (Accel5070) Objective

		double TireSec = 0.0;

		if (Wheelbase >= 2500 && Wheelbase < 2625)
			TireSec = 175.0;
		if (Wheelbase >= 2625 && Wheelbase < 2700)
			TireSec = 195.0;
		if (Wheelbase >= 2700 && Wheelbase < 2825)
			TireSec = 215.0;
		if (Wheelbase >= 2825 && Wheelbase < 2925)
			TireSec = 235.0;
		if (Wheelbase >= 2925)
			TireSec = 245.0;

		double AvgTrack;

		reartrack = 1.0904 * FrontTrack - 143.94;
		AvgTrack = ((FrontTrack + reartrack) / 2.0) / 1000.0;

		width = 78.5145 + 0.089 * FrontTrack + 1.0575 * reartrack;

		double ChassisArea;

		ChassisArea = (Wheelbase / 1000.0) * AvgTrack;

		double Displacement = 0.0;

		if (Engine == 1)
			Displacement = 2.2;
		if (Engine == 2)
			Displacement = 3.4;
		if (Engine == 3)
			Displacement = 3.5;
		if (Engine == 4)
			Displacement = 3.6;
		if (Engine == 5)
			Displacement = 3.8;
		if (Engine == 6)
			Displacement = 4.6;

		double CurbWeight;

		CurbWeight = (0.2055 * ((Wheelbase - 2663.2) / 125.4) + 0.2016
				* ((AvgTrack - 1.4880) / 0.0545) + 0.2131
				* ((ChassisArea - 3.9678) / 0.3165) + 0.1969
				* ((Displacement - 2.6876) / 0.8654) + 0.1930 * ((TireSec - 201.43) / 17.14)) * 211.7 + 1381.2;
		CurbWeight = CurbWeight * 2.205;

		double AE4 = CurbWeight;

		double B30 = 0.0;

		if (Engine == 1)
			B30 = 35.0;
		if (Engine == 2)
			B30 = 35.0;
		if (Engine == 3)
			B30 = 25.0;
		if (Engine == 4)
			B30 = 25.0;
		if (Engine == 5)
			B30 = 25.0;
		if (Engine == 6)
			B30 = 40.0;

		double B37 = 0.0;

		if (Engine == 1)
			B37 = 0.96;
		if (Engine == 2)
			B37 = 0.97;
		if (Engine == 3)
			B37 = 0.96;
		if (Engine == 4)
			B37 = 0.96;
		if (Engine == 5)
			B37 = 0.96;
		if (Engine == 6)
			B37 = 0.93;

		double W4 = 0.0;

		if (Engine == 1)
			W4 = 137.0;
		if (Engine == 2)
			W4 = 180.0;
		if (Engine == 3)
			W4 = 200.0;
		if (Engine == 4)
			W4 = 250.0;
		if (Engine == 5)
			W4 = 205.0;
		if (Engine == 6)
			W4 = 275.0;

		double B29 = 0.007;

		double AG4 = 0.0;

		if (Engine == 1)
			AG4 = 0.381;
		if (Engine == 2)
			AG4 = 0.3570;
		if (Engine == 3)
			AG4 = 0.39;
		if (Engine == 4)
			AG4 = 0.39;
		if (Engine == 5)
			AG4 = 0.33;
		if (Engine == 6)
			AG4 = 0.30;

		double B38 = 0.0;

		if (Engine == 1)
			B38 = 0.704;
		if (Engine == 2)
			B38 = 0.62;
		if (Engine == 3)
			B38 = 0.64;
		if (Engine == 4)
			B38 = 0.64;
		if (Engine == 5)
			B38 = 0.64;
		if (Engine == 6)
			B38 = 0.63;

		double AH4;

		AH4 = 0.83618 * VehicleHeight * 0.03937 * width * 0.03937
				* (0.0006451625806477) + 0.012;

		double BT1 = 31.286;
		double BR1 = 22.347;

		double AF4 = B38 * B37;

		double AJ4 = 0.5 * 1.16 * AG4 * AH4;

		double AI4 = AE4 / 2.204;

		double AP4 = -1.0 * AJ4;

		double AK4 = (AF4 * W4 * 745.7) / (2.0 * AJ4);

		double temp1 = (9.8 * B29 * AI4 + B30) / (3.0 * AJ4);

		double AL4 = Math.sqrt(Math.pow(temp1, 3.0) + Math.pow(AK4, 2.0));

		double temp2 = AL4 + AK4;
		double temp3 = AL4 - AK4;

		double AM4 = Math.pow(temp2, 0.33) - Math.pow(temp3, 0.33);

		double AQ4 = -1.0 * AJ4 * AM4;

		double AR4 = (-1.0 * W4 * 745.7 * AF4) / AM4;

		double temp4 = AJ4 * AM4;
		double AO4 = Math.pow(temp4, 2.0) - (4.0 * AJ4 * AF4 * W4 * 745.7)
				/ AM4;
		double AT4 = 2.0 * AP4 * BT1 + AQ4;
		double AU4 = 2.0 * AP4 * BR1 + AQ4;

		double AS4;

		// AS4 =
		// Math.log(Math.floor(Math.abs(BT1-AM4)))-Math.log(Math.floor(Math.abs(BR1-AM4)))-0.5*Math.log(Math.floor(Math.abs(AP4*Math.pow(BT1,2.0)+AQ4*BT1+AR4)))+0.5*Math.log(Math.floor(Math.abs(AP4*Math.pow(BR1,2.0)+AQ4*BR1+AR4)));

		AS4 = Math.log(Math.abs(BT1 - AM4))
				- Math.log(Math.abs(BR1 - AM4))
				- 0.5
				* Math.log(Math.abs(AP4 * Math.pow(BT1, 2.0) + AQ4 * BT1 + AR4))
				+ 0.5
				* Math.log(Math.abs(AP4 * Math.pow(BR1, 2.0) + AQ4 * BR1 + AR4));

		double AV4;
		AV4 = 2.0
				* (AR4 / AM4 + AQ4 / 2.0)
				* (Math.atan(AT4 / Math.sqrt(-1.0 * AO4)) - Math.atan(AU4
						/ Math.sqrt(-1.0 * AO4))) / Math.sqrt(-1.0 * AO4);

		double accel;
		accel = (AI4 * AM4 / (AP4 * AM4 * AM4 + AQ4 * AM4 + AR4)) * (AS4 + AV4);

		// Compute Cargo Volume Objective

		reartrack = 1.0904 * FrontTrack - 143.94;

		width = 78.5145 + 0.089 * FrontTrack + 1.0575 * reartrack;
		double B12, B2;

		B12 = width;

		B2 = RearOverhang;

		double CargoVolume;

		CargoVolume = -25.2 + 0.0174 * B12 + 0.00811 * B2;

		// Compute Front Headroom Objective

		B28 = 24.0;
		B24 = 37.0;
		B25 = 38.0;

		B19 = VehicleHeight;
		B20 = DrZ + 270.0;

		double FrHR;

		FrHR = ((899.16 * Math.cos(B28 * 3.14159 / 180.0) + (B19 - B20
				- (899.16 * Math.cos(B28 * 3.14159 / 180.0)) - B24 - B25)) / Math
				.cos(8.0 * 3.14159 / 180.0)) + 101.6;

		// Compute Fuel Economy Objective

		if (Wheelbase >= 2500 && Wheelbase < 2625)
			TireSec = 175.0;
		if (Wheelbase >= 2625 && Wheelbase < 2700)
			TireSec = 195.0;
		if (Wheelbase >= 2700 && Wheelbase < 2825)
			TireSec = 215.0;
		if (Wheelbase >= 2825 && Wheelbase < 2925)
			TireSec = 235.0;
		if (Wheelbase >= 2925)
			TireSec = 245.0;

		reartrack = 1.0904 * FrontTrack - 143.94;
		AvgTrack = ((FrontTrack + reartrack) / 2.0) / 1000.0;

		width = 78.5145 + 0.089 * FrontTrack + 1.0575 * reartrack;

		ChassisArea = (Wheelbase / 1000.0) * AvgTrack;

		if (Engine == 1)
			Displacement = 2.2;
		if (Engine == 2)
			Displacement = 3.4;
		if (Engine == 3)
			Displacement = 3.5;
		if (Engine == 4)
			Displacement = 3.6;
		if (Engine == 5)
			Displacement = 3.8;
		if (Engine == 6)
			Displacement = 4.6;

		CurbWeight = (0.2055 * ((Wheelbase - 2663.2) / 125.4) + 0.2016
				* ((AvgTrack - 1.4880) / 0.0545) + 0.2131
				* ((ChassisArea - 3.9678) / 0.3165) + 0.1969
				* ((Displacement - 2.6876) / 0.8654) + 0.1930 * ((TireSec - 201.43) / 17.14)) * 211.7 + 1381.2;
		CurbWeight = CurbWeight * 2.205;

		AE4 = CurbWeight;

		double B33 = 1100.0;
		double B32 = 730.0;

		if (Engine == 1)
			B30 = 35.0;
		if (Engine == 2)
			B30 = 35.0;
		if (Engine == 3)
			B30 = 25.0;
		if (Engine == 4)
			B30 = 25.0;
		if (Engine == 5)
			B30 = 25.0;
		if (Engine == 6)
			B30 = 40.0;

		if (Engine == 1)
			B37 = 0.96;
		if (Engine == 2)
			B37 = 0.97;
		if (Engine == 3)
			B37 = 0.96;
		if (Engine == 4)
			B37 = 0.96;
		if (Engine == 5)
			B37 = 0.96;
		if (Engine == 6)
			B37 = 0.93;

		double B31 = 0.0;

		if (Engine == 1)
			B31 = 0.78;
		if (Engine == 2)
			B31 = 0.821;
		if (Engine == 3)
			B31 = 0.82;
		if (Engine == 4)
			B31 = 0.82;
		if (Engine == 5)
			B31 = 0.82;
		if (Engine == 6)
			B31 = 0.8;

		double B36 = 0.0;

		if (Engine == 1)
			B36 = 137;
		if (Engine == 2)
			B36 = 180;
		if (Engine == 3)
			B36 = 200;
		if (Engine == 4)
			B36 = 250;
		if (Engine == 5)
			B36 = 205;
		if (Engine == 6)
			B36 = 275;

		B29 = 0.007;

		if (Engine == 1)
			AG4 = 0.381;
		if (Engine == 2)
			AG4 = 0.3570;
		if (Engine == 3)
			AG4 = 0.39;
		if (Engine == 4)
			AG4 = 0.39;
		if (Engine == 5)
			AG4 = 0.33;
		if (Engine == 6)
			AG4 = 0.30;

		AH4 = 0.83618 * VehicleHeight * 0.03937 * width * 0.03937
				* (0.0006451625806477) + 0.012;

		double aero = ((11990.0 * 104.6 * AG4 * AH4) / B31) / 1000.0;
		double braking = (11990.0 * 0.16 * ((AE4 / 2.204) + 136.0)) / 1000.0;
		double tire = (11990.0 * (7.272 * B29) * ((AE4 / 2.204) + 136.0)) / 1000.0;
		double chassis = (0.742 * B30 * 11990.0) / 1000.0;
		double accessory = (1372.0 * B32) / 1000.0;
		double driveloss = (aero + braking + tire) * (1.0 - B37);
		double PRCity = (1372.0 * 745.7 * B36)
				/ (1000.0 * (aero + braking + tire + chassis + accessory + driveloss));

		double aeroh = ((16500.0 * 283 * AG4 * AH4) / B31) / 1000.0;
		double brakingh = (16500.0 * 0.0421 * ((AE4 / 2.204) + 136.0)) / 1000.0;
		double tireh = (16500.0 * (9.036 * B29) * ((AE4 / 2.204) + 136.0)) / 1000.0;
		double chassish = (0.922 * B30 * 16500.0) / 1000.0;
		double accessoryh = (765.0 * B33) / 1000.0;
		double drivelossh = (aeroh + brakingh + tireh) * (1.0 - B37);
		double PRHwy = (765.0 * 645.7 * B36)
				/ (1000.0 * (aeroh + brakingh + tireh + chassish + accessoryh + drivelossh));

		double B34 = -0.0024 * PRCity + 0.241;

		double B35 = -0.0067 * PRHwy + 0.345;

		double AX4;
		AX4 = 16500.0
				* ((283.0 * AG4 * AH4 / B31 + (9.036 * B29 + 0.0421)
						* (AE4 / 2.204 + 136.0))
						/ B37 + 0.922 * B30) + 765.0 * B33;

		double BA4 = B35;

		double AY4;
		AY4 = 11990.0
				* ((104.6 * AG4 * AH4 / B31 + (7.272 * B29 + 0.16)
						* (AE4 / 2.204 + 136.0))
						/ B37 + 0.742 * B30) + 1372.0 * B32;

		double AZ4 = B34;

		double BB4 = (1271587789.0 * BA4 / AX4);
		double HwyFE = BB4 * 0.78;

		double BC4 = 924020459.0 * AZ4 / AY4;
		double CityFE = BC4 * 0.90;

		double CombFE;
		CombFE = 1.0 / ((0.55 / CityFE) + (0.45 / HwyFE));

		// Compute Shoulder Room Objective

		double B15 = 190.0;

		reartrack = 1.0904 * FrontTrack - 143.94;

		width = 78.5145 + 0.089 * FrontTrack + 1.0575 * reartrack;

		B12 = width;

		double ShoulderRoom;

		ShoulderRoom = B12 - 2.0 * B15;

		double CurbWeightScaled = CurbWeight / BaselineCurbWeight;
		double accelScaled = accel / BaselineTa;
		double CombFEScaled = CombFE / BaselineFc;
		double ShoulderRoomScaled = ShoulderRoom / BaselineW3;
		double FrHRScaled = FrHR / BaselineH61;
		double CargoVolumeScaled = CargoVolume / BaselineV1;
		 
		 solution.setObjective(0, accelScaled);
		 solution.setObjective(1, -CombFEScaled);
		 solution.setObjective(2, -ShoulderRoomScaled);
		 solution.setObjective(3, -FrHRScaled);
		 solution.setObjective(4, -CargoVolumeScaled);
		 solution.setConstraint(0, TotalViolation);
		 solution.setAttribute("mass", CurbWeightScaled);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(11, 5, 1);
		solution.setVariable(0, new RealVariable(0, 1));
		solution.setVariable(1, new RealVariable(0, 1));
		solution.setVariable(2, new RealVariable(0, 1));
		solution.setVariable(3, new RealVariable(0, 1));
		solution.setVariable(4, new RealVariable(0, 1));
		solution.setVariable(5, new RealVariable(0, 1));
		solution.setVariable(6, new RealVariable(0, 1));
		solution.setVariable(7, new RealVariable(1, 7 - Settings.EPS));
		solution.setVariable(8, new RealVariable(0, 1));
		solution.setVariable(9, new RealVariable(0, 1));
		solution.setVariable(10, new RealVariable(0, 1));
		return solution;
	}

}
