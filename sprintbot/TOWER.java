package sprintbot;

import battlecode.common.*;

public class TOWER {
    public static RobotController rc;
    public static int prevHealth = 1000;

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
        int currHealth = rc.getHealth();
        if (currHealth < prevHealth) {
            rc.broadCast(TOWER_UNDER_DISTRESS + getTowerOrdinal(), 1);
        } else {
            rc.broadCast(TOWER_UNDER_DISTRESS + getTowerOrdinal(), 0);
        }
        Util.attack(rc, enemyRobots);
        prevHealth = rc.getHealth();
    }

    public static int getTowerOrdinal() {
        for (int i = 0; i < RobotPlayer.myTowers; i++) {
            if (myLocation.equals(RobotPlayer.myTowers[i])) {
                return i;
            }
        }
        return -1;
    }
}