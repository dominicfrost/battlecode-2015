package sprintbot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import battlecode.common.*;

public class Pathing {

    static Direction allDirections[] = Direction.values();

    public static boolean straitBuggin(RobotController rc, MapLocation goal) throws GameActionException {
        ArrayList<MapLocation> mLine = calcMLine(rc, goal);
        MapLocation myLocation;
        MapLocation nextLocation;
        Direction currentDir;
        Direction nextLocationDir;

        while (true) {
            //get my location, if its the goal quit
            myLocation = rc.getLocation();
            if (myLocation.distanceSquaredTo(goal) < 1.5) {
                return true;
            }

            //if we are here then we shold be moving on the mLine
            //if we aren't on the mLine we f'd up so quit out
            int myLocationIndex = mLine.indexOf(myLocation);
            if (myLocationIndex == -1) {
                System.out.println("BUG FAILURE: not on mLine when i should be");
                return false;
            }

            //get the next location on the mLine and try to move there
            nextLocation = mLine.get(myLocationIndex + 1);

            nextLocationDir = myLocation.directionTo(nextLocation);
            if (canMove(rc, nextLocationDir)) {
                if (!doMove(rc, nextLocationDir)) return false;
                continue;
            } else {
                //we could not move along the mLine, bug around the wall
                //until we reach one of our loop conditons are met
                MapLocation startingPoint = myLocation;
                if (!putHandOnWall(rc, nextLocationDir, mLine)) return false;
            }
        }
    }

    public static boolean putHandOnWall(RobotController rc, Direction startDir, ArrayList<MapLocation> mLine) throws GameActionException {
        Direction rightDir = startDir;
        Direction leftDir = startDir;

        while (true) {
            rightDir = rightDir.rotateRight();
            leftDir = leftDir.rotateLeft();

            if (canMove(rc, rightDir)) {
                if (!doMove(rc, rightDir)) return false;
                return followWall(rc, rightDir, true, mLine);
            }

            if (canMove(rc, leftDir)) {
                if (!doMove(rc, leftDir)) return false;
                return followWall(rc, leftDir, false, mLine);
            }
        }
    }

    public static boolean followWall(RobotController rc, Direction myDir, boolean movedClockwise, ArrayList<MapLocation> mLine) throws GameActionException {
        while (true) {
            if (mLine.contains(rc.getLocation())) {
                return true;
            }

            //if i can go in towards the mline do it
            Direction backInwards = rotateInDir(myDir, movedClockwise);
            if (canMove(rc, backInwards)) {
                if (mLine.contains(rc.getLocation().add(myDir))) {
                    return doMove(rc, myDir);
                }
                if (!doMove(rc, backInwards)) return false;
                myDir = rotateInDir(backInwards, movedClockwise);
                continue;
            }

            //if i can go strait do it
            if (canMove(rc, myDir)) {
                if (!doMove(rc, myDir)) return false;
                continue;
            }

            //rotate outwards until you can move
            int turns = 0;
            while (true) {
                turns++;
                myDir = rotateInDir(myDir, !movedClockwise);
                if (canMove(rc, myDir)) {
                    if (!doMove(rc, myDir)) return false;
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

    public static boolean canMove(RobotController rc, Direction dir) throws GameActionException {
        if (!rc.canMove(dir)) {
            return false;
        }

        if (rc.getLocation().add(dir).distanceSquaredTo(RobotPlayer.enemyHq) <= 35) {
            return false;
        }
        for (MapLocation towerLoc: RobotPlayer.enemyTowers) {
            if (rc.getLocation().add(dir).distanceSquaredTo(towerLoc) <= 25) {
                return false;
            }
        }

        return true;
    }

    public static boolean doMove(RobotController rc, Direction dir) throws GameActionException {
        if (exitCase(rc)) return false;

        while (!rc.canMove(dir) || !rc.isCoreReady()) {
            rc.yield();
        }
        rc.move(dir);
        return true;
    }

    public static boolean exitCase(RobotController rc) throws GameActionException{
        RobotType type = rc.getType();
        switch (type) {
            case DRONE:
                return Util.harass(rc, DRONE.targets);
            case LAUNCHER:
                return LAUNCHER.tryLaunch(rc);
        }

        return false;
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

        return mLine;
    }
}