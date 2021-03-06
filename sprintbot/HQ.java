package sprintbot;

import battlecode.common.*;

public class HQ {
	public static RobotController rc;
	public static RobotType[] canSpawn = {RobotType.BEAVER};
	public static Boolean first = true;

	public static void execute(RobotController rc_in) throws GameActionException {
		rc = rc_in;
		int executeStartRound = Clock.getRoundNum();
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, RobotPlayer.myTeam);
		RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);

		assessTheSituation();
		if(first){
			setPointsOfInterest(5);
			first = false;
		}

		if (rc.isCoreReady()) {
			if (!Util.attack(rc, enemyRobots)) {
				Util.spawnWithPrecedence(rc, Direction.NORTH, canSpawn);
			}
		}

		if (executeStartRound == Clock.getRoundNum()) {
			rc.yield();
		}
	}

	private static void setPointsOfInterest(int numPoints) throws GameActionException {
		rc.broadcast(MyConstants.NUM_POINTS_OF_INTEREST_OFFSET, numPoints);
		MapLocation myHq = RobotPlayer.myHq;
		MapLocation enemyHq = RobotPlayer.enemyHq;
		MapLocation[] pointsOfInterest = new MapLocation[numPoints];

		int distX = myHq.x - enemyHq.x;
		int distY = myHq.y - enemyHq.y;


		Direction xDir = null;
		Direction yDir = null;

		// determine corner points of interest
		if (distX > 0){
			xDir = Direction.WEST;
		} else {
			xDir = Direction.EAST;
		}	
		if (distY > 0){
			yDir = Direction.NORTH;
		} else {
			yDir = Direction.SOUTH;
		}

		// how close we want the point to be to enemy location relative to map size
		double pointProximity = (1.0 / 100.0);
		// Corner Locations
		double xCornerX = (myHq.add(xDir, Math.abs((distX))).x);
		double xCornerY = (myHq.add(yDir, (int)(Math.abs(distY) * pointProximity))).y;

		double yCornerX = (myHq.add(xDir, (int)(Math.abs(distX) * pointProximity))).x;
		double yCornerY = (myHq.add(yDir, Math.abs((distY))).y);

		MapLocation xCorner = new MapLocation((int)xCornerX, (int)xCornerY);
		MapLocation yCorner = new MapLocation((int)yCornerX, (int)yCornerY);

		// save and transmit locations
		pointsOfInterest[0] = xCorner;
		pointsOfInterest[1] = yCorner;

		int finalXLine = (int)Math.sqrt(yCorner.distanceSquaredTo(enemyHq));
		int finalYLine = (int)Math.sqrt(xCorner.distanceSquaredTo(enemyHq));
		
		double jumpSize = (1 / ((double)numPoints - 1));

		for (int i = 1; i < numPoints - 1; i++){
			double xCoordinate = yCorner.add(xDir, (int)((i * (jumpSize)) * finalXLine)).x;
			double yCoordinate = xCorner.add(yDir, (int)((1 - (i * (jumpSize))) * finalYLine)).y;

			MapLocation nextPoint = new MapLocation((int)xCoordinate, (int)yCoordinate); 
			pointsOfInterest[i + 1] = nextPoint;
		}

		for (int i = 0; i < numPoints; i++){
			rc.broadcast(MyConstants.POINTS_OF_INTEREST_OFFSET + (2 * i), pointsOfInterest[i].x);
			rc.broadcast(MyConstants.POINTS_OF_INTEREST_OFFSET + (2 * i + 1), pointsOfInterest[i].y);
		}
	}

	/*
	 * counts how many of each type of robot there are on the given team
	 */
	public static int[] countTypes(RobotInfo[] myRobots) throws GameActionException {
		int[] typeCount = new int[21];
		for (RobotInfo r : myRobots) {
			RobotType rt = r.type;

			// The max amount of supply we want any robot to acquire from the HQ
			int maxRobotSupply = 500;

			switch (rt) {
			case AEROSPACELAB:
				typeCount[RobotType.AEROSPACELAB.ordinal()]++;
				break;
			case BARRACKS:
				typeCount[RobotType.BARRACKS.ordinal()]++;
				break;
			case BASHER:
				typeCount[RobotType.BASHER.ordinal()]++;
				break;
			case BEAVER:
				typeCount[RobotType.BEAVER.ordinal()]++;
				break;
			case COMMANDER:
				typeCount[RobotType.COMMANDER.ordinal()]++;
				break;
			case COMPUTER:
				typeCount[RobotType.COMPUTER.ordinal()]++;
				break;
			case DRONE:
				// TODO: this may change when we figure out how much supply drones
				// are actually using and how much we have to offer
				maxRobotSupply = Math.min(4000, (2000 - Clock.getRoundNum()) * 5);
				typeCount[RobotType.DRONE.ordinal()]++;
			break;
			case HANDWASHSTATION:
				typeCount[RobotType.HANDWASHSTATION.ordinal()]++;
				break;
			case HELIPAD:
				typeCount[RobotType.HELIPAD.ordinal()]++;
				break;
			case HQ:
				typeCount[RobotType.HQ.ordinal()]++;
				break;
			case LAUNCHER:
				maxRobotSupply = Math.min(20000, (2000 - Clock.getRoundNum()) * 25);
				typeCount[RobotType.LAUNCHER.ordinal()]++;
				break;
			case MINER:
				typeCount[RobotType.MINER.ordinal()]++;
				break;
			case MINERFACTORY:
				typeCount[RobotType.MINERFACTORY.ordinal()]++;
				break;
			case MISSILE:
				typeCount[RobotType.MISSILE.ordinal()]++;
				break;
			case SOLDIER:
				typeCount[RobotType.SOLDIER.ordinal()]++;
				break;
			case SUPPLYDEPOT:
				typeCount[RobotType.SUPPLYDEPOT.ordinal()]++;
				break;
			case TANK:
				typeCount[RobotType.TANK.ordinal()]++;
				break;
			case TANKFACTORY:
				typeCount[RobotType.TANKFACTORY.ordinal()]++;
				break;
			case TECHNOLOGYINSTITUTE:
				typeCount[RobotType.TECHNOLOGYINSTITUTE.ordinal()]++;
				break;
			case TOWER:
				typeCount[RobotType.TOWER.ordinal()]++;
				break;
			case TRAININGFIELD:
				typeCount[RobotType.TRAININGFIELD.ordinal()]++;
				break;
			}

			if (r.supplyLevel < maxRobotSupply && RobotPlayer.myHq.distanceSquaredTo(r.location) <= 15) {
				rc.transferSupplies((int) Math.min(maxRobotSupply - r.supplyLevel, rc.getSupplyLevel()), r.location);
			}
		}
		typeCount[RobotType.HQ.ordinal()] = 1;

		for (int i = 0; i < typeCount.length; i++) {
			rc.broadcast(MyConstants.ROBOT_COUNT_OFFSET + i, typeCount[i]);
		}

		return typeCount;
	}

	public static void assessTheSituation() throws GameActionException{
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, RobotPlayer.myTeam);
		int[] allyTypeCount = countTypes(myRobots);
		broadcastNextSpawnType(allyTypeCount);
		broadcastNextAttackLocation();
	}

	// this function broadcasts the number to spawn of a given type if we have less of that robot type than numDesired
	/*
	 * allyTypeCount: an array containing how many of each robot type we have
	 * type: the RobotType we want to spawn/build
	 * numDesired: how many we want
	 * oreRemaining: how much ore we have left
	 * limit: our bottleneck on building that robot (e.g. if we want to spawn miners and only have 2 miner factories the limit is 2)
	 */
	public static double spawningRule(int[] allyTypeCount, RobotType type, int numDesired, double oreRemaining, int limit) throws GameActionException {
		if (allyTypeCount[type.ordinal()] < numDesired) {
			//we want to spawn numDesired - our robot count for the given type
			//however we could be limited by the number of builder structures we have
			// so we take the smaller of limit and numDesired - our robot count for the given type
			int numToSpawn = Math.min(numDesired - allyTypeCount[type.ordinal()], limit);
			rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToSpawn);
			oreRemaining = oreRemaining - (type.oreCost * numToSpawn);
			//System.out.println("Spawning " + numToSpawn + " " + type.toString() + " " + oreRemaining + " ore remaining");
			allyTypeCount[type.ordinal()] -= numToSpawn;
		}

		return oreRemaining;
	}

	//set the spawning precedence here
	public static void broadcastNextSpawnType(int[] allyTypeCount) throws GameActionException{
		double remainingOre = rc.getTeamOre();
		remainingOre = spawningRule(allyTypeCount, RobotType.BEAVER, 1, remainingOre, allyTypeCount[RobotType.HQ.ordinal()]);
		if (remainingOre < 0) return;
		remainingOre = spawningRule(allyTypeCount, RobotType.HELIPAD, 1, remainingOre, allyTypeCount[RobotType.BEAVER.ordinal()]);
		if (remainingOre < 0) return;
		remainingOre = spawningRule(allyTypeCount, RobotType.AEROSPACELAB, 1, remainingOre, allyTypeCount[RobotType.HELIPAD.ordinal()]);
        if (remainingOre < 0) return;
		        remainingOre = spawningRule(allyTypeCount, RobotType.LAUNCHER, 99, remainingOre, allyTypeCount[RobotType.AEROSPACELAB.ordinal()]);
		        if (remainingOre < 0) return;
		//        remainingOre = spawningRule(allyTypeCount, RobotType.MINER, 25, remainingOre, allyTypeCount[RobotType.MINERFACTORY.ordinal()]);
		//        if (remainingOre < 0) return;
		//        remainingOre = spawningRule(allyTypeCount, RobotType.DRONE, 9999, remainingOre, allyTypeCount[RobotType.HELIPAD.ordinal()]);
		//        if (remainingOre < 0) return;
	}


	public static void broadcastNextAttackLocation() throws GameActionException{
		int closest_dist = Integer.MAX_VALUE;
		int distance;
		MapLocation closest = RobotPlayer.enemyHq;
		MapLocation myLocation = rc.getLocation();

		for(int i = 0; i < RobotPlayer.enemyTowers.length; i++) {
			distance = myLocation.distanceSquaredTo(RobotPlayer.enemyTowers[i]);
			if (distance < closest_dist) {
				closest_dist = distance;
				closest = RobotPlayer.enemyTowers[i];
			}
		}

		rc.broadcast(MyConstants.ATTACK_LOCATION, closest.x);
		rc.broadcast(MyConstants.ATTACK_LOCATION + 1, closest.y);
	}
	
}