package davebot;

import battlecode.common.*;
import java.util.ArrayDeque;

public class Util {
   public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    // This method will attempt to move in Direction d (or as close to it as possible)
    public static void tryMove(RobotController rc, Direction d) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 5) {
            rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
        }
    }


    // This method will attack an enemy in sight, if there is one
    public static void attackSomething(RobotController rc, int myRange, Team enemyTeam) throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
        if (enemies.length > 0) {
            rc.attackLocation(enemies[0].location);
        }
    }

    // This method will attempt to build in the given direction (or as close to it as possible)
    public static void tryBuild(RobotController rc, Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }

    public static Direction intToDirection(int i) {
        switch(i) {
            case 0:
                return Direction.NORTH;
            case 1:
                return Direction.NORTH_EAST;
            case 2:
                return Direction.EAST;
            case 3:
                return Direction.SOUTH_EAST;
            case 4:
                return Direction.SOUTH;
            case 5:
                return Direction.SOUTH_WEST;
            case 6:
                return Direction.WEST;
            case 7:
                return Direction.NORTH_WEST;
            default:
                return Direction.NORTH;
        }
    }

    public static int directionToInt(Direction d) {
        switch(d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }

    public static boolean flee(RobotController rc, RobotInfo[] enemyRobots) throws GameActionException{
        ArrayDeque<MapLocation> enemieLocs = new ArrayDeque<MapLocation>();
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
            tryMove(rc, dirToGoal);
            return true;
        }

        return false;
    }

    public static int[] getRobotCount(RobotController rc) throws GameActionException{
        int[] robotCount = new int[21];
        for (int i = 0; i < robotCount.length; i++) {
            robotCount[i] = rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + i);
        }

        return robotCount;
    }

    // This method will attempt to spawn in the given direction (or as close to it as possible)
    public static void trySpawn(RobotController rc, Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = Util.directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canSpawn(Util.directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.spawn(Util.directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }
}