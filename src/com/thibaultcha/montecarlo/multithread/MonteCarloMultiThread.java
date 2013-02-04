package com.thibaultcha.montecarlo.multithread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.thibaultcha.gui.MonteCarloGUI;
import com.thibaultcha.montecarlo.MonteCarlo;


public class MonteCarloMultiThread extends MonteCarlo
{	
	/**
	 * Multi-thread implementation of the Monte Carlo algorithm
	 * Use a List of Callables to perform nSimulations. Each Callable performs a simulation.
	 * When all simulations finished running, we compute all the values into Sum and have the same return statement as the single-thread version.
	 */
	public double runMonteCarlo(final String callPutFlag, final double S, final double X, double T, double R, double B, double V, final int nSteps, final int nSimulations) throws InterruptedException, ExecutionException
	{
		double dt, Sum=0;
		final double Drift;
		final double vSqrdt;
		dt = T / nSteps;
		Drift = (B - (V*V) / 2) * dt;
		vSqrdt = V*Math.sqrt(dt);
		int z=0;
		if (callPutFlag.equals("c")) {
			z=1;
		} else if (callPutFlag.equals("p")) {
			z=-1;
		}
		
	    final int poolSize = (int)(Runtime.getRuntime().availableProcessors() / (1 - coefficientBloquant));
		final List<Callable<Double>> partitions = new ArrayList<Callable<Double>>();
		
		for (int i=0 ; i<nSimulations ; i++) {
			partitions.add(new Callable<Double>() {
		      public Double call() throws Exception {
		    	  double St = S;
		    	  Random rand = new Random();
		    	  for (int j=0 ; j<nSteps ; j++) {
		    		  St *= Math.exp(Drift + vSqrdt * rand.nextGaussian());
		    	  }
		    	  return St;
		      }
		    });
		}
		
		final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);    
	    final List<Future<Double>> valueOfCalculations = executorPool.invokeAll(partitions);
	    executorPool.shutdown();
	    
	    for(final Future<Double> valueOfACalcul : valueOfCalculations) 
	    	Sum += Math.max(z * (valueOfACalcul.get() - X), 0);
	    
	    return Math.exp(-R * T) * (Sum / nSimulations);
	}
	
	public static void main(final String[] args) {
		try {
			@SuppressWarnings("unused")
			MonteCarloGUI fenetre = new MonteCarloGUI(new MonteCarloMultiThread());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
