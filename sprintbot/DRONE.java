package sprintbot;

import battlecode.common.*;

public class DRONE {
	public static RobotController rc;

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;

		RobotType[] targets = {RobotType.BEAVER, RobotType.MINER, 
				RobotType.SOLDIER, RobotType.BASHER};
		Boolean hunting = false;
		Boolean supplying = false;
		double supplyLevel = rc.getSupplyLevel();

		if (rc.isCoreReady()) {
			if (supplyLevel < 300) {
				Util.moveToLocation(rc, RobotPlayer.myHq);
			} else {
				Util.harass(rc, targets);
			}
		}
	}
}