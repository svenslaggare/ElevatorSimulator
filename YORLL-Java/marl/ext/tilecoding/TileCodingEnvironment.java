package marl.ext.tilecoding;

import marl.agents.Agent;
import marl.environments.Environment;


/**
 * @author pds
 *
 */
public interface TileCodingEnvironment<S extends TileCodingState<S>, A extends Agent<?>>
    extends Environment<S, A>
{
    /**
     * @return The number of features in the Environment
     */
    int   getNumFeatures();
    /**
     * @param feature The feature number
     * @return The minimum value of the specified feature
     */
    double getMinimumValue(int feature);
    /**
     * @param feature The feature number
     * @return The maximum value of the specified feature
     */
    double getMaximumValue(int feature);
}
