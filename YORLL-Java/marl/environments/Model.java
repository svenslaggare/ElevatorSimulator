package marl.environments;

/**
 * An environment Model is able to predict, given a state and an action, what
 * the resulting state and reward would be.
 * 
 * @author pds
 *
 * @param <S> An object which extends State
 */
public interface Model<S extends State<S>>
{
    /**
     * A Sample is a prediction of state transition of an environment.
     * 
     * @author pds
     *
     * @param <S> An object which extends State
     */
    public static final class Sample<S extends State<S>> {
        public double  reward;
        public S       next;
        public boolean terminal;
        
        public Sample(S next, double reward, boolean terminal) {
            this.next     = next;
            this.reward   = reward;
            this.terminal = terminal;
        }
        @Override
        public String toString() {
            return "[reward="+reward+", next="+next+", terminal="+terminal+"]";
        }
    }

    
    
    /**
     * Given a State and an action this method returns the models prediction
     * of the next state and the reward for the transition. It returns this
     * prediction as a sample which contains the the next state and the reward
     * that would be received.
     * 
     * @param state  The state in which the action should be taken
     * @param action The action to be taken
     * @return       A prediction of the state transition including the next
     *               state and the reward
     */
	Sample<S> getSample(S state, int action);
}
