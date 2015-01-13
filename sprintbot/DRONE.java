package sprintbot;

import battlecode.common.*;

public class DRONE {
	public static RobotController rc;
    public static RobotType[] targets = {RobotType.BEAVER, RobotType.MINER,
            							 RobotType.SOLDIER, RobotType.BASHER};
    public static MapLocation pointOfInterest = RobotPlayer.myHq;
    public static MapLocation[] hqPoints = {RobotPlayer.enemyHq.add(Direction.NORTH, 6),
                                            RobotPlayer.enemyHq.add(Direction.NORTH_EAST, 6),
                                            RobotPlayer.enemyHq.add(Direction.NORTH_WEST, 6),
                                            RobotPlayer.enemyHq.add(Direction.SOUTH, 6),
                                            RobotPlayer.enemyHq.add(Direction.SOUTH_EAST, 6),
                                            RobotPlayer.enemyHq.add(Direction.SOUTH_WEST, 6),
                                            RobotPlayer.enemyHq.add(Direction.EAST, 6),
                                            RobotPlayer.enemyHq.add(Direction.WEST, 6)};

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;
//		if (rc.getLocation().distanceSquaredTo(pointOfInterest) <= 4){
//			pointOfInterest = Util.getNewPointOfInterest(rc);
//		}
        pointOfInterest = hqPoints[RobotPlayer.rand.nextInt(8)];
		
//		Boolean hunting = false;
//		Boolean supplying = false;
//		double supplyLevel = rc.getSupplyLevel();

		if (rc.isCoreReady()) {
            if (!Pathing.straitBuggin(rc, pointOfInterest)) {
                while (!rc.isCoreReady()) rc.yield();
                tryMoveAvoidES(rc, Util.intToDirection(RobotPlayer.rand.nextInt(7)));
            }
		}
	}

    public static void tryMoveAvoidES(RobotController rc, Direction d) throws GameActionException {

        MapLocation myLocation = rc.getLocation();

        Direction tryDir = Util.intToDirection(RobotPlayer.rand.nextInt(8));
        MapLocation trying = myLocation.add(tryDir);


        if(rc.canMove(tryDir) && Pathing.canMove(rc, tryDir)) {
            rc.move(tryDir);
        }
    }
}