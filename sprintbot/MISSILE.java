package sprintbot;

import battlecode.common.*;

public class MISSILE {
    public static boolean first = false;
    public static MapLocation toAttack = null;

    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();
        MapLocation target = new MapLocation(rc.readBroadcast(MyConstants.TARGET_TOWER_X), rc.readBroadcast(MyConstants.TARGET_TOWER_Y));
        Direction gank = myLocation.directionTo(target);
        
        int gankenum = Util.directionToInt(gank);
        
        Direction gankUP = Util.intToDirection(gankenum + 1);
        Direction gankDOWN = Util.intToDirection(gankenum - 1);
        
        //IF ADJACENT TO DESTINATION, EXPLODE 
        //IF CAN MOVE IN DIRECTION OF ATTACK_LOCATION, DO THAT, OTHERWISE IF CAN MOVE THE DIAGONALS OF THAT, DO THAT, OTHERWISE YIELD
        
        if (myLocation.distanceSquaredTo(target) <= 1)
        	rc.explode();
        else if(rc.canMove(gank))
        	rc.move(gank);
        else if(rc.canMove(gankUP))
        	rc.move(gankUP);
        else if(rc.canMove(gankDOWN))
        	rc.move(gankDOWN);
        else
        	rc.yield();

        
        //        if (!first) {
//            RobotInfo[] enemiesInRange = rc.senseNearbyRobots(24, RobotPlayer.enemyTeam);
//            double max_hit_count = Integer.MIN_VALUE;
//
//            for (RobotInfo enemy : enemiesInRange) {
//                if (myLocation.distanceSquaredTo(enemy.location) > 6) {
//                    continue;
//                }
//
//                double hit_count = 0;
//                for (RobotInfo otherEnemy : enemiesInRange) {
//                    if (otherEnemy.location.distanceSquaredTo(enemy.location) < 1.5) {
//                        hit_count++;
//                    }
//                }
//                if (hit_count > max_hit_count) {
//                    hit_count = max_hit_count;
//                    toAttack = enemy.location;
//                }
//            }
//
//            if (toAttack == null) {
//                toAttack = myLocation.add(Direction.NORTH, 5);
//            }
//
//            first = true;
//        }
//
//        if (myLocation.equals(toAttack)) {
//            rc.explode();
//        }else {
//            rc.move(myLocation.directionTo(toAttack));
//        }
    }
}