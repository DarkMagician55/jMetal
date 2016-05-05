package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by guotong on 04/05/16.
 *
 * @param <S> Solution
 * @param <R> Result
 */
@SuppressWarnings("serial")
public abstract class AbstractRotateAlgorithm<S extends Solution<?>, R> implements Algorithm<R> {

    private int rotate;// 0 is DE , 1 is CMAES
    private List<S> population;
    private int maxPopulationSize;
    private Problem<S> problem;

    public List<S> getPopulation() {
        return population;
    }

    public void setPopulation(List<S> population) {
        this.population = population;
    }

    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }

    public int getMaxPopulationSize() {
        return maxPopulationSize;
    }

    public void setProblem(Problem<S> problem) {
        this.problem = problem;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getRotate() {
        return rotate;
    }

    protected abstract void initProgress();

    protected abstract void updateProgress();

    protected abstract boolean isStoppingConditionReached();

    protected abstract List<S> createInitialPopulation();

    protected abstract List<S> evaluatePopulation(List<S> population);

    protected abstract List<S> selection(List<S> population);

    protected abstract List<S> reproduction(List<S> population);

    protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation);

    @Override
    public abstract R getResult();

    @Override
    public void run() {
        int rotateNum = 0;
        List<S> offspringPopulation;
        List<S> matingPopulation;

        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
        while (!isStoppingConditionReached()) {
            setRotate(rotateNum%2);
            //setRotate(1);
            matingPopulation = selection(population);
            offspringPopulation = reproduction(matingPopulation);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            population = replacement(population, offspringPopulation);
            updateProgress();
            rotateNum++;
        }
    }
}
