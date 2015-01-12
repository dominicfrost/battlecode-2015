package sprintbot;

import battlecode.common.*;

public class DRONE {
	public static RobotController rc;
    public static RobotType[] targets = {RobotType.BEAVER, RobotType.MINER,
            RobotType.SOLDIER, RobotType.BASHER};

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;
		int executeStartRound = Clock.getRoundNum();

		Boolean hunting = false;
		Boolean supplying = false;
		double supplyLevel = rc.getSupplyLevel();

		if (rc.isCoreReady()){
			Util.harass(rc, targets);
		}

		if (executeStartRound == Clock.getRoundNum()) {
			rc.yield();
		}
	}
}