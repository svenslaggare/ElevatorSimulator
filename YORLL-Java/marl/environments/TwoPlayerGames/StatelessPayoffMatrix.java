/**
 * 
 */
package marl.environments.TwoPlayerGames;

import marl.utility.Config;


/**
 * @author pds
 * @since  2013-30-08
 *
 */
public class StatelessPayoffMatrix
{
    /**
     * The array index for player A.
     */
    public static final int playerA = 0;
    /**
     * The array index for player B.
     */
    public static final int playerB = 1;
    
    
    /**
     * The payoffs for player A.
     */
    private int[][] playerAPayoffs_;
    /**
     * The payoffs for player A.
     */
    private int[][] playerBPayoffs_;
    /**
     * The number of actions available to player A.
     */
    private int     nActionsA_;
    /**
     * The number of actions available to player B.
     */
    private int     nActionsB_;

    
    
    /**
     * @param cfg The configuration for this payoff matrix
     */
    public StatelessPayoffMatrix(Config cfg)
    {
        // Get the number of actions
        nActionsA_ = cfg.getInt("number_of_actions_playerA");
        nActionsB_ = cfg.getInt("number_of_actions_playerB");
        // Initialise the payoff matrices
        playerAPayoffs_ = new int[nActionsA_][nActionsB_];
        playerBPayoffs_ = new int[nActionsA_][nActionsB_];
        
        int[] tmp;
        // Collect playerA payoffs
        tmp = cfg.getIntArray("playerA_payoffs");
        for( int i=0, k=0; i<nActionsA_; i++ )
            for( int j=0; j<nActionsB_; j++, k++ )
                playerAPayoffs_[i][j] = tmp[k];
        
        // Collect playerB payoffs
        tmp = cfg.getIntArray("playerB_payoffs");
        for( int i=0, k=0; i<nActionsA_; i++ )
            for( int j=0; j<nActionsB_; j++, k++ )
                playerBPayoffs_[i][j] = tmp[k];
    }
    
    
    /**
     * @param player The player to get the number of available actions
     *               [playerA | playerB]
     * @return The number of actions available to the specified player
     */
    public int getNumActions(int player) {
        switch( player ) {
            case playerA: return nActionsA_;
            case playerB: return nActionsB_;
            default:      return 0;
        }
    }
    
    
    /**
     * @param actionA The action performed by player A
     * @param actionB The action performed by player B
     * @return        The payoff for player A
     */
    public int getPlayerAPayoff(int actionA, int actionB) {
        return playerAPayoffs_[actionA][actionB];
    }
    /**
     * @param actionA The action performed by player A
     * @param actionB The action performed by player B
     * @return        The payoff for player B
     */
    public int getPlayerBPayoff(int actionA, int actionB) {
        return playerBPayoffs_[actionA][actionB];
    }
}
