package davebot;

import battlecode.common.*;

public class TOWER {
    public static RobotController rc;

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        int executeStartRound = Clock.getRoundNum();
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
        Util.attack(rc, enemyRobots);
        if (executeStartRound == Clock.getRoundNum()) {
            rc.yield();
        }
    }
}