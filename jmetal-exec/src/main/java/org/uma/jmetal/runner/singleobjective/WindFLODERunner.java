package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
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
 * Created by guotong on 4/5/16.
 *
 */
public class WindFLODERunner {
    private static final int DEFAULT_NUMBER_OF_CORES = 1 ;

    /**
     *  Usage: java org.uma.jmetal.runner.singleobjective.DifferentialEvolutionRunner [cores][WindScenario_name][output_num]
     */
    public static void main(String[] args) throws Exception {

        //example
//        args=new String[3];
//        args[0]="1";
//        args[1]="00" || obs_00
//        args[2]="00" 0[0-9]
//        args[3]="00" maxeval

        WindFLO problem;
        Algorithm<DoubleSolution> algorithm;
        DifferentialEvolutionSelection selection;
        DifferentialEvolutionCrossover crossover;
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

        crossover = new DifferentialEvolutionCrossover(0.5, 0.5, "rand/1/bin") ;
        selection = new DifferentialEvolutionSelection();
        int maxeval = Integer.parseInt(args[3]);
        int popusize = 10;

        algorithm = new DifferentialEvolutionBuilder(problem)
                .setCrossover(crossover)
                .setSelection(selection)
                .setSolutionListEvaluator(evaluator)
                .setMaxEvaluations(maxeval)
                .setPopulationSize(popusize)
                .build() ;

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        DoubleSolution solution = algorithm.getResult() ;
        long computingTime = algorithmRunner.getComputingTime() ;

        List<DoubleSolution> population = new ArrayList<>(1) ;
        population.add(solution) ;
        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("./result/DE_VAR_"+args[1]+"_"+args[2]+"_MaxEval:"+maxeval+"_polusize:"+popusize+".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("./result/DE_FUN_"+args[1]+"_"+args[2]+"_MaxEval:"+maxeval+"_polusize:"+popusize+".tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
//        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
//        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        evaluator.shutdown();
    }
}
