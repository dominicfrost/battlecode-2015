package sprintbot;

import battlecode.common.*;

import java.util.*;

public class Util {
	public static Random rand = new Random();
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};


	/*
	 * spawns the current spawn type if possible
	 */
	public static boolean buildWithPrecedence(RobotController rc, Direction d, RobotType[] canBuild) throws GameActionException{
		if (!RobotPlayer.coreReady) {
			return false;
		}

		int numToBuild;
		double myOre = rc.getTeamOre();

		for (RobotType type: canBuild) {
			numToBuild = rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal());
			if (numToBuild > 0 && myOre >= type.oreCost) {
				tryBuild(rc, d, type);
				numToBuild--;
				rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToBuild);
				return true;
			}
		}
		return false;
	}

	/*
	 * spawns the current spawn type if possible
	 */
	public static boolean spawnWithPrecedence(RobotController rc, Direction d, RobotType[] canSpawn) throws GameActionException{
		if (!RobotPlayer.coreReady) {
			return false;
		}

		int numToSpawn;
		double myOre = rc.getTeamOre();

		for (RobotType type: canSpawn) {
			numToSpawn = rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal());
			if (numToSpawn > 0 && myOre >= type.oreCost) {
				trySpawn(rc, d, type);
				numToSpawn--;
				rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToSpawn);
				return true;
			}
		}
		return false;
	}

	public static void generalAttack(RobotController rc) throws GameActionException{
		MapLocation goal = new MapLocation(rc.readBroadcast(MyConstants.ATTACK_LOCATION), rc.readBroadcast(MyConstants.ATTACK_LOCATION + 1));
		if (RobotPlayer.coreReady) {
			if (rc.canAttackLocation(goal)) {
				rc.attackLocation(goal);
				return;
			}
			RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
			if (!attack(rc, enemyRobots)) {
				if (rc.getHealth() < 40) {
					tryMove(rc, rc.getLocation().directionTo(rc.senseHQLocation()));
					return;
				}
				tryMove(rc, rc.getLocation().directionTo(goal));
			}
		}
	}

	public static int mapLocToInt(MapLocation m)  throws GameActionException{
		return (m.x*10000 + m.y);
	}

	public static MapLocation intToMapLoc(int i)  throws GameActionException{
		return new MapLocation(i/10000,i%10000);
	}

	// This method will attempt to move in Direction d (or as close to it as possible)
	public static void tryMove(RobotController rc, Direction d) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5) {
			rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
		}
	}

	// This method will attack an enemy in sight, if there is one
	public static void attackSomething(RobotController rc, int myRange, Team enemyTeam) throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if (enemies.length > 0) {
			rc.attackLocation(enemies[0].location);
		}
	}

	public static MapLocation getNewPointOfInterest(RobotController rc) throws GameActionException{
		int numPointsOfInterest = rc.readBroadcast(MyConstants.NUM_POINTS_OF_INTEREST_OFFSET);
		int offSet = MyConstants.POINTS_OF_INTEREST_OFFSET;

		int random = RobotPlayer.rand.nextInt(numPointsOfInterest);
		random = 2 * random;
		MapLocation pointOfInterest = new MapLocation(rc.readBroadcast(offSet + random), rc.readBroadcast(offSet + random + 1));
		
		return pointOfInterest;

	}

	public static boolean safeToMoveTo(RobotController rc, MapLocation myLocation) throws GameActionException {
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation enemyHQ = rc.senseEnemyHQLocation();

		if (myLocation.distanceSquaredTo(enemyHQ) <= RobotType.HQ.attackRadiusSquared) {
			return false;
		}

		for (MapLocation tower: enemyTowers) {
			if (myLocation.distanceSquaredTo(tower) <=  RobotType.TOWER.attackRadiusSquared) {
				return false;
			}
		}
		return true;
	}

	// This function will try to move to a given location. It will move around enemy towers and HQ.
	public static void moveToLocation(RobotController rc, MapLocation goal) throws GameActionException {
		MapLocation myLocation = rc.getLocation();
		Direction goalDir = myLocation.directionTo(goal);
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(goalDir);

		masterLoop: for (int i = 0; i < offsets.length; i++) {
			Direction targetDir = directions[(dirint+offsets[i]+8)%8];
			if (!rc.canMove(targetDir)) {
				//continue in loop if we cant move to the target location
				continue;
			}
			MapLocation targetLocation = myLocation.add(targetDir);
			for (int j = 0, len = RobotPlayer.enemyTowers.length; j < len; j++) {
				MapLocation towerLocation = RobotPlayer.enemyTowers[j];
				if (targetLocation.distanceSquaredTo(towerLocation) <= 24) {
					// continue in loop if the location is within range of an enemy tower
					continue masterLoop;
				}
			}

			if (targetLocation.distanceSquaredTo(RobotPlayer.enemyHq) <= 35) {
				// continue in loop if the location is within range of an enemy HQ
				continue;
			}

			// we can move to the target location if we have made it this far.
			rc.move(targetDir);
			break;
		}
	}

	// This method will attempt to build in the given direction (or as close to it as possible)
	public static void tryBuild(RobotController rc, Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}

	//use this to return the map location in a specific direction from your robot
	public static MapLocation getAdjacentLocation(RobotController rc, Direction dir, MapLocation loc) throws GameActionException {
		return (loc.add(dir));
	}



	//prefer adjacent tiles with more ore
	//compare ore at current location to ore at all adjacent tiles
	//move to the most fruitful tile, unless there are several w/ same amount, then pick one of them at random
	//frig
	public static void SmartMine(RobotController rc) throws GameActionException {
		if (!RobotPlayer.coreReady) {
			return;
		}

		if(!flee(rc,rc.senseNearbyRobots(rc.getLocation(),24, RobotPlayer.enemyTeam)))
		{
			MapLocation myLocation = rc.getLocation();
			double oreCount = rc.senseOre(myLocation);
			ArrayList<Integer> OreLocations = new ArrayList<Integer>();

			double ore_N = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH, myLocation));
			double ore_NE = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH_EAST, myLocation));
			double ore_E = rc.senseOre(getAdjacentLocation(rc, Direction.EAST, myLocation));
			double ore_SE = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH_EAST, myLocation));
			double ore_S = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH, myLocation));
			double ore_SW = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH_WEST, myLocation));
			double ore_W = rc.senseOre(getAdjacentLocation(rc, Direction.WEST, myLocation));
			double ore_NW = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH_WEST, myLocation));


			if(1.3*oreCount < ore_N)
				OreLocations.add(1);
			if(1.3*oreCount < ore_NE)
				OreLocations.add(2);
			if(1.3*oreCount < ore_E)
				OreLocations.add(3);
			if(1.3*oreCount < ore_SE)
				OreLocations.add(4);
			if(1.3*oreCount < ore_S)
				OreLocations.add(5);
			if(1.3*oreCount < ore_SW)
				OreLocations.add(6);
			if(1.3*oreCount < ore_W)
				OreLocations.add(7);
			if(1.3*oreCount < ore_NW)
				OreLocations.add(8);

			if(OreLocations.size() == 0)
				if (oreCount > 0)
					rc.mine();
				else
					tryMove(rc, Util.intToDirection(RobotPlayer.rand.nextInt(8)));
			else {
				int pick = RobotPlayer.rand.nextInt(OreLocations.size());
				Util.tryMove(rc, intToDirection((int) OreLocations.get(pick)));
			}
		}
	}

	// mine like a dummy
	public static void mine(RobotController rc) throws GameActionException {
		if (!RobotPlayer.coreReady) {
			return;
		}

		MapLocation myLocation = rc.getLocation();
		double oreCount = rc.senseOre(myLocation);
		if (oreCount > 0) {
			rc.mine();
		}else {
			int fate = rand.nextInt(8);
			Util.tryMove(rc, Util.intToDirection(fate));
		}
	}

	public static Direction intToDirection(int i)  throws GameActionException {
		switch(i) {
		case 0:
			return Direction.NORTH;
		case 1:
			return Direction.NORTH_EAST;
		case 2:
			return Direction.EAST;
		case 3:
			return Direction.SOUTH_EAST;
		case 4:
			return Direction.SOUTH;
		case 5:
			return Direction.SOUTH_WEST;
		case 6:
			return Direction.WEST;
		case 7:
			return Direction.NORTH_WEST;
		default:
			return Direction.NORTH;
		}
	}

	public static int directionToInt(Direction d)  throws GameActionException {
		switch(d) {
		case NORTH:
			return 0;
		case NORTH_EAST:
			return 1;
		case EAST:
			return 2;
		case SOUTH_EAST:
			return 3;
		case SOUTH:
			return 4;
		case SOUTH_WEST:
			return 5;
		case WEST:
			return 6;
		case NORTH_WEST:
			return 7;
		default:
			return 0;
		}
	}

	public static boolean flee(RobotController rc, RobotInfo[] enemyRobots) throws GameActionException{
		ArrayDeque<MapLocation> enemieLocs = new ArrayDeque<MapLocation>();
		for (RobotInfo robot: enemyRobots) {
			if (robot.type.attackRadiusSquared <= robot.location.distanceSquaredTo(rc.getLocation())) {
				enemieLocs.add(robot.location);
			}
		}
		if (enemieLocs.size() > 0) {
			int counterx = 0;
			int countery = 0;
			for (MapLocation oppLoc : enemieLocs) {
				counterx += oppLoc.x;
				countery += oppLoc.y;
			}

			MapLocation avg = new MapLocation(counterx / enemieLocs.size(), countery / enemieLocs.size());
			Direction dirToGoal = rc.getLocation().directionTo(avg).opposite();
			tryMove(rc, dirToGoal);
			return true;
		}

		return false;
	}

	public static int[] getRobotCount(RobotController rc) throws GameActionException{
		int[] robotCount = new int[21];
		for (int i = 0; i < robotCount.length; i++) {
			robotCount[i] = rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + i);
		}

		return robotCount;
	}

	public static RobotType getRobotTypeToSpawn(RobotController rc) throws GameActionException{
		RobotType[] types = RobotType.values();
		return types[rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET)];
	}

	// This method will attempt to spawn in the given direction (or as close to it as possible)
	public static void trySpawn(RobotController rc, Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = Util.directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canSpawn(Util.directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.spawn(Util.directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}


	public static boolean attack(RobotController rc, RobotInfo[] enemyRobots) throws GameActionException{
		if (RobotPlayer.weaponReady && enemyRobots.length > 0) {
			MapLocation myLocation = rc.getLocation();
			RobotInfo toAttack = enemyRobots[0];
			int closest = Integer.MAX_VALUE;

			for (RobotInfo enemy : enemyRobots) {
				int distanceToEnemy = myLocation.distanceSquaredTo(enemy.location);
				if (distanceToEnemy < closest) {
					closest = distanceToEnemy;
					toAttack = enemy;
				} else if (distanceToEnemy == closest) {
					if (enemy.health < toAttack.health) {
						toAttack = enemy;
					}
				}
			}

			if (rc.canAttackLocation(toAttack.location)) {
				rc.attackLocation(toAttack.location);
				return true;
			}
		}
		return false;
	}
	public static ArrayList<MapLocation> calcMLine(RobotController rc, MapLocation goal) throws GameActionException {
		Direction dirToGoal;
		ArrayList<MapLocation> mLine = new ArrayList<MapLocation>();
		MapLocation previousLocation = rc.getLocation();

		while (!previousLocation.equals(goal)) {
			mLine.add(previousLocation);
			dirToGoal = previousLocation.directionTo(goal);
			previousLocation = previousLocation.add(dirToGoal);
		}

		return mLine;
	}

	public static Boolean harass(RobotController rc, RobotType[] targets) throws GameActionException {

		MapLocation myLocation = rc.getLocation();
		RobotInfo[] enemiesInSight = rc.senseNearbyRobots(RobotPlayer.sensorRange, rc.getTeam().opponent());
		RobotInfo[] enemiesInRange = rc.senseNearbyRobots(RobotPlayer.attackRange, rc.getTeam().opponent());

		Direction nextMove = null;

		// enemies in range
		if (enemiesInRange.length > 0 && rc.isWeaponReady()){
			// shoot targets
			attackByType(rc, enemiesInRange, targets);
		}

		// enemies in sight
		if (enemiesInSight.length > 0){
			nextMove = kiteDirection(rc, myLocation, enemiesInSight);
			if (nextMove != null && rc.canMove(nextMove)){
				rc.move(nextMove);
				if (rc.isWeaponReady()){
					attackByType(rc, enemiesInRange, targets);
				}
			}
			return true;
		}
		return false;
	}

	// Assign a value to all surrounding squares. The square with the lowest value is returned, directions towards
	// enemy HQ are given preference. Square value = (2 * damage going to take next turn in that square) - (damage 
	// robot can inflict). 
	private static Direction kiteDirection(RobotController rc, MapLocation baseSquare, RobotInfo[] enemiesInSight)  throws GameActionException{
		Direction dirToMove = null;
		int myRange = rc.getType().attackRadiusSquared;
		double[] squareValues = new double[8];
		double lowestVal = 0.0;
		Boolean safeToMove = true;

		// base case
		// if in range of HQ or enemy Towers
		safeToMove = safeToMoveTo(rc, baseSquare);
		if(!safeToMove){
			lowestVal += (2 * RobotType.TOWER.attackPower);
		}

		for (RobotInfo enemy : enemiesInSight){
			int enemyRange = enemy.type.attackRadiusSquared;
			int distanceFromEnemy = baseSquare.distanceSquaredTo(enemy.location);

			// if an enemy can hit me, add 2 * its damage to this square
			if (enemyRange >= distanceFromEnemy){
				lowestVal += (2 * enemy.type.attackPower);
			}
			// if I can hit an enemy, add 1 * my damage to this square
			if (distanceFromEnemy <= myRange){
				lowestVal -= 1;
			}
		}

		// for each surrounding square
		for (Direction dir : directions){
			int index = directionToInt(dir);
			// if this direction is towards point of interest
			MapLocation pointInt = RobotPlayer.pointOfInterest;
			if (pointInt != null) {
				if (baseSquare.directionTo(pointInt) == dir){
					squareValues[index] -= 1;
				}
			}
			// determine value of each square
			for (RobotInfo enemy : enemiesInSight){
				int enemyRange = enemy.type.attackRadiusSquared;
				int distanceFromEnemy = baseSquare.add(dir).distanceSquaredTo(enemy.location);
				// if an enemy can hit me, add 2 * its damage to this square
				if (enemyRange >= distanceFromEnemy){
					squareValues[index] += (2 * enemy.type.attackPower);
				}
				safeToMove = safeToMoveTo(rc, baseSquare.add(dir));
				if(!safeToMove){
					squareValues[index] += (2 * RobotType.TOWER.attackPower);
				}
				// if I can hit an enemy, add 1
				if (distanceFromEnemy <= myRange){
					squareValues[index] -= 1;
				}
			}
			// if this direction is safer than last one, set to move there
			if (squareValues[index] < lowestVal && rc.canMove(dir)){
				lowestVal = squareValues[index];
				dirToMove = intToDirection(index);
			}	
		}
		return dirToMove;
	}
	private static void attackByType(RobotController rc, RobotInfo[] enemies, RobotType[] targetTypes) throws GameActionException {
		if (enemies.length == 0) {
			return;
		}
		RobotInfo target = enemies[0];
		double lowestHealth = enemies[0].health;

		for (RobotInfo enemy : enemies){
			for (RobotType tType : targetTypes){
				if (enemy.type == tType && enemy.health < lowestHealth){
					target = enemy;
					lowestHealth = target.health;	
				}
			}
		}
		if (rc.canAttackLocation(target.location)){
			rc.attackLocation(target.location);
		}
	}

	public static void debug(RobotController rc, String msg) throws GameActionException {
		if (rc.getID() == 26777 && Clock.getRoundNum() < 1000) {
			System.out.println("DEBUG: " + msg);
		}
	}
}