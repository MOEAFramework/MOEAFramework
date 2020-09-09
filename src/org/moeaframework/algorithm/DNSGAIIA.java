package org.moeaframework.algorithm;

import org.moeaframework.core.*;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
/**
 * Implementation of the first version of Dynamic NSGA-II algorithm proposed by Kalyanmoy Deb et. al. by extending the
 * original {@link NSGAII} algorithm and implementing the {@link DynamicAlgorithm}
 * <p>
 * References:
 * <ol>
 * <li>Deb K., Rao N. U.B., Karthik S. (2007) Dynamic Multi-objective Optimization and Decision-Making Using Modified
 * NSGA-II: A Case Study on Hydro-thermal Power Scheduling. In: Obayashi S., Deb K., Poloni C., Hiroyasu T.,
 * Murata T. (eds) Evolutionary Multi-Criterion Optimization. EMO 2007. Lecture Notes in Computer Science,
 * vol 4403. Springer, Berlin, Heidelberg
 * </ol>
 */
public class DNSGAIIA extends NSGAII implements DynamicAlgorithm {
    /**
    * The detection operator
    */
    private final Detection detection;
    /**
     *  operator
     */
    private final double zeta;

    /**
     * Constructs the NSGA-II algorithm with the specified components.
     *
     * @param problem        the problem being solved
     * @param population     the population used to store solutions
     * @param archive        the archive used to store the result; can be {@code null}
     * @param selection      the selection operator
     * @param variation      the variation operator
     * @param initialization
     * @param detection      the detection operator
     * @param zeta           the zeta variable
     */
    public DNSGAIIA(Problem problem, NondominatedSortingPopulation population, EpsilonBoxDominanceArchive archive, Selection selection, Variation variation, Initialization initialization, Detection detection, double zeta) {
        super(problem, population, archive, selection, variation, initialization);
        this.detection = detection;
        this.zeta = zeta;
    }

    public DNSGAIIA(Problem problem, NondominatedSortingPopulation population, EpsilonBoxDominanceArchive archive, Selection selection, Variation variation, Initialization initialization, Detection detection) {
        this(problem, population, archive, selection, variation, initialization, detection, 0.2d);
    }

    @Override
    public void iterate() {
        super.iterate();
        if(detection.isEnvironmentChanged(problem, StreamSupport.stream(population.spliterator(),true).toArray(Solution[]::new)))
            stepOnChange();
    }

    @Override
    public void stepOnChange() {
        int[] indexes = ThreadLocalRandom.current().ints(0, population.size()).distinct().limit((int)(population.size()*this.zeta)).toArray();
        population.removeAll(Arrays.stream(indexes).boxed().map(population::get).collect(Collectors.toList()));
        Stream.generate(problem::newSolution).limit(indexes.length).forEach(population::add);
    }
}
