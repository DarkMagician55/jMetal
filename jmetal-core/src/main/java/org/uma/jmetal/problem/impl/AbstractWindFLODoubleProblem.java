package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.WindFLODoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.WindFLODoubleSolution;

/**
 * Created by guotong on 4/13/16.
 */
public abstract class AbstractWindFLODoubleProblem extends AbstractDoubleProblem implements WindFLODoubleProblem {
    public double getWakeFreeRatio() {
        return 0.0;
    }

    public double getEnergyCost(){
        return 0.0;
    }

    public double getTurbineRadius(){
        return 0.0;
    }

    public double getFarmWidth(){
        return 0.0;
    }

    public double getFarmHeight(){
        return 0.0;
    }

    public double[][] getObstacles(){
        return new double[1][1];
    }
    public DoubleSolution createSolution() {
        return new WindFLODoubleSolution(this)  ;
    }
}
