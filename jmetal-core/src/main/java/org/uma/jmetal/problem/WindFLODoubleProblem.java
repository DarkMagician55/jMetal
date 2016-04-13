package org.uma.jmetal.problem;

/**
 * Created by guotong on 4/13/16.
 */
public interface WindFLODoubleProblem extends DoubleProblem {

    public double getWakeFreeRatio() ;

    public double getEnergyCost();

    public double getTurbineRadius();

    public double getFarmWidth();

    public double getFarmHeight();

    public double[][] getObstacles();
}
