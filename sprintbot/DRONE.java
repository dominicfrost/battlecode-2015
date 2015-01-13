package sprintbot;

import battlecode.common.*;

public class DRONE {
	public static RobotController rc;
    public static RobotType[] targets = {RobotType.BEAVER, RobotType.MINER,
            							 RobotType.SOLDIER, RobotType.BASHER};
    public static MapLocation pointOfInterest = null;

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;
		if (pointOfInterest == null || rc.getLocation().distanceSquaredTo(pointOfInterest) < 9){
			pointOfInterest = Util.getNewPointOfInterest(rc);
		}
		
		Boolean hunting = false;
		Boolean supplying = false;
		double supplyLevel = rc.getSupplyLevel();

		if (rc.isCoreReady()) {
            Pathing.straitBuggin(rc, RobotPlayer.enemyHq);
            if (!Pathing.straitBuggin(rc, RobotPlayer.enemyHq)) {
                while (!rc.isCoreReady()) rc.yield();
                tryMoveAvoidES(rc, Util.intToDirection(RobotPlayer.rand.nextInt(7)));
            }
		}
	}

    public static void tryMoveAvoidES(RobotController rc, Direction d) throws GameActionException {

        MapLocation myLocation = rc.getLocation();
        MapLocation toMove = null;
        MapLocation trying;
        Direction tryDir;

        for (int i = 0; i < 4; i++) {
            tryDir = Util.intToDirection(i);
            trying = myLocation.add(Util.intToDirection(i));
            for (MapLocation enemyTower: RobotPlayer.enemyTowers) {
                if (enemyTower.distanceSquaredTo(myLocation) > 24) {
                    if(RobotPlayer.enemyHq.distanceSquaredTo(myLocation) > 34) {
                        if(rc.canMove(tryDir)) {
                            rc.move(tryDir);
                        }
                        return;
                    }
                }
            }


            tryDir = Util.intToDirection(((-1*i) + 8) % 8);
            trying = myLocation.add(Util.intToDirection(i));
            for (MapLocation enemyTower: RobotPlayer.enemyTowers) {
                if (enemyTower.distanceSquaredTo(myLocation) > 24) {
                    if(RobotPlayer.enemyHq.distanceSquaredTo(myLocation) > 34) {
                        if(rc.canMove(tryDir)) {
                            rc.move(tryDir);
                        }
                        return;
                    }
                }
            }
        }

    }
}