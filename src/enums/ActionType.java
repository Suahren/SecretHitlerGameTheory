package enums;

/**
 * Action type enum
 */
public enum ActionType {

    //Policy Types
    DISCARD,            //Discard a policy
    PLAY,               //Play a policy
    DECLARE_PASSED,     //Declare what policies were passed to the chancellor
    DECLARE_DISCARD,    //Declare what policy was discarded
    VETO,               //Veto a pair of policies

    //Player Types
    SELECT,             //Select a president or chancellor
    ACCUSE,             //Accuse a player
    INVESTIGATE,        //Investigate a player
    SHOOT,              //Shoot a player
    VOTE_YES,           //Voted for a president/chancellor combo
    VOTE_NO             //Voted against a president/chancellor combo
}
