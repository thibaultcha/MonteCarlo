package com.thibaultcha.montecarlo;

import java.util.concurrent.ExecutionException;

public abstract class MonteCarlo
{
	public static float coefficientBloquant = (float) 0.1;
	 
	/**
	 * Abstract method to be implemented in a single-thread or multi-thread algorithm
	 * @param callPutFlag = 'c' || 'p'
	 * @param S = stock price today
	 * @param X = strike price
	 * @param T = time to maturity
	 * @param R = discount price
	 * @param B = cost of carry rate
	 * @param V = volatility
	 * @param nSteps
	 * @param nSimulations
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public abstract double runMonteCarlo(String callPutFlag, final double S, double X, double T, double R, double B, double V, final int nSteps, int nSimulations) throws InterruptedException, ExecutionException;
}