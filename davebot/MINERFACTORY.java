package davebot;

import battlecode.common.*;

public class MINERFACTORY {
    public static RobotController rc;
    public static RobotType[] canSpawn = {RobotType.MINER};
    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        int executeStartRound = Clock.getRoundNum();

        Util.spawnWithPrecedence(rc, Direction.NORTH, canSpawn);

        if (executeStartRound == Clock.getRoundNum()) {
            rc.yield();
        }
    }
}