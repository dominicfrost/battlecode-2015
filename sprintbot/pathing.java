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
            if (myLocation.equals(goal)) {
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
            if (rc.canMove(nextLocationDir)) {
                doMove(rc, nextLocationDir);
                continue;
            } else {
                //we could not move along the mLine, bug around the wall
                //until we reach one of our loop conditons are met
                MapLocation startingPoint = myLocation;
                putHandOnWall(rc, nextLocationDir, mLine);
            }
        }
    }

    public static void putHandOnWall(RobotController rc, Direction startDir, ArrayList<MapLocation> mLine) throws GameActionException {
        Direction rightDir = startDir;
        Direction leftDir = startDir;

        while (true) {
            rightDir = rightDir.rotateRight();
            leftDir = leftDir.rotateLeft();

            if (rc.canMove(rightDir)) {
                doMove(rc, rightDir);
                followWall(rc, rightDir, true, mLine);
                return;
            }

            if (rc.canMove(leftDir)) {
                doMove(rc, leftDir);
                followWall(rc, leftDir, false, mLine);
                return;
            }
        }
    }

    public static void followWall(RobotController rc, Direction myDir, boolean movedClockwise, ArrayList<MapLocation> mLine) throws GameActionException {
        while (true) {
            if (mLine.contains(rc.getLocation())) {
                return;
            }

            //if i can go in towards the mline do it
            Direction backInwards = rotateInDir(myDir, movedClockwise);
            if (rc.canMove(backInwards)) {
                if (mLine.contains(rc.getLocation().add(myDir))) {
                    doMove(rc, myDir);
                    return;
                }
                doMove(rc, backInwards);
                myDir = rotateInDir(backInwards, movedClockwise);
                continue;
            }

            //if i can go strait do it
            if (rc.canMove(myDir)) {
                doMove(rc, myDir);
                continue;
            }

            //rotate outwards until you can move
            int turns = 0;
            while (true) {
                turns++;
                myDir = rotateInDir(myDir, !movedClockwise);
                if (rc.canMove(myDir)) {
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

    public static void doMove(RobotController rc, Direction dir) throws GameActionException {
        while (!rc.canMove(dir) || !rc.isCoreReady()) {
            rc.yield();
        }
        rc.move(dir);
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

    // ----- We need to figure out if this is even useful, we can't sense the whole map this year... ----

    // returns 2d aray of integers representing directions that lead to a given goal following the shortest possible path
//    public static int[][] assessMapWithDirection(RobotController rc, MapLocation goal, int[][] map) throws GameActionException {
//        ArrayDeque<MapLocation> queue = new ArrayDeque<MapLocation>();
//        ArrayDeque<MapLocation> enemyQueue = new ArrayDeque<MapLocation>();
//        int mapWidth = map.length;
//        int mapHeight = map[0].length;
//        int currentX;
//        int currentY;
//
//        MapLocation currentLocation;
//        map[goal.x][goal.y] = 9;
//
//        // cant move through HQ's, set their tiles to void
//        MapLocation temp = rc.senseHQLocation();
//        map[temp.x][temp.y] = 9;
//        MapLocation enemyHq = rc.senseEnemyHQLocation();
//        map[enemyHq.x][enemyHq.y] = 9;
//
//        // we dont want to path within range of the enemy hq, set all tiles within range to void.
//        enemyQueue.add(enemyHq);
//        while(!enemyQueue.isEmpty()){
//            currentLocation = enemyQueue.poll();
//            currentX = currentLocation.x;
//            currentY = currentLocation.y;
//
//            for(Direction dir : allDirections){
//                temp = currentLocation.add(dir);
//                if(temp.x != -1 && temp.y != -1 && temp.x < mapWidth && temp.y < mapHeight){
//                    if(map[temp.x][temp.y] != 9 && enemyHq.distanceSquaredTo(temp) < 35){
//                        map[temp.x][temp.y] = 9;
//                        enemyQueue.add(temp);
//                    }
//                }
//            }
//        }
//
//        // we dont want to path within range of any enemy towers. set all tiles within range to void.
//        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
//        for (MapLocation tower: enemyTowers) {
//            enemyQueue.add(tower);
//            while (!enemyQueue.isEmpty()) {
//                currentLocation = enemyQueue.poll();
//                currentX = currentLocation.x;
//                currentY = currentLocation.y;
//
//                for (Direction dir : allDirections) {
//                    temp = currentLocation.add(dir);
//                    if (temp.x != -1 && temp.y != -1 && temp.x < mapWidth && temp.y < mapHeight) {
//                        if (map[temp.x][temp.y] != 9 && enemyHq.distanceSquaredTo(temp) < 24) {
//                            map[temp.x][temp.y] = 9;
//                            enemyQueue.add(temp);
//                        }
//                    }
//                }
//            }
//        }
//
//        // we want map locations in the queue
//        queue.add(goal);
//        while(!queue.isEmpty()) {
//
//            currentLocation = queue.poll();
//            currentX = currentLocation.x;
//            currentY = currentLocation.y;
//
//            // check the northern square
//            if(currentY != 0 && map[currentX][currentY-1] == 0 && rc.senseTerrainTile(new MapLocation(currentX, currentY-1)).ordinal() != 2) {
//                map[currentX][currentY-1] = 5;
//                queue.add(new MapLocation(currentX, currentY-1));
//
//            }
//            // check the north eastern square
//            if(currentY != 0 && currentX != mapWidth-1 && map[currentX+1][currentY-1] == 0 && rc.senseTerrainTile(new MapLocation(currentX+1, currentY-1)).ordinal() != 2) {
//                map[currentX+1][currentY-1] = 6;
//                queue.add(new MapLocation(currentX+1, currentY-1));
//
//            }
//            // check the eastern square
//            if(currentX != mapWidth-1 && map[currentX+1][currentY] == 0 && rc.senseTerrainTile(new MapLocation(currentX+1, currentY)).ordinal() != 2) {
//                map[currentX+1][currentY] = 7;
//                queue.add(new MapLocation(currentX+1, currentY));
//            }
//            // check the south eastern square
//            if(currentX != mapWidth-1 && currentY != mapHeight-1 && map[currentX+1][currentY+1] == 0 && rc.senseTerrainTile(new MapLocation(currentX+1, currentY+1)).ordinal() != 2) {
//                map[currentX+1][currentY+1] = 8;
//                queue.add(new MapLocation(currentX+1, currentY+1));
//            }
//            // check the southern square
//            if(currentY != mapHeight-1 && map[currentX][currentY+1] == 0 && rc.senseTerrainTile(new MapLocation(currentX, currentY+1)).ordinal() != 2) {
//                map[currentX][currentY+1] = 1;
//                queue.add(new MapLocation(currentX, currentY+1));
//            }
//            // check the south western square
//            if(currentX != 0 && currentY != mapHeight-1 && map[currentX-1][currentY+1] == 0 && rc.senseTerrainTile(new MapLocation(currentX-1, currentY+1)).ordinal() != 2) {
//                map[currentX-1][currentY+1] = 2;
//                queue.add(new MapLocation(currentX-1, currentY+1));
//            }
//            // check the western square
//            if(currentX != 0 && map[currentX-1][currentY] == 0 && rc.senseTerrainTile(new MapLocation(currentX-1, currentY)).ordinal() != 2) {
//                map[currentX-1][currentY] = 3;
//                queue.add(new MapLocation(currentX-1, currentY));
//            }
//            // check the north western square
//            if(currentX != 0 && currentY != 0 && map[currentX-1][currentY-1] == 0 && rc.senseTerrainTile(new MapLocation(currentX-1, currentY-1)).ordinal() != 2) {
//                map[currentX-1][currentY-1] = 4;
//                queue.add(new MapLocation(currentX-1, currentY-1));
//            }
//
//
//        }
//        return map;
//    }
}