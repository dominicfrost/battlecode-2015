package sprintbot;

import battlecode.common.*;

public class MISSLE {
    public static boolean first = true;
    public static MapLocation toAttack = null;

    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();
        if (first) {
            RobotInfo[] enemiesInRange = rc.senseNearbyRobots(24, RobotPlayer.enemyTeam);
            double max_hit_count = Integer.MIN_VALUE;

            for (RobotInfo enemy : enemiesInRange) {
                if (myLocation.distanceSquaredTo(enemy.location) > 6) {
                    continue;
                }

                double hit_count = 0;
                for (RobotInfo otherEnemy : enemiesInRange) {
                    if (otherEnemy.location.distanceSquaredTo(enemy.location) < 1.5) {
                        hit_count++;
                    }
                }
                if (hit_count > max_hit_count) {
                    hit_count = max_hit_count;
                    toAttack = enemy.location;
                }
            }

            if (toAttack == null) {
                toAttack = myLocation.add(Direction.NORTH, 5);
            }

            first = false;
        }

        if (myLocation.equals(toAttack)) {
            rc.explode();
        }else {
            rc.move(myLocation.directionTo(toAttack));
        }
    }
}