package sprintbot;

import battlecode.common.*;

public class DRONE {
	public static RobotController rc;
    public static RobotType[] targets = {RobotType.BEAVER, RobotType.MINER,
            RobotType.SOLDIER, RobotType.BASHER};
    MapLocation pointOfInterest = null;

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;

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