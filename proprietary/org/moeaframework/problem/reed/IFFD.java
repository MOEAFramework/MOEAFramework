/* Copyright 2009-2011 David Hadka
 * 
 * This file is part of the MOEA Framework.
 * 
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * The MOEA Framework is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.problem.reed;

import org.moeaframework.core.PRNG;
import org.moeaframework.util.sequence.Sequence;

/*
Code extracted from Yong Tang's Work on the ORMS problem.

Copyright 2007-2010 Patrick Reed and Yong Tang

See Tang, Yong, Reed, P. M., Wagener, T., and van Werkhoven, K., "Comparing 
sensitivity analysis methods to advance lumped watershed model identification 
and evaluation." Hydrology and Earth System Sciences, 11, 793-817, 2007.
*/
public class IFFD implements Sequence {
	
	private final int ncol;
	
	private final int nzero;
	
	public IFFD(int ncol, int nzero) {
		super();
		this.ncol = ncol;
		this.nzero = nzero;
	}
	
	//parameter Level matrix
	int[][] LL;

	@Override
	public double[][] generate(int nsample, int nparm) {
		int nrow = 2*ncol;
		int nrep = nsample/nrow;
		int[] Levels = new int[nrow];
		int i,j,k;
		double l=0.0,u=1.0,midwid;
		
		LL = new int[nsample][nparm]; 
		double[][] data = new double[nsample][nparm];
		
		//assign parameter groups
		int[][] group = groupMap(nrep,nparm,ncol,nzero);
		//create Hadamard matrix
		int[][] H1 = hadamard(ncol);
		//translate to FF levels
		FFLevels(H1, ncol);
		//fold samples
		int[][] H2 = foldIntSample(H1,ncol);
			
		//Generate Samples
		for(i=0;i<nrep;i++)
		{
			for(j=0;j<nparm;j++)
			{
				midwid=nzero/((double)nrep);
				if(group[i][j]==0)
				{
					l=0.5-midwid/2;
					u=0.5+midwid/2;
					for(k=0;k<nrow;k++)
					{
						data[i*nrow+k][j]=PRNG.nextDouble(l,u);
						LL[i*nrow+k][j]=0;
					}
				}
				else if(group[i][j]>0)
				{
					for(k=0;k<nrow;k++)
					{
						Levels[k]=H2[k][group[i][j]-1];
					}
					//generate sampels
					for(k=0;k<nrow;k++)
					{
						if(Levels[k]==0)
						{
							l=0.0;
							u=0.5-midwid/2;
							LL[i*nrow+k][j]=-1;
						}
						else if(Levels[k]==1)
						{
							l=0.5+midwid/2;
							u=1.0;
							LL[i*nrow+k][j]=1;
						}
						data[i*nrow+k][j]=PRNG.nextDouble(l,u);
					}
				}
				else
				{
					for(k=0;k<nrow;k++)
					{
						Levels[k]=1-H2[k][Math.abs(group[i][j])-1];
					}
					
					//generate sampels
					for(k=0;k<nrow;k++)
					{
						if(Levels[k]==0)
						{
							l=0.0;
							u=0.5-midwid/2;
							LL[i*nrow+k][j]=-1;
						}
						else if(Levels[k]==1)
						{
							l=0.5+midwid/2;
							u=1.0;
							LL[i*nrow+k][j]=1;
						}
						data[i*nrow+k][j]=PRNG.nextDouble(l,u);
					}
				}
			}
		}
		
		return data;
	}
	
	//fold samples with integer type
	int[][] foldIntSample(int[][] oldSample, int n)
	{
		int i,j;
		int[][] newSample = new int[2*ncol][ncol];
		
		for(i=0;i<n;i++)
			for(j=0;j<n;j++)
				newSample[i][j]=oldSample[i][j];
		for(i=n;i<2*n;i++)
			for(j=0;j<n;j++)
				newSample[i][j]=1-oldSample[i-n][j];
		
		return newSample;
	}
	
	//translate -1 and 1 in a Hadamard matrix to the levels 0 and 1, lower, upper level
	void FFLevels(int[][] H, int n)
	{
		int i,j;
		for(i=0;i<n;i++)
			for(j=0;j<n;j++)
				if(H[i][j]==-1) H[i][j]=0;	
	}
	
	//used to generate hadamard matrix n*n
	//The size n must be of the form 2^k*p for p=1, 12 or 20
	//The code is translated from matlab
	int[][] hadamard(int n) {
		int i,j,k;
		int e,p;
		int[][] H = new int[n][n];
		
		//base hadamard
		int h1=1;
		int[][] h12 ={{1,1,1,1,1,1,1,1,1,1,1,1},
		{1,-1, 1,-1, 1, 1, 1,-1,-1,-1, 1,-1},
		{1,-1,-1, 1,-1, 1, 1, 1,-1,-1,-1, 1},
		{1, 1,-1,-1, 1,-1, 1, 1, 1,-1,-1,-1},
		{1,-1, 1,-1,-1, 1,-1, 1, 1, 1,-1,-1},
		{1,-1,-1, 1,-1,-1, 1,-1, 1, 1, 1,-1},
		{1,-1,-1,-1, 1,-1,-1, 1,-1, 1, 1, 1},
		{1, 1,-1,-1,-1, 1,-1,-1, 1,-1, 1, 1},
		{1, 1, 1,-1,-1,-1, 1,-1,-1, 1,-1, 1},
		{1, 1, 1, 1,-1,-1,-1, 1,-1,-1, 1,-1},
		{1,-1, 1, 1, 1,-1,-1,-1, 1,-1,-1, 1},
		{1, 1,-1, 1, 1, 1,-1,-1,-1, 1,-1,-1}};
		int[][] h20={{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1},
		{1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1},
		{1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1},
		{1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1},
		{1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1},
		{1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1},
		{1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1},
		{1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1},
		{1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1},
		{1,-1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1},
		{1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1},
		{1,-1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1},
		{1, 1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1},
		{1, 1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1},
		{1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1},
		{1, 1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1},
		{1,-1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1},
		{1,-1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1},
		{1, 1,-1,-1, 1, 1,-1,-1,-1,-1, 1,-1, 1,-1, 1, 1, 1, 1,-1,-1}};
		
		//find k if n = 2^k*p
		e = 0;
		while (n>1 && (n/2)*2 == n)
		{
			e++;
			n=n/2;
		}
		
		if (n!=1) e-=2; // except for n=2^k, need a multiple of 4
		if (e<0) n=-1; // trigger error if not a multiple of 4
		
		//Kronecker product construction
		if (n==1)
		{
		    
			p=1;
			H[0][0]=h1;
			for(i=0;i<e;i++)
			{
				for(j=0;j<(int)Math.pow(2,i);j++)
					for(k=0;k<(int)Math.pow(2,i);k++)
					{
						
						H[j][k+p*(int)Math.pow(2,i)]=H[j][k];
						H[j+p*(int)Math.pow(2,i)][k]=H[j][k];
						H[j+p*(int)Math.pow(2,i)][k+p*(int)Math.pow(2,i)]=-H[j][k];
					}

			}
		}
		else if(n==3)
		{
			p=12;
			for(j=0;j<p;j++)
				for(k=0;k<p;k++)
					H[j][k]=h12[j][k];
			for(i=0;i<e;i++)
			{
				for(j=0;j<(int)Math.pow(2,i);j++)
					for(k=0;k<(int)Math.pow(2,i);k++)
					{
						
						H[j][k+p*(int)Math.pow(2,i)]=H[j][k];
						H[j+p*(int)Math.pow(2,i)][k]=H[j][k];
						H[j+p*(int)Math.pow(2,i)][k+p*(int)Math.pow(2,i)]=-H[j][k];
					}

			}
		}
		else if(n==5)
		{
			p=20;
			for(j=0;j<p;j++)
				for(k=0;k<p;k++)
					H[j][k]=h20[j][k];
			for(i=0;i<e;i++)
			{
				for(j=0;j<(int)Math.pow(2,i);j++)
					for(k=0;k<(int)Math.pow(2,i);k++)
					{
						
						H[j][k+p*(int)Math.pow(2,i)]=H[j][k];
						H[j+p*(int)Math.pow(2,i)][k]=H[j][k];
						H[j+p*(int)Math.pow(2,i)][k+p*(int)Math.pow(2,i)]=-H[j][k];
					}

			}
		}
		else
		{
			throw new IllegalArgumentException("n must be 2^e*p, for p = 1, 12, 20 ");
		}

		return H;
	}
	
	//Permuation, random select n numbers from 1,...m, m>=n, without replacement
	int[] permutate(int m, int n) {
		int[] index1 = new int[m];
		int[] index2 = new int[m];
		int[] P = new int[n];
		
	    for (int i=0; i<m; i++) {
			index1[i] = 1;
			index2[i] = 0;
	    }
	    
		for(int i=0;i<n;i++) {
			int nPos = 0;
			for (int j=0; j<m; j++) { 
				if (index1[j] > 0)   
				{
					index2[nPos] = (j+1);
					nPos = nPos+1;
				}
			}
			
			int k = -1;
			while (k < 1) {
				k = (int)Math.round(0.5+nPos*PRNG.nextDouble());
			}
			
			int element = index2[k-1];
			index1[element-1] = 0;
			P[i] = element;
		}
		
		return P;
	}
	
	//group parameters, return a nrep*nparm matrix
	private int[][] groupMap(int nrep, int nparm, int ncol, int nzero){
		int i, j, k, kk;
		int[] P = null;
		int[][] group = new int[nrep][nparm];

		for(i=0;i<nparm;i++)
		{
			//select the zero replicates
			P = permutate(nrep,nzero);
			for(j=0;j<nzero;j++)
			{
				group[P[j]-1][i]=0;
			}
			//assign group and orientation randomly to the remaining replicates
			for(j=0;j<nrep;j++)
			{
				kk=0;
				for(k=0;k<nzero;k++)
					if(P[k]==j+1) kk=-1;
				if(kk==0)
				{
					kk=-1;
					while (kk < 1)  kk = (int)Math.round(0.5+ncol*PRNG.nextDouble());
					if(PRNG.nextDouble()>0.5)
						group[j][i]=kk;
					else
						group[j][i]=-kk;
				}
			}
		}
		
	    return group;
	}
	
	//Caculate F values for main effects and 2-way interactions based on anova
	//objs is the array storing the output responses (objectives) for nsample
	//LL is the factor Level matrix, -1, 0, and +1
	//F is the array storing F values
	void ANOVA(float[] objs, int[][] LL, int nsample, int nparam, float[] F, float[] R2)
	{
		int i,j,k,l;
		float[] Yi = new float[3]; //Level mean
		float[] Yj = new float[3];
		float GY;//grand mean
		int[] ni = new int[3]; //number of points at each level
		int[] nj = new int[3]; //number of points at each level
		float[][] Yij = new float[3][3];
		int[][] nij = new int[3][3];
		float SSTR;//treatment sum of squares
		float SSTO;
		float SSE;//error sum of squares
		float MSTR;//treatmeant mean squre 
		float MSE;//error eman squre
		float SSAB;//cross-factor sum of squares
		float SSTRAB;
		float SSEAB;
		float MSAB;
		float MSEAB;
		float s;
		int index;
		
		s=0;
		for(i=0;i<nsample;i++)
		{
			s=s+objs[i];
		}
		GY=s/nsample;
		
		SSTO=0;
		for(i=0;i<nsample;i++)
		{
			SSTO=SSTO+(objs[i]-GY)*(objs[i]-GY);
		}
		R2[0]=0;
		R2[1]=0;
		//main effects
		for(i=0;i<nparam;i++)
		{
			for(j=0;j<3;j++) 
			{
				Yi[j]=0;
				ni[j]=0;
			}
			for(j=0;j<nsample;j++)
			{
				Yi[LL[j][i]+1]=Yi[LL[j][i]+1]+objs[j];
				ni[LL[j][i]+1]=ni[LL[j][i]+1]+1;
			}
			SSTR=0;
			for(j=0;j<3;j++) 
			{
				if(ni[j]>0) Yi[j]=Yi[j]/ni[j]; 
				SSTR=SSTR+ni[j]*(Yi[j]-GY)*(Yi[j]-GY);
			}
			SSE=0;
			for(j=0;j<nsample;j++)
			{
				SSE=SSE+(objs[j]-Yi[LL[j][i]+1])*(objs[j]-Yi[LL[j][i]+1]);
			}
			MSTR=SSTR/(3-1);
			MSE=SSE/(nsample-3);
			if(MSE>0) F[i]=MSTR/MSE;
			R2[0]=R2[0]+SSTR/SSTO;
			R2[1]=R2[1]+SSTR/SSTO;
		}
		//Interactions
		index=nparam;
		for(i=0;i<nparam-1;i++)
		{
			for(j=0;j<3;j++) 
			{
				Yi[j]=0;
				ni[j]=0;
			}
			for(j=0;j<nsample;j++)
			{
				Yi[LL[j][i]+1]=Yi[LL[j][i]+1]+objs[j];
				ni[LL[j][i]+1]=ni[LL[j][i]+1]+1;
			}
			for(j=0;j<3;j++) 
			{
				if(ni[j]>0) Yi[j]=Yi[j]/ni[j]; 
			}

			for(j=i+1;j<nparam;j++)
			{
				for(k=0;k<3;k++) 
				{
					Yj[k]=0;
					nj[k]=0;
				}
				for(k=0;k<nsample;k++)
				{
					Yj[LL[k][j]+1]=Yj[LL[k][j]+1]+objs[k];
					nj[LL[k][j]+1]=nj[LL[k][j]+1]+1;
				}
				for(k=0;k<3;k++) 
				{
					if(nj[k]>0) Yj[k]=Yj[k]/nj[k]; 
				}

				for(k=0;k<3;k++)
				{
					for(l=0;l<3;l++)
					{
						Yij[k][l]=0;
						nij[k][l]=0;
					}
				}
				for(k=0;k<nsample;k++)
				{
					Yij[LL[k][i]+1][LL[k][j]+1]=Yij[LL[k][i]+1][LL[k][j]+1]+objs[k];
					nij[LL[k][i]+1][LL[k][j]+1]=nij[LL[k][i]+1][LL[k][j]+1]+1;
				}
				SSTRAB=0;
				for(k=0;k<3;k++)
				{
					for(l=0;l<3;l++)
					{
						if(nij[k][l]>0) Yij[k][l]=Yij[k][l]/nij[k][l];
						SSTRAB=SSTRAB+nij[k][l]*(Yij[k][l]-GY)*(Yij[k][l]-GY);
					}
				}
				SSAB=0;
				for(k=0;k<3;k++)
				{
					for(l=0;l<3;l++)
					{
						SSAB=SSAB+nij[k][l]*(Yij[k][l]-Yi[k]-Yj[l]+GY)*(Yij[k][l]-Yi[k]-Yj[l]+GY);
					}
				}
				MSAB=SSAB/((3-1)*(3-1));
				SSEAB=0;
				for(k=0;k<nsample;k++)
				{
					SSEAB=SSEAB+(objs[k]-Yij[LL[k][i]+1][LL[k][j]+1])*(objs[k]-Yij[LL[k][i]+1][LL[k][j]+1]);
				}
				MSEAB=SSEAB/(nsample-3*3);
				if(MSEAB>0) F[index]=MSAB/MSEAB;
				R2[1]=R2[1]+SSAB/SSTO;
				index=index+1;
			}
		}
		
	}

	void Bt_ANOVA(int nsample, int nparam, int np, int nrspl, int[][] LL, float[] stobj, float[] FCI)
	{
		int k;
		float[] obj = new float[nsample];
		int rspl;
		int[][] LL1 = new int[nsample][nparam];
		float[][] s = new float[nrspl][np];
		float[] ss = new float[np];
		float[] sss = new float[np];
		float[] R2 = new float[2];
		int ii,kk;

				for(ii=0;ii<np;ii++)
				{
					ss[ii]=0;
					sss[ii]=0;
				}
				for(rspl=0;rspl<nrspl;rspl++)
				{
					for(k=0;k<nsample;k++)
					{
						kk=(int)Math.round((nsample-1)*PRNG.nextDouble());
						obj[k]=stobj[kk];
						for(ii=0;ii<nparam;ii++) LL1[k][ii]=LL[kk][ii];
					}
					ANOVA(obj,LL1,nsample,nparam,s[rspl],R2);
					for(ii=0;ii<np;ii++) ss[ii]=ss[ii]+s[rspl][ii];
				}
				for(ii=0;ii<np;ii++) ss[ii]=ss[ii]/nrspl;
				for(rspl=0;rspl<nrspl;rspl++)
				{
					for(ii=0;ii<np;ii++)
						sss[ii]=sss[ii]+(s[rspl][ii]-ss[ii])*(s[rspl][ii]-ss[ii]);
				}
				for(ii=0;ii<np;ii++)
				{
					FCI[ii]=(float)(1.96*Math.sqrt(sss[ii]/(nrspl-1)));
				}
	}
	
	public static void main(String[] args) {
		int nrep = 100; //number of replications
		int ncol = 20; //Hadamard matrix size (2, 12 or 20)
		int nzero = 15; //number of replicates that take middle level, useful for IFFD
		int nsample=nrep*(2*ncol); //# samples
		int nparam=2; //# parameters
		int np=nparam+nparam*(nparam-1)/2; //# of parameters and parameter interactions
		int nrspl=1000; //bootstrap resamples
		float[] obj=new float[nsample]; //objective (model response) vector
		float[] F = new float[np]; //the F values assigned by ANOVA
		float[] R2 = new float[2]; //the R2 values assigned by ANOVA
		float[] FCI = new float[np]; //the confidence interval values assigned by ANOVA bootstrapping
		int i, j, k;

		//generate IFFD samples
		IFFD iffd = new IFFD(ncol, nzero);
		double[][] par = iffd.generate(nsample, nparam); //parameter matrix
		int[][] LL = iffd.LL; //parameter Level matrix

		//***insert code here to run IFFD parameters through model and assign reponses to obj***
		for (i=0; i<nsample; i++) {
			obj[i] = (float)(par[i][0]+0*par[i][1]);
		}

		//calculate F values according ANOVA
		iffd.ANOVA(obj,LL,nsample,nparam,F,R2);

		//bootstrap ANOVA
		iffd.Bt_ANOVA(nsample,nparam,np,nrspl,LL,obj,FCI);

		//output results
		System.out.printf("Main Effects\n");
		for (i=0; i<nparam; i++) {
			System.out.printf("  Parameter %d:\t%10.4f [ %6.4f ]\n", i, F[i], FCI[i]);
		}
		System.out.printf("Interaction Effects\n");
		for(j=0;j<nparam-1;j++) {
			for(k=j+1;k<nparam;k++) {
				System.out.printf("  Parameters %d & %d:\t%10.4f [ %6.4f ]\n", j, k, F[i], FCI[i]);
				i++;
			}
		}
		System.out.printf("R2-1: %6.4f\n", R2[0]);
		System.out.printf("R2-2: %6.4f\n", R2[1]);
	}

}
