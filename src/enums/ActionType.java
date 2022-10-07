package enums;

/**
 * Action type enum
 */
public enum ActionType {

    //Policy Types
    PLAY,               //Play a policy
    DISCARD,            //Discard a policy
    PASS,               //Pass policies to the chancellor
    DECLARE_DISCARDED,  //Declare what policy was discarded
    DECLARE_PASSED,     //Declare what policies were passed to the chancellor

    VETO,               //Veto a pair of policies

    //Player Types
    SELECT,             //Select a president or chancellor
    ACCUSE,             //Accuse a player
    INVESTIGATE,        //Investigate a player
    SHOOT,              //Shoot a player
    VOTE_YES,           //Voted for a president/chancellor combo
    VOTE_NO             //Voted against a president/chancellor combo
}
