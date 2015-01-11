package sprintbot;

import battlecode.common.*;

public class HQ {
    public static RobotController rc;
    public static RobotType[] canSpawn = {RobotType.BEAVER};
    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        int executeStartRound = Clock.getRoundNum();
        RobotInfo[] myRobots = rc.senseNearbyRobots(999999, RobotPlayer.myTeam);
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);

        assessTheSituation();
        if (rc.isCoreReady()) {
            if (!Util.attack(rc, enemyRobots)) {
                Util.spawnWithPrecedence(rc, Direction.NORTH, canSpawn);
            }
        }
        if (executeStartRound == Clock.getRoundNum()) {
            rc.yield();
        }
    }

    /*
     * counts how many of each type of robot there are on the given team
     */
    public static int[] countTypes(RobotInfo[] myRobots) throws GameActionException {
        int[] typeCount = new int[21];
        for (RobotInfo r : myRobots) {
            RobotType rt = r.type;

            if (r.supplyLevel < 2000 && RobotPlayer.myHq.distanceSquaredTo(r.location) <= 15) {
                rc.transferSupplies((int) Math.min(2000 - r.supplyLevel, rc.getSupplyLevel()), r.location);
            }

            switch (rt) {
                case AEROSPACELAB:
                    typeCount[RobotType.AEROSPACELAB.ordinal()]++;
                    break;
                case BARRACKS:
                    typeCount[RobotType.BARRACKS.ordinal()]++;
                    break;
                case BASHER:
                    typeCount[RobotType.BASHER.ordinal()]++;
                    break;
                case BEAVER:
                    typeCount[RobotType.BEAVER.ordinal()]++;
                    break;
                case COMMANDER:
                    typeCount[RobotType.COMMANDER.ordinal()]++;
                    break;
                case COMPUTER:
                    typeCount[RobotType.COMPUTER.ordinal()]++;
                    break;
                case DRONE:
                    typeCount[RobotType.DRONE.ordinal()]++;
                    break;
                case HANDWASHSTATION:
                    typeCount[RobotType.HANDWASHSTATION.ordinal()]++;
                    break;
                case HELIPAD:
                    typeCount[RobotType.HELIPAD.ordinal()]++;
                    break;
                case HQ:
                    typeCount[RobotType.HQ.ordinal()]++;
                    break;
                case LAUNCHER:
                    typeCount[RobotType.LAUNCHER.ordinal()]++;
                    break;
                case MINER:
                    typeCount[RobotType.MINER.ordinal()]++;
                    break;
                case MINERFACTORY:
                    typeCount[RobotType.MINERFACTORY.ordinal()]++;
                    break;
                case MISSILE:
                    typeCount[RobotType.MISSILE.ordinal()]++;
                    break;
                case SOLDIER:
                    typeCount[RobotType.SOLDIER.ordinal()]++;
                    break;
                case SUPPLYDEPOT:
                    typeCount[RobotType.SUPPLYDEPOT.ordinal()]++;
                    break;
                case TANK:
                    typeCount[RobotType.TANK.ordinal()]++;
                    break;
                case TANKFACTORY:
                    typeCount[RobotType.TANKFACTORY.ordinal()]++;
                    break;
                case TECHNOLOGYINSTITUTE:
                    typeCount[RobotType.TECHNOLOGYINSTITUTE.ordinal()]++;
                    break;
                case TOWER:
                    typeCount[RobotType.TOWER.ordinal()]++;
                    break;
                case TRAININGFIELD:
                    typeCount[RobotType.TRAININGFIELD.ordinal()]++;
                    break;
            }
        }

        for (int i = 0; i < typeCount.length; i++) {
            rc.broadcast(MyConstants.ROBOT_COUNT_OFFSET + i, typeCount[i]);
        }

        return typeCount;
    }

    public static void assessTheSituation() throws GameActionException{
        RobotInfo[] myRobots = rc.senseNearbyRobots(999999, RobotPlayer.myTeam);
        int[] allyTypeCount = countTypes(myRobots);
        broadcastNextSpawnType(allyTypeCount);
        broadcastNextAttackLocation();
    }

    // this function broadcasts the number to spawn of a given type if we have less of that robot type than numDesired
    public static double spawningRule(int[] allyTypeCount, RobotType type, int numDesired, double oreRemaining) throws GameActionException {
        if (allyTypeCount[type.ordinal()] < numDesired) {
            int numToSpawn = numDesired - allyTypeCount[type.ordinal()];
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToSpawn);
            oreRemaining = oreRemaining - (type.oreCost * numToSpawn);
        }

        return oreRemaining;
    }

    //set the spawning precedence here
    public static void broadcastNextSpawnType(int[] allyTypeCount) throws GameActionException{
        double remainingOre = rc.getTeamOre();
        if (spawningRule(allyTypeCount, RobotType.BEAVER, 3, remainingOre) < 0) return;
    }


    public static void broadcastNextAttackLocation() throws GameActionException{
        int closest_dist = Integer.MAX_VALUE;
        int distance;
        MapLocation closest = RobotPlayer.enemyHq;
        MapLocation myLocation = rc.getLocation();

        for(int i = 0; i < RobotPlayer.enemyTowers.length; i++) {
            distance = myLocation.distanceSquaredTo(RobotPlayer.enemyTowers[i]);
            if (distance < closest_dist) {
                closest_dist = distance;
                closest = RobotPlayer.enemyTowers[i];
            }
        }

        rc.broadcast(MyConstants.ATTACK_LOCATION, closest.x);
        rc.broadcast(MyConstants.ATTACK_LOCATION + 1, closest.y);
    }
}