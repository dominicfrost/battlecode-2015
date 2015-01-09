package davebot;

import battlecode.common.*;

public class TOWER {
    public static void execute(RobotController rc) throws GameActionException {
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
    }

    /*
     * attacks the enemy that is the closeset
     * if two are equal distance away it attacks the weaker one
     */
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