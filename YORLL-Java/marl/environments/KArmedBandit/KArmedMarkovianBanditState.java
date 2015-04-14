/**
 * 
 */
package marl.environments.KArmedBandit;

import java.util.Arrays;

import marl.environments.State;


/**
 * The state of a Markovian k-armed bandit problem is the concatenation of the states of each machine.
 * 
 * @author Erel Segal Halevi
 * @since  2012-12-25
 *
 */
public class KArmedMarkovianBanditState implements
        State<KArmedMarkovianBanditState>
{
    /**
     * The state of each bandit.
     */
    private int[] states_;

    
    /**
     * @param numOfBandits The number of bandits
     */
    public KArmedMarkovianBanditState(int numOfBandits)
    {
       states_ = new int[numOfBandits]; 
    }
    public KArmedMarkovianBanditState(int[] states)
    {
        this.states_ = states;
    }
    
    
    /**
     * Returns the state of the specified bandit.
     * @param bandit The bandit whose state is required
     * @return The value of the specified bandits state
     */
    public int getState(int bandit) {
        return states_[bandit];
    }
    
    
    /**
     * Sets the value of the state of the specified bandit to the given
     * value.
     * @param bandit The bandit whose state will be updated
     * @param val    The new value it will become
     */
    public void setState(int bandit, int val) {
        states_[bandit] = val;
    }


    /* (non-Javadoc)
     * @see marl.environments.State#set(marl.environments.State)
     */
    @Override
    public void set(KArmedMarkovianBanditState that) {
        for( int bandit=0; bandit<states_.length; bandit++ )
            states_[bandit] = that.states_[bandit];
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(states_);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KArmedMarkovianBanditState other = (KArmedMarkovianBanditState) obj;
        if (!Arrays.equals(states_, other.states_))
            return false;
        return true;
    }

    @Override public String toString() {
        return "KArmedMarkovianBanditState [states=" + Arrays.toString(states_) + "]";
    }
}
