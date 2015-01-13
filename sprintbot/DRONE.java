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
			// first defend any towers
			for (int i = 0; i < 6; i++) {
				if (rc.readBroadcast(MyConstants.TOWER_UNDER_DISTRESS + i) == 1) {
					int x = rc.readBroadcast(MyConstants.TOWER_UNDER_DISTRESS_LOCATION + (i * 2));
					int y = rc.readBroadcast(MyConstants.TOWER_UNDER_DISTRESS_LOCATION + (i * 2) + 1);
					pointOfInterest = new MapLocation(x, y);
				}
			}

			if (supplyLevel < 300) {
				Pathing.straitBuggin(rc, RobotPlayer.myHq);
			} else {
                Pathing.straitBuggin(rc, pointOfInterest);
			}
		}
	}
}