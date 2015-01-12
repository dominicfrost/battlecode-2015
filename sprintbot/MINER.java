package sprintbot;

import battlecode.common.*;

public class MINER {
    public static RobotController rc;
    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        int executeStartRound = Clock.getRoundNum();
        if (rc.isCoreReady()) {
            Util.SmartMine(rc);
        }
        if (executeStartRound == Clock.getRoundNum()) {
            rc.yield();
        }
    }
}