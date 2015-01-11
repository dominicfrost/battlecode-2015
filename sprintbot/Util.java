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

        RobotType toBuild = getRobotTypeToSpawn(rc);
        if (Arrays.asList(canBuild).indexOf(toBuild) > -1) {
            double myOre = rc.getTeamOre();
            if (myOre >= toBuild.oreCost) {
                //System.out.println("Building " + toBuild.toString());
                tryBuild(rc, d, toBuild);
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

        RobotType toSpawn = getRobotTypeToSpawn(rc);
        if (Arrays.asList(canSpawn).indexOf(toSpawn) > -1) {
            double myOre = rc.getTeamOre();
            if (myOre >= toSpawn.oreCost) {
                //System.out.println("Spawning " + toSpawn.toString());
                trySpawn(rc, d, toSpawn);
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
}