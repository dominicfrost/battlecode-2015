package davebot;

import battlecode.common.*;

public class HQ {
    public static void execute(RobotController rc, Team myTeam, Team enemyTeam) throws GameActionException {
        RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
        int[] allyTypeCount = countTypes(myRobots);
        if (rc.isCoreReady()) {
            if (!attack(rc, enemyRobots)) {
                spawnBeaver(rc, myRobots);
            }
        }
    }

    /*
     * attacks the enemy that is the closeset
     * if two are equal distance away it attacks the weaker one
     */
    public static void attack(RobotController rc, RobotInfo[] enemyRobots) {
        if (rc.isWeaponReady() < 1) {
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
        if (getTeamOre() >= 100 && allyTypeCount[RobotType.BEAVER.ordinal()] < 5) {
            utils.smartSpawn(RobotType.BEAVER);
        }
    }

    /*
     * counts how many of each type of robot there are on the given team
     */
    public static int[] countTypes(RobotInfo[] myRobots) throws GameActionException {
        int[] typeCount = new int[21]
        for (RobotInfo r : myRobots) {
            switch (r.type) {
                case AEROSPACELAB:
                    typeCount[AEROSPACELAB.ordinal()]++;
                    break;
                case BARRACKS:
                    typeCount[BARRACKS.ordinal()]++;
                    break;
                case BASHER:
                    typeCount[BASHER.ordinal()]++;
                    break;
                case BEAVER:
                    typeCount[BEAVER.ordinal()]++;
                    break;
                case COMMANDER:
                    typeCount[COMMANDER.ordinal()]++;
                    break;
                case COMPUTER:
                    typeCount[COMPUTER.ordinal()]++
                    break;
                case DRONE:
                    typeCount[DRONE.ordinal()]++
                    break;
                case HANDWASHSTATION:
                    typeCount[HANDWASHSTATION.ordinal()]++;
                    break;
                case HELIPAD:
                    typeCount[HELIPAD.ordinal()]++;
                    break;
                case HQ:
                    typeCount[HQ.ordinal()]++;
                    break;
                case LAUNCHER:
                    typeCount[LAUNCHER.ordinal()]++;
                    break;
                case MINER:
                    typeCount[MINER.ordinal()]++;
                    break;
                case MINERFACTORY:
                    typeCount[MINERFACTORY.ordinal()]++;
                    break;
                case MISSILE:
                    typeCount[MISSILE.ordinal()]++;
                    break;
                case SOLDIER:
                    typeCount[SOLDIER.ordinal()]++;
                    break;
                case SUPPLYDEPOT:
                    typeCount[SUPPLYDEPOT.ordinal()]++;
                    break;
                case TANK:
                    typeCount[TANK.ordinal()]++;
                    break;
                case TANKFACTORY:
                    typeCount[TANKFACTORY.ordinal()]++;
                    break;
                case TECHNOLOGYINSTITUTE:
                    typeCount[TECHNOLOGYINSTITUTE.ordinal()]++;
                    break;
                case TOWER:
                    typeCount[TOWER.ordinal()]++;
                    break;
                case TRAININGFIELD:
                    typeCount[TRAININGFIELD.ordinal()]++;
                    break;
            }
        }

        for (int i = 0; i < typeCount.length; i++) {
            rc.brodcast(MyConstants.ROBOT_COUNT_OFFSET + i, typeCount[i]);
        }

        return typeCount;
    }
}