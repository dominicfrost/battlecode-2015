package marksbot2;

import java.util.Random;
import java.lang.Math.*;

import battlecode.common.*;

public class RobotPlayer{
	public static int HARASS_LOCATIONS = 45; // will occupy channels 45 - 57; 
	public static int SUPPLY_REQUEST_OFFSET = 58; // will occupy channels 58 - 88

	static RobotController rc;
	static Direction facing;
	static Random rand;

	static Boolean assaulting;


	public static void run(RobotController RC){
		rc = RC;
		rand = new Random(rc.getID());
		facing = getRandomDirection();

		assaulting = false;

		int maxBeavers = 10;
		int maxMiners = 30;

		while(true){
			try{
				switch(rc.getType()){
				case HQ:
					if (Clock.getRoundNum() == 1) {
						MapLocation rallyPoint = null;
						rallyPoint = getRallyPoint();

						rc.broadcast(0, rallyPoint.x);
						rc.broadcast(1,  rallyPoint.y);
						rc.broadcast(2, rc.senseEnemyHQLocation().x);
						rc.broadcast(3, rc.senseEnemyHQLocation().y);
					}
					if (rc.isCoreReady()){
						compileArmyStats();
						attackEnemyZero();
						if (getNum(RobotType.BEAVER) < maxBeavers){
							spawnUnit(RobotType.BEAVER);
						}
					}

					if (Clock.getRoundNum() >= 1800){
						MapLocation attackLocation = rc.senseEnemyHQLocation();
						rc.broadcast(0, attackLocation.x);
						rc.broadcast(1, attackLocation.y);	
					}


					break;

				case BEAVER:
					int maxMinerFactories = 2;
					int maxBarracks = 1;
					int maxTankFactories = 3;
					int maxHelipads = 1;
					int maxSupplyDepots = 2;

					if(rc.isCoreReady()){
						if (Clock.getRoundNum() >= 1800){	
							assault();
						}

						attackEnemyZero();

						MapLocation myLoc = rc.getLocation();
						MapLocation locHQ = rc.senseHQLocation();
						int distance = (myLoc.distanceSquaredTo(locHQ));

						if (distance < 36){
							if(getNum(RobotType.MINERFACTORY) < maxMinerFactories){
								buildUnit(RobotType.MINERFACTORY);	
							} else if (getNum(RobotType.HELIPAD) < maxHelipads){
								buildUnit(RobotType.HELIPAD);
							} else if (getNum(RobotType.BARRACKS) < maxBarracks){
								buildUnit(RobotType.BARRACKS);
							} else if (getNum(RobotType.SUPPLYDEPOT) < maxSupplyDepots){
								buildUnit(RobotType.SUPPLYDEPOT);
							} else if (getNum(RobotType.TANKFACTORY) < maxTankFactories){
								buildUnit(RobotType.TANKFACTORY);
							} 
						}

						if (spreadOut()){
							moveAround();
						} else {
							mineAndMove();

						}
					}
					break;

				case MINER:
					if(rc.isCoreReady()){
						if (Clock.getRoundNum() >= 1800){	
							assault();
						}
						attackEnemyZero();

						if (spreadOut()){
							moveAround();
						} else {
							mineAndMove();

						}
					}
					break;

				case MINERFACTORY:
					if (rc.isCoreReady()){
						if (getNum(RobotType.MINER) < maxMiners){
							spawnUnit(RobotType.MINER);
						}
					}
					break;


				case TOWER:
					if (rc.isCoreReady() && rc.isWeaponReady()){
						attackEnemyZero();
					}
					break;


				case BARRACKS:
					if (rc.isCoreReady()){
						if (rc.getTeamOre() >= 80){
							int fate = rand.nextInt(100);

							if (fate < 70){
								spawnUnit(RobotType.SOLDIER);
							} else {
								spawnUnit(RobotType.BASHER);
							}
						}
					}
					break;

				case TANKFACTORY:
					if (rc.isCoreReady()){ 
						if (rc.getTeamOre() >= 250){
							spawnUnit(RobotType.TANK);
						}
					}
					break;

				case HELIPAD:
					if (rc.isCoreReady()){ 
						if (rc.getTeamOre() >= 125){
							if (getNum(RobotType.DRONE) < 5){
								spawnUnit(RobotType.DRONE);
							}
						}
						break;
					}

				case TANK:
					if (Clock.getRoundNum() >= 1800){	
						assault();
					}
					if ((rc.readBroadcast(5) == 1 || assaulting) && !checkForRetreat()){
						assaulting = true;
						assault();
					} else {
						assaulting = false;
						attackEnemyZero();
						headToRallyPoint();
					}
					break;

				case SOLDIER:
					if (Clock.getRoundNum() >= 1800){	
						assault();
					}
					if ((rc.readBroadcast(5) == 1 || assaulting) && !checkForRetreat()){
						assaulting = true;
						assault();
					} else {
						assaulting = false;
						attackEnemyZero();
						headToRallyPoint();
					}
					break;

				case BASHER:
					if (Clock.getRoundNum() >= 1800){	
						assault();
					}
					if ((rc.readBroadcast(5) == 1 || assaulting) && !checkForRetreat()){
						assaulting = true;
						assault();
					} else {
						assaulting = false;
						attackEnemyZero();
						headToRallyPoint();
					}
					break;


				case DRONE:
					RobotType[] targets = {RobotType.BEAVER, RobotType.MINER, 
							RobotType.SOLDIER, RobotType.BASHER};
					Boolean hunting = false;
					Boolean supplying = false;
					double supplyLevel = rc.getSupplyLevel();

					if (rc.isCoreReady()){
						//if (supplyLevel < 250){
						//	resupply();
						//} else {
						harass(targets);
						//}
					}

					break;

				case SUPPLYDEPOT:
					if((Clock.getRoundNum() % 50) == 0 ){
						assaultReady();
					}
				}


				transferSupplies();

			} catch (GameActionException e){
				e.printStackTrace();}

			rc.yield();
		}
	}


	private static void resupply() throws GameActionException {
		MapLocation locHQ = rc.senseHQLocation();
		Direction dirToHQ = rc.getLocation().directionTo(locHQ);
		int distToHQ = rc.getLocation().distanceSquaredTo(locHQ);


		if (rc.canMove(dirToHQ)){
			rc.move(dirToHQ);
		}

	}


	private static void harass(RobotType[] targets) throws GameActionException {

		MapLocation myLocation = rc.getLocation();
		int sensorRange = rc.getType().sensorRadiusSquared;
		int attackRange = rc.getType().attackRadiusSquared;
		RobotInfo[] enemiesInSight = rc.senseNearbyRobots(sensorRange, rc.getTeam().opponent());
		RobotInfo[] enemiesInRange = rc.senseNearbyRobots(attackRange, rc.getTeam().opponent());

		Direction nextMove = null;

		// enemies in range
		if (enemiesInRange.length > 0){
			// shoot targets
			attackByType(enemiesInRange, targets);
		}

		// enemies in sight
		if (enemiesInSight.length > 0){
			Boolean safe = true;

			// can any of these guys hit me?
			for (RobotInfo enemy : enemiesInSight){
				int enemyRange = enemy.type.attackRadiusSquared;
				int distanceFromEnemy = myLocation.distanceSquaredTo(enemy.location);

				if (enemyRange >= distanceFromEnemy){
					safe = false;
				}
			}
			// kite
			nextMove = kiteDirection(myLocation, enemiesInSight);
			if (nextMove != null){
				rc.move(nextMove);
			}
		} else {
			rc.move(getMoveDir(rc.senseEnemyHQLocation()));
		}

		// enemies in range
		if (enemiesInRange.length > 0){
			// shoot targets
			attackByType(enemiesInRange, targets);
		}
	}


	// Assign a value to all surrounding squares. The square with the lowest value is returned, directions towards
	// enemy HQ are given preference. Square value = (2 * damage going to take next turn in that square) - (damage 
	// robot can inflict). 
	private static Direction kiteDirection(MapLocation baseSquare, RobotInfo[] enemiesInSight) {

		Direction dirToMove = null;
		int myRange = rc.getType().attackRadiusSquared;
		double[] squareValues = new double[8];
		double lowestVal = 0.0;

		// base case
		for (RobotInfo enemy : enemiesInSight){
			int enemyRange = enemy.type.attackRadiusSquared;
			int distanceFromEnemy = baseSquare.distanceSquaredTo(enemy.location);

			// if an enemy can hit me, add 2 * its damage to this square
			if (enemyRange >= distanceFromEnemy){
				lowestVal += (2 * enemy.type.attackPower);
			}
			// if I can hit an enemy, add 1 * my damage to this square
			if (distanceFromEnemy <= myRange){
				lowestVal -= (rc.getType().attackPower);
			}
		}

		// for each surrounding square
		for (Direction dir : Direction.values()){
			int index = directionToInt(dir);

			// determine value of each square
			for (RobotInfo enemy : enemiesInSight){
				int enemyRange = enemy.type.attackRadiusSquared;
				int distanceFromEnemy = baseSquare.add(dir).distanceSquaredTo(enemy.location);

				// if an enemy can hit me, add 2 * its damage to this square
				if (enemyRange >= distanceFromEnemy){
					squareValues[index] += (2 * enemy.type.attackPower);
				}

				// if I can hit an enemy, add 1 * my damage to this square
				if (distanceFromEnemy <= myRange){
					squareValues[index] -= (rc.getType().attackPower);
				}
			}

			// if this direction is safer than last one, set to move there
			if (squareValues[index] <= lowestVal && rc.canMove(dir)){
				lowestVal = squareValues[index];
				dirToMove = intToDirection(index);
			}	
		}
		return dirToMove;

	}

	public static Direction intToDirection(int i) {
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

	public static int directionToInt(Direction d) {
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


	private static Boolean checkForRetreat() throws GameActionException {
		int remainingTroops = rc.readBroadcast(6) + 1;
		rc.broadcast(6, remainingTroops);
		if (remainingTroops < 10){
			return true;
		} else {
			return false;
		}
	}


	private static void assault() throws GameActionException {
		MapLocation target = getAssaultOrders();

		if (rc.getLocation().distanceSquaredTo(target) < Math.sqrt(rc.getType().attackRadiusSquared)){
			if (rc.isWeaponReady() && rc.canAttackLocation(target)){
				rc.attackLocation(target);
			}	
		} else {
			attackEnemyZero();
		}


		Direction toMove = getMoveDir(target);
		if (toMove != null){
			rc.move(toMove);
		}
	}


	private static MapLocation getAssaultOrders() {
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation myLocation = rc.getLocation();
		MapLocation assaultTarget = null;

		if (enemyTowers.length > 0){
			MapLocation nearestTower = enemyTowers[0];
			int minDist = myLocation.distanceSquaredTo(nearestTower);

			for(MapLocation tower : enemyTowers){
				int d = myLocation.distanceSquaredTo(tower);
				if (d < minDist){
					nearestTower = tower;
				}	
			}

			assaultTarget = nearestTower;
		} else {
			assaultTarget = rc.senseEnemyHQLocation();
		}
		return assaultTarget;
	}


	private static void assaultReady() throws GameActionException {
		int armySize =  getNum(RobotType.SOLDIER) + getNum(RobotType.BASHER) + getNum(RobotType.TANK) + getNum(RobotType.DRONE);
		if (armySize >= 20){
			rc.broadcast(5, 1);
		} else {
			rc.broadcast(5, 0);
		}

		rc.broadcast(6, 0);
	}


	private static Boolean spreadOut() {
		RobotInfo[] nearbyRobots = rc.senseNearbyRobots(9, rc.getTeam());
		Boolean needToMove = false;
		for (RobotInfo robot : nearbyRobots){
			if (robot.type == RobotType.HQ || robot.type == RobotType.MINERFACTORY){
				needToMove = true;
			}
		}

		if (needToMove || nearbyRobots.length > 10){
			return true;
		}

		return false;

	}


	private static Direction scoutMiningLocations() {
		MapLocation myLocation = rc.getLocation();
		MapLocation surveySite = null;

		for (Direction dir : Direction.values()){
			surveySite = myLocation.add(dir);
			if(rc.senseOre(surveySite) > 0){
				return dir;
			}
		}
		return null;
	}


	private static MapLocation getRallyPoint() {
		MapLocation myHQ = rc.senseHQLocation();
		MapLocation theirHQ = rc.senseEnemyHQLocation();

		int distanceToEnemyHQ = myHQ.distanceSquaredTo(theirHQ);

		MapLocation rallyPoint = myHQ.add(myHQ.directionTo(theirHQ), (int)(Math.sqrt(distanceToEnemyHQ) / 4));

		return rallyPoint;
	}


	private static void compileArmyStats() throws GameActionException{
		int numBeavers = 0;
		int numMiners = 0;
		int numSoldiers = 0;
		int numTanks = 0;

		int numTowers = 0;
		int numMinerFactories = 0;
		int numBarracks = 0;
		int numTankFactories = 0;
		int numHelipads = 0;
		int numDrones = 0;

		int numSupplyDepots = 0;

		RobotInfo[] myArmy = rc.senseNearbyRobots(10000, rc.getTeam());
		for (RobotInfo thisRobot : myArmy){
			switch(thisRobot.type){
			case BEAVER:
				numBeavers++;
				break;
			case MINER:
				numMiners++;
				break;
			case SOLDIER:
				numSoldiers++;
				break;
			case TANK:
				numTanks++;
				break;
			case TOWER:
				numTowers++;
				break;
			case MINERFACTORY:
				numMinerFactories++;
				break;
			case BARRACKS:
				numBarracks++;
				break;
			case TANKFACTORY:
				numTankFactories++;
				break;	
			case HELIPAD:
				numHelipads++;
				break;
			case DRONE:
				numDrones++;
				break;
			case SUPPLYDEPOT:
				numSupplyDepots++;
				break;
			}
		}

		rc.broadcast(100, numBeavers);
		rc.broadcast(101, numMiners);
		rc.broadcast(102, numSoldiers);
		rc.broadcast(103, numTanks);
		rc.broadcast(104, numTowers);
		rc.broadcast(105, numMinerFactories);
		rc.broadcast(106, numBarracks);
		rc.broadcast(107, numTankFactories);
		rc.broadcast(108, numHelipads);
		rc.broadcast(109, numDrones);
		rc.broadcast(110, numSupplyDepots);


	}

	private static int getNum(RobotType rbt) throws GameActionException{
		switch(rbt){
		case BEAVER:
			return rc.readBroadcast(100);
		case MINER:
			return rc.readBroadcast(101);
		case SOLDIER:
			return rc.readBroadcast(102);
		case TANK:
			return rc.readBroadcast(103);
		case TOWER:
			return rc.readBroadcast(104);
		case MINERFACTORY:
			return rc.readBroadcast(105);
		case BARRACKS:
			return rc.readBroadcast(106);
		case TANKFACTORY:
			return rc.readBroadcast(107);
		case HELIPAD:
			return rc.readBroadcast(108);
		case DRONE:
			return rc.readBroadcast(109);
		case SUPPLYDEPOT:
			return rc.readBroadcast(110);
		default:
			return 0;
		}

	}


	private static void headToRallyPoint() throws GameActionException{
		int rallyX = rc.readBroadcast(0);
		int rallyY = rc.readBroadcast(1);

		MapLocation rallyPoint = new MapLocation(rallyX, rallyY);
		Direction toMove = getMoveDir(rallyPoint);
		if (toMove != null){
			rc.move(toMove);
		}
		else {
			moveAround();
		}

	}


	private static Direction getMoveDir(MapLocation dest) throws GameActionException {
		Direction[] dirs = getDirectionsToward(dest);
		for (Direction d : dirs) {
			if (rc.canMove(d)) {
				return d;
			}
		}
		return null;
	}

	private static Direction[] getDirectionsToward(MapLocation dest) throws GameActionException {
		Direction toDest = rc.getLocation().directionTo(dest);
		Direction[] dirs = {toDest,
				toDest.rotateLeft(), toDest.rotateRight(),
				toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

		return dirs;
	}


	private static void transferSupplies() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(), GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation suppliesToThisLocation = null;
		for(RobotInfo ri:nearbyAllies){
			if (ri.supplyLevel < lowestSupply){
				lowestSupply = ri.supplyLevel;
				transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
				suppliesToThisLocation = ri.location;
			}
		}
		if(suppliesToThisLocation != null){
			rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
		}

	}


	private static void buildUnit(RobotType type) throws GameActionException {
		if(rc.getTeamOre() > type.oreCost){
			Direction buildDir = getRandomDirection();
			MapLocation buildLocation = rc.getLocation().add(buildDir);

			Boolean canBuild = true;
			RobotInfo[] nearbyBuildings = rc.senseNearbyRobots(buildLocation, 1, rc.getTeam());

			for (RobotInfo rb : nearbyBuildings){
				switch (rb.type){
				case MINERFACTORY:
				case BARRACKS:
				case HELIPAD:
				case SUPPLYDEPOT:
				case TOWER:
					canBuild = false;
					break;
				case HQ:
					canBuild = false;
					moveAround();
				default:
					break;
				}
			}



			if (rc.canBuild(buildDir, type) && canBuild){
				rc.build(buildDir, type);
			}
		}

	}


	private static void attackEnemyZero() throws GameActionException {
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		if (nearbyEnemies.length > 0){
			if (rc.isWeaponReady() && rc.canAttackLocation(nearbyEnemies[0].location)){
				rc.attackLocation(nearbyEnemies[0].location);
			}
		}

	}

	private static void attackByType(RobotInfo[] enemies, RobotType[] targetTypes) throws GameActionException {
		if (!rc.isWeaponReady()){
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


	private static void spawnUnit(RobotType rb) throws GameActionException {
		Direction randomDir = getRandomDirection();
		if (rc.canSpawn(randomDir,  rb)){
			rc.spawn(randomDir, rb);	
		}

	}


	private static Direction getRandomDirection() {
		return Direction.values()[(int)(rand.nextDouble()*8)];
	}


	private static void mineAndMove() throws GameActionException {
		if(rc.senseOre(rc.getLocation()) > 0){
			if (rc.canMine() == true){
				rc.mine();
			}
		} else {
			Direction oreSensed = scoutMiningLocations();
			if (oreSensed != null){
				rc.move(oreSensed);
			} else {
				moveAround();
			}
		}
	}


	private static void moveAround() throws GameActionException {if(rand.nextDouble() < 0.05){
		if (rand.nextDouble() < 0.5){
			facing = facing.rotateLeft();
		} else {
			facing = facing.rotateRight();
		}

		MapLocation tileInFront = rc.getLocation().add(facing);

		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		boolean tileInFrontSafe = true;
		for(MapLocation m: enemyTowers){
			if(m.distanceSquaredTo(tileInFront) <= RobotType.TOWER.attackRadiusSquared){
				tileInFrontSafe = false;
				break;
			}
		}

		if(rc.senseTerrainTile(rc.getLocation().add(facing))!=TerrainTile.NORMAL || !tileInFrontSafe){
			facing = facing.rotateLeft();
		} else {
			if (rc.canMove(facing)){
				rc.move(facing);
			}

		}	
	}

	}
}
