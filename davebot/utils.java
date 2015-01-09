package davebot;

import battlecode.common.*;
import java.util.ArrayList;

public class utils {
    /*
    * This method will attempt to spawn in the given direction (or as close to it as possible)
    */
    public static void smartSpawn(RobotController rc, RobotType type) {
//        // This method will attempt to spawn in the given direction (or as close to it as possible)
//        int offsetIndex = 0;
//        int[] offsets = {0,1,-1,2,-2,3,-3,4};
//        int dirint = directionToInt(d);
//        boolean blocked = false;
//        while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
//            offsetIndex++;
//        }
//        if (offsetIndex < 8) {
//            rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
//        }
    }

    static void smartMove(RobotController rc, Direction d) throws GameActionException {
//        Direction[] directions = Direction.values();
//        int offsetIndex = 0;
//        int[] offsets = {0,1,-1,2,-2};
//        int dirint = directionToInt(d);
//        while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
//            offsetIndex++;
//        }
//        if (offsetIndex < 5) {
//            rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
//        }
    }

    public static boolean flee(RobotController rc, RobotInfo[] enemyRobots) {
        ArrayList<MapLocation> enemieLocs = new ArrayList<MapLocation>();
        for (RobotInfo robot: enemyRobots) {
            if (robot.type.attackRadiusSquared <= robot.location.distanceSquaredTo(rc.getLocation())) {
                enemieLocs.add(robot.location);
            }
        }
        if (enemieLocs.size() > 0) {
            int counterx = 0;
            int countery = 0;
            for (MapLocation oppLoc : enemieLocs) {
                counterx += oppLoc.x;
                countery += oppLoc.y;
            }

            MapLocation avg = new MapLocation(counterx / enemieLocs.size(), countery / enemieLocs.size());
            Direction dirToGoal = rc.getLocation().directionTo(avg).opposite();
            utils.smartMove(rc, dirToGoal);
            return true;
        }

        return false;
    }

    public static int[] getRobotCount(RobotController rc) {
        int[] robotCount = new int[21];
        for (int i = 0; i < robotCount.length; i++) {
            robotCount[i] = rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + i);
        }

        return robotCount;
    }
}