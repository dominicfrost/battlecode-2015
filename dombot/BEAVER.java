package dombot;

import battlecode.common.*;

public class BEAVER {
    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc, RobotPlayer.myRange, RobotPlayer.enemyTeam);
        }

        if (rc.isCoreReady()) {
            Direction targetDirection;
            int r = RobotPlayer.rand.nextInt(100);
            double ore = rc.getTeamOre();

            RobotInfo[] enemyRobots = rc.senseNearbyRobots(RobotPlayer.myRange, RobotPlayer.enemyTeam);
            if (enemyRobots.length > 0) {
                RobotInfo closestRobot = enemyRobots[0];
                int closestDistance = Integer.MAX_VALUE;
                for (RobotInfo robot : enemyRobots) {
                    int newDistance = robot.location.distanceSquaredTo(myLocation);
                    if (newDistance < closestDistance) {
                        closestDistance = newDistance;
                        closestRobot = robot;
                    }
                }
            } else if (rc.checkDependencyProgress(RobotType.BARRACKS) != DependencyProgress.INPROGRESS &&
                    ore >= 300 && rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.BARRACKS.ordinal()) < 1) {
                Util.tryBuild(rc, Direction.NORTH, RobotType.BARRACKS);
            } else if (rc.checkDependencyProgress(RobotType.TECHNOLOGYINSTITUTE) != DependencyProgress.INPROGRESS &&
                    ore >= 200 && rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TECHNOLOGYINSTITUTE.ordinal()) < 1) {
                Util.tryBuild(rc, Direction.NORTH, RobotType.TECHNOLOGYINSTITUTE);
            } else if (rc.hasBuildRequirements(RobotType.TRAININGFIELD) &&
                    rc.checkDependencyProgress(RobotType.TRAININGFIELD) != DependencyProgress.INPROGRESS &&
                    ore >= 200 && rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TRAININGFIELD.ordinal()) < 1) {
                Util.tryBuild(rc, Direction.NORTH, RobotType.TRAININGFIELD);
            } else if (rc.hasBuildRequirements(RobotType.TANKFACTORY) &&
                    rc.checkDependencyProgress(RobotType.TANKFACTORY) != DependencyProgress.INPROGRESS &&
                    ore >= 500 && rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TANKFACTORY.ordinal()) < 1) {
                Util.tryBuild(rc, Direction.NORTH, RobotType.TANKFACTORY);
            } else if (r > 90 && rc.checkDependencyProgress(RobotType.SUPPLYDEPOT) != DependencyProgress.INPROGRESS &&
                    ore >= 100 && rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.SUPPLYDEPOT.ordinal()) < 10 &&
                    rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TANKFACTORY.ordinal()) >= 1) {
                Util.tryBuild(rc, Direction.NORTH, RobotType.SUPPLYDEPOT);
            } else if (r > 85) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.myHQLocation));
            } else if (r > 80) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.myHQLocation).rotateRight().rotateRight());
            } else if (r > 75) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.myHQLocation).rotateLeft().rotateLeft());
            } else if (r > 70) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.myHQLocation).opposite());
            } else {
                rc.mine();
            }
        }
    }
}