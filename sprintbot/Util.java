package sprintbot;

import battlecode.common.*;
import java.util.*;

public class Util {
    public static Random rand = new Random();
    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};


    /*
     * spawns the current spawn type if possible
     */
    public static boolean buildWithPrecedence(RobotController rc, Direction d, RobotType[] canBuild) throws GameActionException{
        if (!rc.isCoreReady()) {
            return false;
        }

        int numToBuild;
        double myOre = rc.getTeamOre();

        for (RobotType type: canBuild) {
            numToBuild = rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal());
            if (numToBuild > 0 && myOre >= type.oreCost) {
                tryBuild(rc, d, type);
                numToBuild--;
                rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToBuild);
                return true;
            }
        }
        return false;
    }

    /*
     * spawns the current spawn type if possible
     */
    public static boolean spawnWithPrecedence(RobotController rc, Direction d, RobotType[] canSpawn) throws GameActionException{
        if (!rc.isCoreReady()) {
            return false;
        }

        int numToSpawn;
        double myOre = rc.getTeamOre();

        for (RobotType type: canSpawn) {
            numToSpawn = rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal());
            if (numToSpawn > 0 && myOre >= type.oreCost) {
                trySpawn(rc, d, type);
                numToSpawn--;
                rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToSpawn);
                return true;
            }
        }
        return false;
    }

    public static void generalAttack(RobotController rc) throws GameActionException{
        MapLocation goal = new MapLocation(rc.readBroadcast(MyConstants.ATTACK_LOCATION), rc.readBroadcast(MyConstants.ATTACK_LOCATION + 1));
        System.out.println("GOAL: " + goal.toString());
        if (rc.isCoreReady()) {
            if (rc.canAttackLocation(goal)) {
                System.out.println("can attack goal!: " + goal.toString());
                rc.attackLocation(goal);
                return;
            }
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
            if (!attack(rc, enemyRobots)) {
                if (rc.getHealth() < 40) {
                    System.out.println("my health or supply is too low, going to hq");
                    tryMove(rc, rc.getLocation().directionTo(rc.senseHQLocation()));
                    return;
                }
                System.out.println("moving to goal!: " + goal.toString());
                tryMove(rc, rc.getLocation().directionTo(goal));
            }
        }
    }

    public static int mapLocToInt(MapLocation m){
        return (m.x*10000 + m.y);
    }

    public static MapLocation intToMapLoc(int i){
        return new MapLocation(i/10000,i%10000);
    }

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

    public static boolean saftToMoveTo(RobotController rc, MapLocation myLocation,  MapLocation[] enemyTowers, MapLocation enemyHq) {
        if (myLocation.distancedSquaredTo(enemyHq) <= 35) {
            return false;
        }

        for (MapLocation tower: enemyTowers) {
            if (myLocation.distancedSquaredTo(tower) <= 24) {
                return false;
            }
        }

        return true;
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
    
    //use this to return the map location in a specific direction from your robot
    public static MapLocation getAdjacentLocation(RobotController rc, Direction dir, MapLocation loc){
    	return (loc.add(dir));
    }
    
    //mine while preferring areas w/ greater ore, but stay within 1/2 distance to enemy hq
    public static void SmartMine(RobotController rc) throws GameActionException {
        if (!rc.isCoreReady()) {
            return;
        }

        MapLocation myLocation = rc.getLocation();
        double oreCount = rc.senseOre(myLocation);
        
        double ore_N = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH, myLocation));
        double ore_NE = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH_EAST, myLocation));
        double ore_E = rc.senseOre(getAdjacentLocation(rc, Direction.EAST, myLocation));
        double ore_SE = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH_EAST, myLocation));
        double ore_S = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH, myLocation));
        double ore_SW = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH_WEST, myLocation));
        double ore_W = rc.senseOre(getAdjacentLocation(rc, Direction.WEST, myLocation));
        double ore_NW = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH_WEST, myLocation));
        
        if(oreCount < ore_N)
        	tryMove(rc, Direction.NORTH);
        else if(oreCount < ore_NE)
        	tryMove(rc, Direction.NORTH_EAST);
        else if(oreCount < ore_E)
        	tryMove(rc, Direction.EAST);
        else if(oreCount < ore_SE)
        	tryMove(rc, Direction.SOUTH_EAST);
        else if(oreCount < ore_S)
        	tryMove(rc, Direction.SOUTH);
        else if(oreCount < ore_SW)
        	tryMove(rc, Direction.SOUTH_WEST);
        else if(oreCount < ore_W)
        	tryMove(rc, Direction.WEST);
        else if(oreCount < ore_NW)
        	tryMove(rc, Direction.NORTH_WEST);
        else if(oreCount > 0) {
            rc.mine();
        }else {
            int fate = rand.nextInt(8);
            Util.tryMove(rc, Util.intToDirection(fate));
        }
    }

    // mine like a dummy
    public static void mine(RobotController rc) throws GameActionException {
        if (!rc.isCoreReady()) {
            return;
        }

        MapLocation myLocation = rc.getLocation();
        double oreCount = rc.senseOre(myLocation);
        if (oreCount > 0) {
            rc.mine();
        }else {
            int fate = rand.nextInt(8);
            Util.tryMove(rc, Util.intToDirection(fate));
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

    public static RobotType getRobotTypeToSpawn(RobotController rc) throws GameActionException{
        RobotType[] types = RobotType.values();
        return types[rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET)];
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

    public static void putHandOnWall(RobotController rc, Direction startDir, ArrayList<MapLocation> mLine) throws GameActionException  {
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

    public static void followWall(RobotController rc, Direction myDir, boolean movedClockwise, ArrayList<MapLocation> mLine) throws GameActionException  {
        while(true) {
            if (mLine.contains(rc.getLocation())) {
                return;
            }

            //if i can go in towards the mline do it
            Direction backInwards = rotateInDir(myDir, movedClockwise);
            if (rc.canMove(backInwards)) {
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

    public static void doMove(RobotController rc, Direction dir) throws GameActionException{
//        while (true) {
            while (!rc.canMove(dir) || !rc.isCoreReady()) {
                rc.yield();
            }
//            RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
//            if (!attack(rc, enemyRobots)) {
//                rc.move(dir);
//                rc.yield();
//                return;
//            }
//            rc.yield();
            rc.move(dir);
//        }
    }

    public static boolean attack(RobotController rc, RobotInfo[] enemyRobots) throws GameActionException{
        if (rc.isWeaponReady()) {
            MapLocation myLocation = rc.getLocation();
            RobotInfo toAttack = enemyRobots[0];
            int closest = Integer.MAX_VALUE;

            for (RobotInfo enemy : enemyRobots) {
                int distanceToEnemy = myLocation.distanceSquaredTo(enemy.location);
                if (distanceToEnemy < closest) {
                    closest = distanceToEnemy;
                    toAttack = enemy;
                } else if (distanceToEnemy == closest) {
                    if (enemy.health < toAttack.health) {
                        toAttack = enemy;
                    }
                }
            }

            if (rc.canAttackLocation(toAttack.location)) {
                rc.attackLocation(toAttack.location);
                return true;
            }
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