package davebot;

import battlecode.common.*;
import java.util.*;

public class Util {
    public static Random rand = new Random();
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

    public static boolean straitBuggin(RobotController rc, MapLocation goal) throws GameActionException {
        ArrayList<MapLocation> mLine = calcMLine(rc, goal);
        MapLocation myLocation;
        MapLocation nextLocation;
        Direction currentDir;
        Direction nextLocationDir;

        while (true) {
            //get my location, if its the goal quit
            myLocation = rc.getLocation();
            if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                System.out.println("my location" + myLocation.toString());
            }
            if (myLocation.equals(goal)) {
                return true;
            }

            //if we are here then we shold be moving on the mLine
            //if we aren't on the mLine we f'd up so quit out
            int myLocationIndex = mLine.indexOf(myLocation);
            if (myLocationIndex == -1 ) {
                System.out.println("BUG FAILURE: not on mLine when i should be");
                return false;
            }

            //get the next location on the mLine and try to move there
            nextLocation = mLine.get(myLocationIndex + 1);
            if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                System.out.println("locations (mine, goal)  " + myLocation.toString() + " " + nextLocation.toString());
            }

            nextLocationDir = myLocation.directionTo(nextLocation);
            if (rc.canMove(nextLocationDir)) {
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("Going to " + nextLocation.toString());
                }
                doMove(rc, nextLocationDir);
                continue;
            } else {
                //we could not move along the mLine, bug around the wall
                //until we reach one of our loop conditons are met
                MapLocation startingPoint = myLocation;
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("Puttin hand on wall, tried to go " + nextLocationDir.toString());
                }
                putHandOnWall(rc, nextLocationDir, mLine);
            }

        }

    }

    public static void putHandOnWall(RobotController rc, Direction startDir, ArrayList<MapLocation> mLine) throws GameActionException  {
        Direction rightDir = startDir;
        Direction leftDir = startDir;

        while (true) {
            rightDir = rightDir.rotateRight();
            leftDir = leftDir.rotateLeft();

            if (rc.canMove(rightDir)) {
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("Move in the direction: " + rightDir.toString());
                }
                doMove(rc, rightDir);
                followWall(rc, rightDir, true, mLine);
                return;
            }

            if (rc.canMove(leftDir)) {
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("Move in the direction: " + leftDir.toString() + " from " + rc.getLocation());
                }
                doMove(rc, leftDir);
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("Moved, new location: " + rc.getLocation());
                }
                followWall(rc, leftDir, false, mLine);
                return;
            }

            if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                System.out.println("couldnt get hand on wall trying again");
            }
        }
    }

    public static void followWall(RobotController rc, Direction myDir, boolean movedClockwise, ArrayList<MapLocation> mLine) throws GameActionException  {
        while(true) {
            if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                System.out.println("checking if in mLine " + rc.getLocation().toString());
            }
            if (mLine.contains(rc.getLocation())) {
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("Back on mLine, quiting out of followWall, " + rc.getLocation().toString());
                }
                return;
            }

            //if i can go in towards the mline do it
            Direction backInwards = rotateInDir(myDir, movedClockwise);



            //backInwards = rotateInDir(backInwards, movedClockwise);
            if (rc.canMove(backInwards)) {
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("backIn Move in the direction: " + backInwards.toString());
                }
                if(mLine.contains(rc.getLocation().add(myDir))) {
                    doMove(rc, myDir);
                    return;
                }
                doMove(rc, backInwards);
                myDir = rotateInDir(backInwards, movedClockwise);
                continue;
            }

            //if i can go strait do it
            if (rc.canMove(myDir)) {
                if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                    System.out.println("strait Move in the direction: " + myDir.toString());
                }
                doMove(rc, myDir);
                continue;
            }

            //rotate outwards until you can move
            int turns = 0;
            while (true) {
                turns++;
                myDir = rotateInDir(myDir, !movedClockwise);
                if (rc.canMove(myDir)) {
                    if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
                        System.out.println("outwards Move in the direction: " + myDir.toString());
                    }
                    doMove(rc, myDir);
                    int completeTurn = turns % 2;
                    if (completeTurn == 0) {
                        myDir = rotateInDir(myDir, movedClockwise);
                    }
                    break;
                }
            }
        }
    }

    public static Direction rotateInDir(Direction startDir, boolean rotateLeft) {
        if (rotateLeft) {
            return startDir.rotateLeft();
        } else {
            return startDir.rotateRight();
        }
    }

    public static void doMove(RobotController rc, Direction dir) throws GameActionException{
        while (!rc.isCoreReady()) {
            rc.yield();
        }
        while (!rc.canMove(dir)) {
            rc.yield();
        }
        rc.move(dir);
        rc.yield();
    }

    public static ArrayList<MapLocation> calcMLine(RobotController rc, MapLocation goal) {
        Direction dirToGoal;
        ArrayList<MapLocation> mLine = new ArrayList<MapLocation>();
        MapLocation previousLocation = rc.getLocation();

        while (!previousLocation.equals(goal)) {
            mLine.add(previousLocation);
            dirToGoal = previousLocation.directionTo(goal);
            previousLocation = previousLocation.add(dirToGoal);
        }
        if (rc.getID() == 33514 && Clock.getRoundNum() < 500) {
            System.out.print("mLine: ");
            for (int i = 0; i < mLine.size(); i++) {
                System.out.print(mLine.get(i).toString() + ", ");
            }
            System.out.println();
        }

        return mLine;
    }
}