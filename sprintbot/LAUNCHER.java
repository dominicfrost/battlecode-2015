package sprintbot;

import battlecode.common.*;

public class LAUNCHER {
    public static RobotController rc;
    public static boolean atGoal = false;
    public static MapLocation goal = null;
    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    public static int numTowers;
    public static MapLocation target;

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        if (rc.getSupplyLevel() < 300) {
            Util.moveToLocation(rc, RobotPlayer.myHq);
        } else {
            target = new MapLocation(rc.readBroadcast(MyConstants.TARGET_TOWER_X), rc.readBroadcast(MyConstants.TARGET_TOWER_Y));

            Direction targetDir = rc.getLocation().directionTo(target);
            if (rc.getLocation().add(targetDir).distanceSquaredTo(target) < 24) {
                smartLaunch(rc.getLocation().directionTo(target));
            } else {
                Pathing.straitBuggin(rc, target, true);
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