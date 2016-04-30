package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2011;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.WindFLO;
import org.uma.jmetal.problem.singleobjective.WindScenario;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guotong on 4/14/16.
 */
public class WindFLOPSORunner {

    private static final int DEFAULT_NUMBER_OF_CORES = 1 ;

    public static void main(String[] args) throws Exception {

        WindFLO problem;
        Algorithm<DoubleSolution> algorithm;
        SolutionListEvaluator<DoubleSolution> evaluator ;

        String problemName = "org.uma.jmetal.problem.singleobjective.WindFLO" ;

        problem = (WindFLO) ProblemUtils.<DoubleSolution> loadProblem(problemName);
        WindScenario ws = new WindScenario("/home/guotong/code/WindFLO/Scenarios/"+args[1]+".xml");
        problem.initialize(ws);

        int numberOfCores ;
        if (args.length == 1) {
            numberOfCores = Integer.valueOf(args[0]) ;
        } else {
            numberOfCores = DEFAULT_NUMBER_OF_CORES ;
        }

        if (numberOfCores == 1) {
            evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
        } else {
            evaluator = new MultithreadedSolutionListEvaluator<DoubleSolution>(numberOfCores, problem) ;
        }

        int swarmSize = 10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables()));
        //System.out.println(swarmSize);
        //for test
        //swarmSize = 15;
        int maxIter = Integer.parseInt(args[3]);
        algorithm = new StandardPSO2011(problem, swarmSize,maxIter, 3, evaluator) ;

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        DoubleSolution solution = algorithm.getResult() ;
        long computingTime = algorithmRunner.getComputingTime() ;

        List<DoubleSolution> population = new ArrayList<>(1) ;
        population.add(solution) ;
        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("./result/PSO_VAR_"+args[1]+"_"+args[2]+".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("./result/PSO_FUN_"+args[1]+"_"+args[2]+".tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        evaluator.shutdown();
    }
}
