/**
 * 
 */
package marl.agents;

import marl.environments.Stateless;
import marl.environments.StatelessEnvironment;


/**
 * <p>Defines an agent in an environment with a single state - "Stateless" - and an integer time.
 * <p>If you extend this environment, you don't have to implement the "perceive" method, as the state is the same each time.
 * @author Erel Segal the Levite
 * @since 2012-12-09
 */
public abstract class StatelessAgent<E extends StatelessEnvironment<?>> 
	extends Agent<E>
{
	protected static final Stateless state_ = new Stateless();

    @Override public abstract void initialise();
    
    @Override public void add(E env) {
        super.add(env);
    }

    @Override public abstract void reset(int episodeNo);

    @Override public abstract void update(double reward, boolean terminal);

    @Override protected void perceive() {
        // do nothing, this is a stateless game
    }

    @Override protected abstract void reason(int time);

    @Override protected abstract void act();
}
