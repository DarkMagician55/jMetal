package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Created by guotong on 04/05/16.
 */
@SuppressWarnings("serial")
public abstract class AbstractRotateStrategy<S extends Solution<?>, Result> extends AbstractRotateAlgorithm<S, Result> {

  /**
   * Constructor
   * @param problem The problem to solve
   */
  public AbstractRotateStrategy(Problem<S> problem) {
    setProblem(problem);
  }

  //region CMAES

  //endregion CMAES


  // region DE

  //endregion DE

  // region PSO

  //endregion PSO

}
