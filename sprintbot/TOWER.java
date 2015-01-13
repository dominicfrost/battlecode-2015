package sprintbot;

import battlecode.common.*;

public class TOWER {
    public static RobotController rc;
    public static double prevHealth = 1000;
    public static boolean first = true;

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        if (first) {
            first = false;
            int ordinal = getTowerOrdinal();
            MapLocation location = rc.getLocation();
            rc.broadcast(MyConstants.TOWER_UNDER_DISTRESS_LOCATION + ordinal, location.x);
            rc.broadcast(MyConstants.TOWER_UNDER_DISTRESS_LOCATION + ordinal + 1, location.y);
        }

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
                return i * 2;
            }
        }
        return -1;
    }
}