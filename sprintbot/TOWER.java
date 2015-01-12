package sprintbot;

import battlecode.common.*;

public class TOWER {
    public static RobotController rc;
    public static double prevHealth = 1000;

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
        double currHealth = rc.getHealth();
        if (currHealth < prevHealth) {
            rc.broadcast(MyConstants.TOWER_UNDER_DISTRESS + getTowerOrdinal(), 1);
        } else {
            rc.broadcast(MyConstants.TOWER_UNDER_DISTRESS + getTowerOrdinal(), 0);
        }
        Util.attack(rc, enemyRobots);
        prevHealth = rc.getHealth();
    }

    public static int getTowerOrdinal() {
        MapLocation myLocation = rc.getLocation();
        for (int i = 0; i < RobotPlayer.myTowers.length; i++) {
            if (myLocation.equals(RobotPlayer.myTowers[i])) {
                return i;
            }
        }
        return -1;
    }
}