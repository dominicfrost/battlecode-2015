package davebot;

import battlecode.common.*;

public class HQ {
    public static RobotController rc;
    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        RobotInfo[] myRobots = rc.senseNearbyRobots(999999, RobotPlayer.myTeam);
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
        int[] allyTypeCount = countTypes(myRobots);
        if (rc.isCoreReady()) {
            if (!attack(rc, enemyRobots)) {
                //spawnBeaver(rc, myRobots);
            }
        }
    }

    /*
     * attacks the enemy that is the closeset
     * if two are equal distance away it attacks the weaker one
     */
    public static boolean attack(RobotController rc, RobotInfo[] enemyRobots) {
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

    /*
     * Spawns some beaver
     */
    public static void spawnBeaver(RobotController rc, int[] allyTypeCount) {
        if (rc.getTeamOre() >= 100 && allyTypeCount[RobotType.BEAVER.ordinal()] < 5) {
            Utils.smartSpawn(rc, RobotType.BEAVER);
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
            rc.brodcast(MyConstants.ROBOT_COUNT_OFFSET + i, typeCount[i]);
        }

        return typeCount;
    }
}