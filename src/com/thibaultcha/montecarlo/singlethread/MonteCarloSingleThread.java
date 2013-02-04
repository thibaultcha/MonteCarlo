package com.thibaultcha.montecarlo.singlethread;

import java.io.IOException;
import java.lang.Math;
import java.util.Random;

import com.thibaultcha.gui.MonteCarloGUI;
import com.thibaultcha.montecarlo.MonteCarlo;


public class MonteCarloSingleThread extends MonteCarlo
{
	/** 
	 * Basic Monte Carlo single-thread implementation
	 */
    public double runMonteCarlo(String callPutFlag, double S, double X, double T, double R, double B, double V, int nSteps, int nSimulations)
	{		
		Random rand = new Random();
		double dt, St, Sum=0,Drift, vSqrdt;
		dt = T / nSteps;
		Drift = (B - (V*V) / 2) * dt;
		vSqrdt = V*Math.sqrt(dt);
		int z=0;
		if (callPutFlag.equals("c")) {
			z=1;
		} else if (callPutFlag.equals("p")) {
			z=-1;
		}
		
		for (int i=0 ; i<nSimulations ; i++) {
			St = S;
			for (int j=0 ; j<nSteps ; j++) {
				St = St * Math.exp(Drift + vSqrdt * rand.nextGaussian());
			}
			Sum = Sum + Math.max(z * (St - X), 0);
		}

		return Math.exp(-R * T) * (Sum / nSimulations);
	}
	
	public static void main(final String[] args) {
		try {
			@SuppressWarnings("unused")
			MonteCarloGUI fenetre = new MonteCarloGUI(new MonteCarloSingleThread());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
