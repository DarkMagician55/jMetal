//  SMPSORunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.smpso.SMPSO;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.problem.multiobjective.Fonseca;

/**
 * This class executes:
 * - The SMPSO algorithm described in:
 *   Antonio J. Nebro, Juan José Durillo, Carlos Artemio Coello Coello:
 *   Analysis of leader selection strategies in a multi-objective Particle Swarm Optimizer.
 *   IEEE Congress on Evolutionary Computation 2013: 3153-3160
 * - The SMPSOhv algorithm described in:
 *   Antonio J. Nebro, Juan José Durillo, Carlos Artemio Coello Coello:
 *   Analysis of leader selection strategies in a multi-objective Particle Swarm Optimizer.
 *   IEEE Congress on Evolutionary Computation 2013: 3153-3160
 */
public class SMPSORunner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException       
   * Usage: three options
   *          - SMPSORunner
   *          - SMPSORunner problemName
   *          - SMPSORunner problemName ParetoFrontFile
   */
  public static void main(String[] args) throws Exception {
    Problem problem;
    Algorithm algorithm;
    Mutation mutation;

    QualityIndicatorGetter indicators;

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
    } else {
      problem = new Fonseca("Real");
      /*
        Examples:
        problem = new Water("Real");
        problem = new ZDT3("ArrayReal", 30);
        problem = new ConstrEx("Real");
        problem = new DTLZ1("Real");
        problem = new OKA2("Real")
      */
    }

    /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator() // SMPSO
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem) // parallel SMPSO
     */
    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator();

    /*
     * Alternatives:
     * - setArchive = new CrowdingArchive(100, problem.getNumberOfObjectives()) ; // SMPSO
     * - setArchive = new FastHypervolumeArchive(100, problem.getNumberOfObjectives()); // SMPSOhv
     */
    Archive archive = new CrowdingArchive(10, problem.getNumberOfObjectives()) ;

    mutation = new PolynomialMutation.Builder()
      .setDistributionIndex(20.0)
      .setProbability(1.0 / problem.getNumberOfVariables())
      .build();

    algorithm = new SMPSO.Builder(problem, archive, evaluator)
      .setMutation(mutation)
      .setMaxIterations(2)
      .setSwarmSize(10)
      .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute();

    SolutionSet population = algorithmRunner.getSolutionSet();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionSetOutput.Printer(population)
      .setSeparator("\t")
      .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      JMetalLogger.logger.info("Quality indicators");
      JMetalLogger.logger.info("Hypervolume: " + indicators.getHypervolume(population));
      JMetalLogger.logger.info("GD         : " + indicators.getGD(population));
      JMetalLogger.logger.info("IGD        : " + indicators.getIGD(population));
      JMetalLogger.logger.info("Spread     : " + indicators.getSpread(population));
      JMetalLogger.logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
