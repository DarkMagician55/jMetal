package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.algorithm.singleobjective.rotatestrategy.RotateStrategy;
import org.uma.jmetal.problem.singleobjective.WindFLO;
import org.uma.jmetal.problem.singleobjective.WindScenario;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guotong on 4/14/16.
 */
public class WindFLORotateRunner {
    public static void main(String[] args) throws Exception {

        Algorithm<DoubleSolution> algorithm;
        WindFLO problem = new WindFLO() {
        } ;
        WindScenario ws = new WindScenario("/home/guotong/code/WindFLO/Scenarios/"+args[1]+".xml");
        problem.initialize(ws);

        algorithm = new RotateStrategy.Builder(problem).setPopulationSize(10).setMaxEvaluations(10000000)
                .build() ;


        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        DoubleSolution solution = algorithm.getResult() ;
        List<DoubleSolution> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("./result/ROTATE_VAR_"+args[1]+"_"+args[2]+".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("./result/ROTATE_FUN_"+args[1]+"_"+args[2]+".tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    }
}
