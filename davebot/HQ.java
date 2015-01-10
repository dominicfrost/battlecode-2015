package davebot;

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

    public static void broadcastNextSpawnType(int[] allyTypeCount) throws GameActionException{
        if (allyTypeCount[RobotType.BEAVER.ordinal()] < 5) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.BEAVER.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.MINERFACTORY.ordinal()] < 3) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.MINERFACTORY.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.MINER.ordinal()] < 10) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.MINER.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.BARRACKS.ordinal()] < 2) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.BARRACKS.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.SOLDIER.ordinal()] < 10) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.SOLDIER.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.MINER.ordinal()] < 20) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.MINER.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.TANKFACTORY.ordinal()] < 1) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.TANKFACTORY.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.TANK.ordinal()] < 5) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.TANK.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.HELIPAD.ordinal()] < 2) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.HELIPAD.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.DRONE.ordinal()] < 5) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.DRONE.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.TANK.ordinal()] < 10) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.TANK.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.DRONE.ordinal()] < 10) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.DRONE.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.MINERFACTORY.ordinal()] < 4) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.MINERFACTORY.ordinal());
            return;
        }
        if (allyTypeCount[RobotType.MINER.ordinal()] < 30) {
            rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.MINER.ordinal());
            return;
        }
        rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET, RobotType.TANK.ordinal());
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

        rc.broadcast(MyConstants.ATTACK_LOCATION, Util.mapLocToInt(closest));
    }
}