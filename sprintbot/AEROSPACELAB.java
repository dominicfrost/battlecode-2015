package sprintbot;

import battlecode.common.*;
import java.util.*;

public class AEROSPACELAB {

    public static int numTowers = 999;
    public static RobotType[] canSpawn = {RobotType.LAUNCHER};

    public static void execute(RobotController rc_in) throws GameActionException {
        RobotController rc = rc_in;
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        if (enemyTowers.length != numTowers) {
            int closestTower = 99999;
            MapLocation tower = new MapLocation(0,0);
            for (int i = 0; i < enemyTowers.length; i++) {
                int dist = RobotPlayer.myHq.distanceSquaredTo(enemyTowers[i]);
                if (dist < closestTower) {
                    closestTower = dist;
                    tower = enemyTowers[i];
                }
            }
            numTowers = enemyTowers.length;
            rc.broadcast(MyConstants.TARGET_TOWER_X, tower.x);
            rc.broadcast(MyConstants.TARGET_TOWER_Y, tower.y);
        }

        Util.spawnWithPrecedence(rc, Direction.NORTH, canSpawn);
    }
}