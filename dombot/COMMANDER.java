package dombot;

import battlecode.common.*;

public class COMMANDER {
    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc, RobotPlayer.myRange, RobotPlayer.enemyTeam);
        }

        if (rc.isCoreReady()) {
            Direction targetDirection;
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(RobotPlayer.myRange, RobotPlayer.enemyTeam);

            if (rc.getSupplyLevel() < 1000) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.myHQLocation));
            } else if (rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TANK.ordinal()) < 5) {
                MapLocation rally = new MapLocation((RobotPlayer.myHQLocation.x + RobotPlayer.enemyHQLocation.x) /2, (RobotPlayer.myHQLocation.y + RobotPlayer.enemyHQLocation.y) / 2);
                rally = new MapLocation((RobotPlayer.myHQLocation.x + rally.x) / 2, (RobotPlayer.myHQLocation.y + rally.y) / 2);
                Util.tryMove(rc, myLocation.directionTo(rally));
            } else if (enemyRobots.length > 0) {
                RobotInfo closestRobot = enemyRobots[0];
                int closestDistance = Integer.MAX_VALUE;
                for (RobotInfo robot : enemyRobots) {
                    int newDistance = robot.location.distanceSquaredTo(myLocation);
                    if (newDistance < closestDistance) {
                        closestDistance = newDistance;
                        closestRobot = robot;
                    }
                }
                if (myLocation.distanceSquaredTo(closestRobot.location) > RobotPlayer.myRange) {
                    Util.tryMove(rc, myLocation.directionTo(closestRobot.location));
                }
            } else {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation));
            }
        }
    }
}