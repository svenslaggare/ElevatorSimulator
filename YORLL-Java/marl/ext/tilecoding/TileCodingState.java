package marl.ext.tilecoding;

import marl.environments.State;

/**
 * Grid Tile State is an extension of the State interface which ensures that a
 * specific feature of the Grid Tile can be requested via the
 * getFeature(int featureNo) method.
 */
public interface TileCodingState<S extends TileCodingState<S>>
	extends State<S>
{
    /**
     * @return double The value of the requested feature
     */
	double getFeature(int featureNo);
}
