package davebot;

import battlecode.common.*;

public class TANK {
    public static RobotController rc;

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        int executeStartRound = Clock.getRoundNum();
        Util.generalAttack(rc);
        if (executeStartRound == Clock.getRoundNum()) {
            rc.yield();
        }
    }
}