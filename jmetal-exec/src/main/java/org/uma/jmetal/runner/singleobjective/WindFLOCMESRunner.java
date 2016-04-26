package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.problem.WindFLODoubleProblem;
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
public class WindFLOCMESRunner {
    public static void main(String[] args) throws Exception {

        Algorithm<DoubleSolution> algorithm;
        WindFLO problem = new WindFLO() {
        } ;
        WindScenario ws = new WindScenario("/home/guotong/code/WindFLO/Scenarios/"+args[1]+".xml");
        problem.initialize(ws);

        algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem)
                .build() ;


        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        DoubleSolution solution = algorithm.getResult() ;
        List<DoubleSolution> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("./result/CMES_VAR_"+args[1]+"_"+args[2]+".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("./result/CMES_FUN_"+args[1]+"_"+args[2]+".tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    }
}
