package sprintbot;

import battlecode.common.*;

public class DRONE {
	public static RobotController rc;
    public static RobotType[] targets = {RobotType.BEAVER, RobotType.MINER,
            							 RobotType.SOLDIER, RobotType.BASHER};
    public static MapLocation pointOfInterest = RobotPlayer.myHq;

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;
		if (rc.getLocation().distanceSquaredTo(pointOfInterest) <= 4){
			pointOfInterest = Util.getNewPointOfInterest(rc);
		}
		
		Boolean hunting = false;
		Boolean supplying = false;
		double supplyLevel = rc.getSupplyLevel();

		if (rc.isCoreReady()) {
			if (supplyLevel < 300) {
				Pathing.straitBuggin(rc, RobotPlayer.myHq);
			} else {
                Pathing.straitBuggin(rc, pointOfInterest);
			}
		}
	}
}