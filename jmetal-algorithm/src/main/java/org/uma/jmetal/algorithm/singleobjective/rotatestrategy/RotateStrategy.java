//  CovarianceMatrixAdaptationEvolutionStrategy.java
//
//  Author:
//       Esteban López-Camacho <esteban@lcc.uma.es>
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

package org.uma.jmetal.algorithm.singleobjective.rotatestrategy;

import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.algorithm.impl.AbstractRotateStrategy;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.util.CMAESUtils;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.WindFLO;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.*;

/**
 * Class implementing the CMA-ES algorithm
 */
@SuppressWarnings("serial")
public class RotateStrategy
        extends AbstractRotateStrategy<DoubleSolution, DoubleSolution> {
    //DEFAULT
    private Comparator<DoubleSolution> comparator;
    private int lambda;
    private int populationSize;
    private int evaluations;
    private int maxEvaluations;
    private SolutionListEvaluator<DoubleSolution> evaluator;

    //DE
    protected DifferentialEvolutionCrossover crossoverOperator ;
    protected DifferentialEvolutionSelection selectionOperator ;

    //CMAES
    private double[] typicalX;

    /**
     * CMA-ES state variables
     */

    // Distribution mean and current favorite solution to the optimization problem
    private double[] distributionMean;

    // coordinate wise standard deviation (step size)
    private double sigma;

    // Symmetric and positive definitive covariance matrix
    private double[][] c;

    // Evolution paths for c and sigma
    private double[] pathsC;
    private double[] pathsSigma;

  /*
   * Strategy parameter setting: Selection
   */

    // number of parents/points for recombination
    private int mu;


    private double[] weights;
    private double muEff;

  /*
   * Strategy parameter setting: Adaptation
   */

    // time constant for cumulation for c
    private double cumulationC;

    // t-const for cumulation for sigma control
    private double cumulationSigma;

    // learning rate for rank-one update of c
    private double c1;

    // learning rate for rank-mu update
    private double cmu;

    // damping for sigma
    private double dampingSigma;

  /*
   * Dynamic (internal) strategy parameters and constants
   */

    // coordinate system
    private double[][] b;

    // diagonal D defines the scaling
    private double[] diagD;

    // c^1/2
    private double[][] invSqrtC;

    // track update of b and c
    private int eigenEval;

    private double chiN;

    private DoubleSolution bestSolutionEver = null;

    private Random rand;

    /**
     * Constructor
     */
    private RotateStrategy(Builder builder) {
        //COMMON
        super(builder.problem);
        this.maxEvaluations = builder.maxEvaluations;

        //CMAES
        this.populationSize = this.lambda = builder.populationSize;
        this.typicalX = builder.typicalX;
        this.sigma = builder.sigma;

        //DE
        this.crossoverOperator = builder.crossoverOperator;
        this.selectionOperator = builder.selectionOperator;
        this.evaluator = builder.evaluator;

        long seed = System.currentTimeMillis();
        rand = new Random(seed);
        comparator = new ObjectiveComparator<DoubleSolution>(0);

        //initializeInternalParameters();change to initprogram

    }

    /* Getters */
    public int getLambda() {
        return lambda;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /**
     * Buider class
     */
    public static class Builder {
        //DEFAULT
        //private static final int DEFAULT_POPULATIONSIZE = 10 ;
        private static final int DEFAULT_LAMBDA = 10;
        private static final int DEFAULT_MAX_EVALUATIONS = 1000000;
        private static final double DEFAULT_SIGMA = 0.3;
        //COMMON
        private DoubleProblem problem;
        private int populationSize;//lambda
        private int lambda;
        private int maxEvaluations;

        //CMAES


        private double[] typicalX;
        private double sigma;

        //DE
        private DifferentialEvolutionCrossover crossoverOperator;
        private DifferentialEvolutionSelection selectionOperator;
        private SolutionListEvaluator<DoubleSolution> evaluator;

        public Builder(DoubleProblem problem) {
            //COMMON
            this.problem = problem;
            this.maxEvaluations = DEFAULT_MAX_EVALUATIONS;
            this.populationSize = lambda = DEFAULT_LAMBDA;

            //CMAES
            this.sigma = DEFAULT_SIGMA;

            //DE
            this.crossoverOperator = new DifferentialEvolutionCrossover(0.5, 0.5, "rand/1/bin");
            this.selectionOperator = new DifferentialEvolutionSelection();
            this.evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();

        }

        public Builder setPopulationSize(int populationSize) {
            if (populationSize < 0) {
                throw new JMetalException("Population size is negative: " + populationSize);
            }

            this.populationSize = this.lambda = populationSize;

            return this;
        }

        public Builder setLambda(int lambda) {
            this.populationSize = this.lambda = lambda;
            return this;
        }

        public Builder setMaxEvaluations(int maxEvaluations) {
            this.maxEvaluations = maxEvaluations;
            return this;
        }

        public Builder setTypicalX(double[] typicalX) {
            this.typicalX = typicalX;
            return this;
        }

        public Builder setSigma(double sigma) {
            this.sigma = sigma;
            return this;
        }

        public Builder setCrossover(DifferentialEvolutionCrossover crossover) {
            this.crossoverOperator = crossover;

            return this;
        }

        public Builder setSelection(DifferentialEvolutionSelection selection) {
            this.selectionOperator = selection;

            return this;
        }

        public Builder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
            this.evaluator = evaluator;

            return this;
        }

        public RotateStrategy build() {
            return new RotateStrategy(this);
        }
    }

    @Override
    protected void initProgress() {
        evaluations = 0;
        initializeInternalParameters();
    }

    @Override
    protected void updateProgress() {
        evaluations += populationSize;
        updateInternalParameters();
        System.out.println(bestSolutionEver.getObjective(0));
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newIndividual = getProblem().createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    @Override
    protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
//        for (DoubleSolution solution : population) {
//            getProblem().evaluate(solution);
//        }
//        return population;
        return evaluator.evaluate(population, getProblem());
    }

    @Override
    protected List<DoubleSolution> selection(List<DoubleSolution> population) {
        return population;
    }

    @Override
    protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
        if(getRotate() == 0){
            List<DoubleSolution> offspringPopulation = new ArrayList<>();

            for (int i = 0; i < populationSize; i++) {
                selectionOperator.setIndex(i);
                List<DoubleSolution> parents = selectionOperator.execute(matingPopulation);

                crossoverOperator.setCurrentSolution(matingPopulation.get(i));
                List<DoubleSolution> children = crossoverOperator.execute(parents);

                offspringPopulation.add(children.get(0));
            }

            return offspringPopulation;
        }
        else if(getRotate() == 1){
            List<DoubleSolution> offspringPopulation = new ArrayList<>(populationSize);

            for (int iNk = 0; iNk < lambda; iNk++) {
                offspringPopulation.add(sampleSolution());
            }

            return offspringPopulation;
        }
        else {
            return null;
        }

    }

    @Override
    protected List<DoubleSolution> replacement(List<DoubleSolution> population,
                                               List<DoubleSolution> offspringPopulation) {
        if(getRotate() == 0){
            List<DoubleSolution> pop = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                if (comparator.compare(population.get(i), offspringPopulation.get(i)) < 0) {
                    pop.add(population.get(i));
                } else {
                    pop.add(offspringPopulation.get(i));
                }
            }

            Collections.sort(pop, comparator) ;
            return pop;
        }
        else if(getRotate() == 1){
            return offspringPopulation;
        }
        else {
            return  null;
        }
    }

    @Override
    public DoubleSolution getResult() {
            return bestSolutionEver;
    }

    private void initializeInternalParameters() {

        int numberOfVariables = getProblem().getNumberOfVariables();

        WindFLO problem = (WindFLO) getProblem();
        ArrayList<double[]> grid = new ArrayList<double[]>();
        double interval = 10 * problem.getTurbineRadius();

        for (double x = 0.0; x < problem.getFarmWidth(); x += interval) {
            for (double y = 0.0; y < problem.getFarmHeight(); y += interval) {
                boolean valid = true;
                for (int o = 0; o < problem.getObstacles().length; o++) {
                    double[] obs = problem.getObstacles()[o];
                    if (x > obs[0] && y > obs[1] && x < obs[2] && y < obs[3]) {
                        valid = false;
                    }
                }
                if (valid) {
                    double[] point = {x, y};
                    grid.add(point);
                }
            }
        }

        // objective variables initial point
        // TODO: Initialize the mean in a better way

        if (typicalX != null) {
            distributionMean = typicalX;
        } else {
            distributionMean = new double[numberOfVariables];
            for (int i = 0; i < ((problem.getNumberOfVariables()) / 2); i++) {
                distributionMean[i * 2] = grid.get(i)[0];
                distributionMean[i * 2 + 1] = grid.get(i)[1];
            }
//      for (int i = 0; i < numberOfVariables; i++) {
//        distributionMean[i] = rand.nextDouble();
//      }
        }

    /* Strategy parameter setting: Selection */

        // number of parents/points for recombination
        mu = (int) Math.floor(lambda / 2);

        // muXone array for weighted recombination
        weights = new double[mu];
        double sum = 0;
        for (int i = 0; i < mu; i++) {
            weights[i] = (Math.log(mu + 1 / 2) - Math.log(i + 1));
            //System.out.println();
            sum += weights[i];
        }
        // normalize recombination weights array
        for (int i = 0; i < mu; i++) {
            weights[i] = weights[i] / sum;
        }

        // variance-effectiveness of sum w_i x_i
        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < mu; i++) {
            sum1 += weights[i];
            sum2 += weights[i] * weights[i];
        }
        muEff = sum1 * sum1 / sum2;

    /* Strategy parameter setting: Adaptation */

        // time constant for cumulation for C
        cumulationC =
                (4 + muEff / numberOfVariables) / (numberOfVariables + 4 + 2 * muEff / numberOfVariables);

        // t-const for cumulation for sigma control
        cumulationSigma = (muEff + 2) / (numberOfVariables + muEff + 5);

        // learning rate for rank-one update of C
        c1 = 2 / ((numberOfVariables + 1.3) * (numberOfVariables + 1.3) + muEff);

        // learning rate for rank-mu update
        cmu = Math.min(1 - c1,
                2 * (muEff - 2 + 1 / muEff) / ((numberOfVariables + 2) * (numberOfVariables + 2) + muEff));

        // damping for sigma, usually close to 1
        dampingSigma = 1 +
                2 * Math.max(0, Math.sqrt((muEff - 1) / (numberOfVariables + 1)) - 1) + cumulationSigma;

    /* Initialize dynamic (internal) strategy parameters and constants */

        // diagonal D defines the scaling
        diagD = new double[numberOfVariables];

        // evolution paths for C and sigma
        pathsC = new double[numberOfVariables];
        pathsSigma = new double[numberOfVariables];

        // b defines the coordinate system
        b = new double[numberOfVariables][numberOfVariables];
        // covariance matrix C
        c = new double[numberOfVariables][numberOfVariables];

        // C^-1/2
        invSqrtC = new double[numberOfVariables][numberOfVariables];

        for (int i = 0; i < numberOfVariables; i++) {
            pathsC[i] = 0;
            pathsSigma[i] = 0;
            diagD[i] = 1;
            for (int j = 0; j < numberOfVariables; j++) {
                b[i][j] = 0;
                invSqrtC[i][j] = 0;
            }
            for (int j = 0; j < i; j++) {
                c[i][j] = 0;
            }
            b[i][i] = 1;
            c[i][i] = diagD[i] * diagD[i];
            invSqrtC[i][i] = 1;
        }

        // track update of b and D
        eigenEval = 0;

        chiN = Math.sqrt(numberOfVariables) * (1 - 1 / (4 * numberOfVariables) + 1 / (21
                * numberOfVariables * numberOfVariables));

    }

    private void updateInternalParameters() {

        int numberOfVariables = getProblem().getNumberOfVariables();

        double[] oldDistributionMean = new double[numberOfVariables];
        System.arraycopy(distributionMean, 0, oldDistributionMean, 0, numberOfVariables);

        // Sort by fitness and compute weighted mean into distributionMean
        // minimization
        storeBest();

        // calculate new distribution mean and BDz~N(0,C)
        updateDistributionMean();

        // Cumulation: Update evolution paths
        int hsig = updateEvolutionPaths(oldDistributionMean);

        // Adapt covariance matrix C
        adaptCovarianceMatrix(oldDistributionMean, hsig);

        // Adapt step size sigma
        double psxps = CMAESUtils.norm(pathsSigma);
        sigma *= Math.exp((cumulationSigma / dampingSigma) * (Math.sqrt(psxps) / chiN - 1));

        // Decomposition of C into b*diag(D.^2)*b' (diagonalization)
        decomposeCovarianceMatrix();

    }

    private void updateDistributionMean() {

        int numberOfVariables = getProblem().getNumberOfVariables();

        for (int i = 0; i < numberOfVariables; i++) {
            distributionMean[i] = 0.;
            for (int iNk = 0; iNk < mu; iNk++) {
                double variableValue = (double) getPopulation().get(iNk).getVariableValue(i);
                distributionMean[i] += weights[iNk] * variableValue;
            }
        }

    }

    private int updateEvolutionPaths(double[] oldDistributionMean) {

        int numberOfVariables = getProblem().getNumberOfVariables();

        double[] artmp = new double[numberOfVariables];
        for (int i = 0; i < numberOfVariables; i++) {
            artmp[i] = 0;
            for (int j = 0; j < numberOfVariables; j++) {
                artmp[i] += invSqrtC[i][j] * (distributionMean[j] - oldDistributionMean[j]) / sigma;
            }
        }
        // cumulation for sigma (pathsSigma)
        for (int i = 0; i < numberOfVariables; i++) {
            pathsSigma[i] = (1. - cumulationSigma) * pathsSigma[i]
                    + Math.sqrt(cumulationSigma * (2. - cumulationSigma) * muEff) * artmp[i];
        }

        // calculate norm(pathsSigma)^2
        double psxps = CMAESUtils.norm(pathsSigma);

        // cumulation for covariance matrix (pathsC)
        int hsig = 0;
        if ((Math.sqrt(psxps) / Math
                .sqrt(1. - Math.pow(1. - cumulationSigma, 2. * evaluations / lambda)) / chiN) < (1.4
                + 2. / (numberOfVariables + 1.))) {
            hsig = 1;
        }
        for (int i = 0; i < numberOfVariables; i++) {
            pathsC[i] = (1. - cumulationC) * pathsC[i]
                    + hsig * Math.sqrt(cumulationC * (2. - cumulationC) * muEff)
                    * (distributionMean[i] - oldDistributionMean[i])
                    / sigma;
        }

        return hsig;

    }

    private void adaptCovarianceMatrix(double[] oldDistributionMean, int hsig) {

        int numberOfVariables = getProblem().getNumberOfVariables();

        for (int i = 0; i < numberOfVariables; i++) {
            for (int j = 0; j <= i; j++) {
                c[i][j] = (1 - c1 - cmu) * c[i][j]
                        + c1
                        * (pathsC[i] * pathsC[j] + (1 - hsig) * cumulationC
                        * (2. - cumulationC) * c[i][j]);
                for (int k = 0; k < mu; k++) {
          /*
           * additional rank mu
           * update
           */
                    double valueI = getPopulation().get(k).getVariableValue(i);
                    double valueJ = getPopulation().get(k).getVariableValue(j);
                    c[i][j] += cmu
                            * weights[k]
                            * (valueI - oldDistributionMean[i])
                            * (valueJ - oldDistributionMean[j]) / sigma
                            / sigma;
                }
            }
        }

    }

    private void decomposeCovarianceMatrix() {
        int numberOfVariables = getProblem().getNumberOfVariables();

        if (evaluations - eigenEval > lambda / (c1 + cmu) / numberOfVariables / 10) {

            eigenEval = evaluations;

            // enforce symmetry
            for (int i = 0; i < numberOfVariables; i++) {
                for (int j = 0; j <= i; j++) {
                    b[i][j] = b[j][i] = c[i][j];
                }
            }

            // eigen decomposition, b==normalized eigenvectors
            double[] offdiag = new double[numberOfVariables];
            CMAESUtils.tred2(numberOfVariables, b, diagD, offdiag);
            CMAESUtils.tql2(numberOfVariables, diagD, offdiag, b);

            checkEigenCorrectness();

            double[][] artmp2 = new double[numberOfVariables][numberOfVariables];
            for (int i = 0; i < numberOfVariables; i++) {
                if (diagD[i] > 0) {
                    diagD[i] = Math.sqrt(diagD[i]);
                }
                for (int j = 0; j < numberOfVariables; j++) {
                    artmp2[i][j] = b[i][j] * (1 / diagD[j]);
                }
            }
            for (int i = 0; i < numberOfVariables; i++) {
                for (int j = 0; j < numberOfVariables; j++) {
                    invSqrtC[i][j] = 0.0;
                    for (int k = 0; k < numberOfVariables; k++) {
                        invSqrtC[i][j] += artmp2[i][k] * b[j][k];
                    }
                }
            }

        }

    }

    private void checkEigenCorrectness() {
        int numberOfVariables = getProblem().getNumberOfVariables();

        if (CMAESUtils.checkEigenSystem(numberOfVariables, c, diagD, b) > 0) {
            evaluations = maxEvaluations;
        }

        for (int i = 0; i < numberOfVariables; i++) {
            // Numerical problem?
            if (diagD[i] < 0) {
                JMetalLogger.logger.severe(
                        "CovarianceMatrixAdaptationEvolutionStrategy.updateDistribution:" +
                                " WARNING - an eigenvalue has become negative.");
                evaluations = maxEvaluations;
            }
        }

    }

    private DoubleSolution sampleSolution() {

        DoubleSolution solution = getProblem().createSolution();

        int numberOfVariables = getProblem().getNumberOfVariables();
        double[] artmp = new double[numberOfVariables];
        double sum;

        for (int i = 0; i < numberOfVariables; i++) {
            //TODO: Check the correctness of this random (http://en.wikipedia.org/wiki/CMA-ES)
            artmp[i] = diagD[i] * rand.nextGaussian();
        }
        for (int i = 0; i < numberOfVariables; i++) {
            sum = 0.0;
            for (int j = 0; j < numberOfVariables; j++) {
                sum += b[i][j] * artmp[j];
            }
            double old = solution.getVariableValue(i);
            //System.out.println(i+",old:"+old+",distributionMean[i]:"+distributionMean[i]+",sigma*sum:"+sigma+"*"+"sum"+"="+sigma*sum);

            double value = distributionMean[i] + sigma * sum;
            //System.out.println(value);
            if (value > ((DoubleProblem) getProblem()).getUpperBound(i)) {
                value = ((DoubleProblem) getProblem()).getUpperBound(i);
            } else if (value < ((DoubleProblem) getProblem()).getLowerBound(i)) {
                value = ((DoubleProblem) getProblem()).getLowerBound(i);
            }

            solution.setVariableValue(i, value);
        }

        return solution;
    }

    private void storeBest() {
        Collections.sort(getPopulation(), comparator);
        if ((bestSolutionEver == null) || (bestSolutionEver.getObjective(0) > getPopulation().get(0)
                .getObjective(0))) {
            bestSolutionEver = getPopulation().get(0);
        }
    }

    @Override
    public String getName() {
        return "ROTATE";
    }

    @Override
    public String getDescription() {
        return "Rotate Strategy";
    }

}