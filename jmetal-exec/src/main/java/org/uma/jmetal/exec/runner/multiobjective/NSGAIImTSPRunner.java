//  NSGAIImMOTSPRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.exec.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIITemplate;
import org.uma.jmetal.operator.crossover.PMXCrossover;
import org.uma.jmetal.operator.mutation.SwapMutation;
import org.uma.jmetal.operator.selection.BinaryTournament2;
import org.uma.jmetal.problem.multiobjective.MultiObjectiveTSP;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

import java.io.IOException;

/**
 * Class to configure and run the NSGA-II algorithm. The settings are aimed
 * at solving the multiobjective TSP problem.
 */

public class NSGAIImTSPRunner {
  /**
   * @param args Command line arguments.
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws Exception {
    Problem problem; 
    Algorithm algorithm; 
    Operator crossover; 
    Operator mutation; 
    Operator selection; 

    QualityIndicatorGetter indicators;

    problem = new MultiObjectiveTSP("Permutation", "kroA100.tsp", "kroB100.tsp");

     /*
     * Alternatives:
     * - "NSGAII"
     * - "SteadyStateNSGAII"
     */

    String nsgaIIVersion = "NSGAII" ;
      /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator() // NSGAII
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem) // parallel NSGAII
     */

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    crossover = new PMXCrossover.Builder()
            .setProbability(0.95)
            .build() ;

    mutation = new SwapMutation.Builder()
            .setProbability(0.2)
            .build() ;

    selection = new BinaryTournament2.Builder()
            .build() ;

    algorithm = new NSGAIITemplate.Builder(problem, evaluator)
            .setPopulationSize(100)
            .setMaxEvaluations(1000000)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setSelection(selection)
            .build("NSGAII") ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  } 
} 
