package org.moeaframework.problem.ZCAT;

public interface BasisFunction {
	
	public double apply(double[] J);
	
	public static final BasisFunction Z1 = (J) ->	{
	    double Z = 0.0;

	    for (int i = 0; i < J.length; i++) {
	        Z += J[i] * J[i];
	    }
	    
	    return (10.0 / J.length) * Z;
	};
	
	public static final BasisFunction Z2 = (J) -> {
	    double Z = -Double.MAX_VALUE;
	    
	    for (int i = 0; i < J.length; i++) {
	        Z = Math.max(Z, Math.abs(J[i]));
	    }
	    
	    return 10.0 * Z;
	};
	
	public static final BasisFunction Z3 = (J) -> {
	    double k = 5.0;
	    double Z = 0.0;
	    
	    for (int i = 0; i < J.length; i++) {
	        Z += (Math.pow(J[i], 2.0) - Math.cos((2.0 * k - 1) * Math.PI * J[i]) + 1.0) / 3.0;
	    }
	    
	    return (10.0 / J.length) * Z;
	};
	
	public static final BasisFunction Z4 = (J) -> {
	    double k = 5.0;
	    double pow1 = -Double.MAX_VALUE;
	    double pow2 = 0.0;
	    
	    for (int i = 0; i < J.length; i++) {
	        pow1 = Math.max(pow1, Math.abs(J[i]));
	        pow2 += 0.5 * (Math.cos((2.0 * k - 1) * Math.PI * J[i]) + 1.0);
	    }
	    
	    return (10.0 / (2.0 * Math.exp(1.0) - 2.0)) * (Math.exp(Math.pow(pow1, 0.5)) - Math.exp(pow2 / J.length) - 1.0 + Math.exp(1.0));
	};
	
	public static final BasisFunction Z5 = (J) -> {
	    double Z = 0.0;
	    
	    for (int i = 0; i < J.length; i++) {
	        Z += Math.pow(Math.abs(J[i]), 0.002);
	    }
	    
	    return -0.7 * Z3.apply(J) + (10.0 / J.length) * Z;
	};
	
	public static final BasisFunction Z6 = (J) -> {
	    double Z = 0.0;
	    
	    for (int i = 0; i < J.length; i++) {
	        Z += Math.abs(J[i]);
	    }
	    
	    return -0.7 * Z4.apply(J) + 10.0 * Math.pow(Z / J.length, 0.002);
	};

}
