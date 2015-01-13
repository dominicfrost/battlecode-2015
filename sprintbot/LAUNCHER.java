package sprintbot;

import battlecode.common.*;

public class LAUNCHER {
    public static RobotController rc;
    public static boolean atGoal = false;
    public static MapLocation goal = null;
    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    public static int numTowers;


    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        MapLocation target = new MapLocation(rc.readBroadcast(MyConstants.TARGET_TOWER_X), rc.readBroadcast(MyConstants.TARGET_TOWER_Y));

        Direction targetDir = rc.getLocation().directionTo(target);
        if (rc.getLocation().add(targetDir).distanceSquaredTo(target) < 24) {
            tryLaunch(rc);
        } else {
            Pathing.straitBuggin(rc, target, true);
        }
    }

    public static boolean tryLaunch(RobotController rc) throws GameActionException{
        if (rc.isCoreReady() && rc.getMissileCount() > 0) {
            // get the direction to launch
            // this direction is the direction to the weighted avg of enemy locations
            RobotInfo[] enemiesInRange = rc.senseNearbyRobots(25, RobotPlayer.enemyTeam);
            if (enemiesInRange.length > 0) {
                int counterx = 0;
                int countery = 0;
                for (RobotInfo opp : enemiesInRange) {
                    counterx += opp.location.x;
                    countery += opp.location.y;
                }


                MapLocation avg = new MapLocation(counterx / enemiesInRange.length, countery / enemiesInRange.length);
                Direction dirToGoal = rc.getLocation().directionTo(avg);
                smartLaunch(dirToGoal);
                return true;
            }
        }

        return false;
    }

    public static void smartLaunch(Direction d) throws GameActionException{
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2};
        int dirint = Util.directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 5 && !rc.canLaunch(directions[(dirint + offsets[offsetIndex] + 8) % 8])) {
            offsetIndex++;
        }
        if (offsetIndex < 5) {
            rc.launchMissile(directions[(dirint + offsets[offsetIndex] + 8) % 8]);
        }
    }
}