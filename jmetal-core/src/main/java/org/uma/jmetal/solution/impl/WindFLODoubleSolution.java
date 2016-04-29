package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.WindFLODoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;

import java.util.HashMap;

/**
 * Created by guotong on 4/11/16.
 */

@SuppressWarnings("serial")
public class WindFLODoubleSolution
        extends AbstractGenericSolution<Double, WindFLODoubleProblem>
        implements DoubleSolution {

    /** Constructor */
    public WindFLODoubleSolution(WindFLODoubleProblem problem) {
        super(problem) ;

        overallConstraintViolationDegree = 0.0 ;
        numberOfViolatedConstraints = 0 ;

        initializeDoubleVariables();
        initializeObjectiveValues();
    }

    /** Copy constructor */
    public WindFLODoubleSolution(WindFLODoubleSolution solution) {
        super(solution.problem) ;

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            setVariableValue(i, solution.getVariableValue(i));
        }

        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            setObjective(i, solution.getObjective(i)) ;
        }

        overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
        numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;
        attributes = new HashMap<Object, Object>(solution.attributes) ;
    }

    @Override
    public Double getUpperBound(int index) {
        return problem.getUpperBound(index);
    }

    @Override
    public Double getLowerBound(int index) {
        return problem.getLowerBound(index) ;
    }

    @Override
    public WindFLODoubleSolution copy() {
        return new WindFLODoubleSolution(this);
    }

    @Override
    public String getVariableValueString(int index) {
        return getVariableValue(index).toString() ;
    }

    //  private void initializeDoubleVariables() {
//    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
//      Double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i)) ;
//      setVariableValue(i, value) ;
//    }
//  }
    private void initializeDoubleVariables() {

        ArrayList<double[]> grid = new ArrayList<double[]>();
        double interval = 10 * problem.getTurbineRadius();

        //建立网格，八倍最小半径，这grid到底有什么用，可以获取最多的发电机个数
        for (double x=0.0; x<problem.getFarmWidth(); x+=interval) {
            for (double y=0.0; y<problem.getFarmHeight(); y+=interval) {
                boolean valid = true;
                //获取障碍信息，如果处在障碍中将其置为无效d
                for (int o=0; o<problem.getObstacles().length; o++) {
                    double[] obs = problem.getObstacles()[o];
                    if (x>obs[0] && y>obs[1] && x<obs[2] && y<obs[3]) {
                        valid = false;
                    }
                }
                //如果有效，把这个点加入grid
                if (valid) {
                    double[] point = {x, y};
                    grid.add(point);
                }
            }
        }
        //System.out.println(grid.size());
        int a=randomGenerator.nextInt(0,5);
        //System.out.println(a);
        //System.out.println((problem.getNumberOfVariables())/2);
        for (int i = 0 ; i < ((problem.getNumberOfVariables())/2); i++) {
//      System.out.println(grid.get(i)[0]);
//      System.out.println(grid.get(i)[1]);
            setVariableValue(i*2, grid.get(i)[0] + randomGenerator.nextDouble(0, 1)) ;
            //setVariableValue(i*2, grid.get(i+a)[0]) ;
            setVariableValue(i*2+1, grid.get(i)[1] + randomGenerator.nextDouble(0, 1)) ;
            //setVariableValue(i*2+1, grid.get(i+a)[1]) ;
        }
    }
}
