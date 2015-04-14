/**
 * 
 */
package experiments.example;

import marl.agents.Agent;
import marl.environments.PuddleWorld.PuddleWorldEnvironment;
import marl.environments.PuddleWorld.PuddleWorldState;
import marl.ext.tilecoding.ModelTileCodeLearning;
import marl.utility.Config;


/**
 * @author pds
 *
 */
public class PuddleWorldAgentTC
    extends Agent<PuddleWorldEnvironment<PuddleWorldAgentTC>>
{
    private Config           cfg_;
    private int              action_;         // the action choice
    private PuddleWorldState currentState_,   // the current state
                             previousState_;  // the previous state
                                              // the learning algorithm used

    private ModelTileCodeLearning<PuddleWorldState>
                             learning_;
    
    
    
    public PuddleWorldAgentTC(Config cfg)
    {
        cfg_ = cfg;
    }
    
    
    public int getNoTiles() {
        return learning_.getNoTiles();
    }
    
    

    @Override
    public void initialise()
    {
        currentState_   = new PuddleWorldState();
        previousState_  = new PuddleWorldState();
        action_         = 0;
    }
    
    @Override
    public void add(PuddleWorldEnvironment<PuddleWorldAgentTC> env)
    {
        super.add(env);
        learning_ = new ModelTileCodeLearning<>(cfg_, env);
        learning_.setEnvironmentModel(environment);
        
        learning_.inform(env.getNumActions(this));
    }

    @Override
    public void reset(int episodeNo)
    {
        // decrease the value of epsilon
        learning_.decreaseEpsilon(episodeNo);
        
        // perceive the starting state
        perceive();
    }

    @Override
    public void update(double reward, boolean terminal)
    {
        // perceive the newly entered state
        perceive();

        // update the learning algorithm
        if( !terminal )
            learning_.update(previousState_, currentState_, action_, reward);
        else
            learning_.update(currentState_, null, action_, reward);
        
        // instead of using the actual state transitions to learn
        // get a non-terminal state from the environment
        // sample the result of an action on it and learn from that
//        PuddleWorldState state;
//        do {
//            state = env_.getState();
//        } while( env_.isTerminal(state) );
//        int                            action = learning_.select(state);
//        Model.Sample<PuddleWorldState> sample = env_.getSample(state, action);
//        if( !sample.terminal )
//            learning_.update(state, sample.next, action_, reward);
//        else
//            learning_.update(state, null, action_, reward);
    }

    @Override
    protected void perceive()
    {
        // store the previous state
        previousState_.set(currentState_);
        // perceive the state
        currentState_.set(environment.getState(this));

        // no need to inform q learning of new actions
        // since they never change
    }

    @Override
    protected void reason(int time)
    {
        // use E-greedy to select the next action
        action_ = learning_.select(currentState_);
    }

    @Override
    protected void act()
    {
        environment.performAction(this, action_);
    }
}
