package marksbot;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	static RobotController rc;

	static Team myTeam;
	static Team enemyTeam;

	static int myRange;
	static Random rand;
	static Direction facing;

	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

	public static void run(RobotController RC) {
		rc = RC;
		rand = new Random(rc.getID());

		myRange = rc.getType().attackRadiusSquared;
		facing = getRandomDirection();

		MapLocation enemyLoc = rc.senseEnemyHQLocation();
		Direction lastDirection = null;

		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();

		RobotInfo[] myRobots;

		while(true) {
			try {
				rc.setIndicatorString(0, "This is an indicator string.");
				rc.setIndicatorString(1, "I am a " + rc.getType());
			} catch (Exception e) {
				System.out.println("Unexpected exception");
				e.printStackTrace();
			}

			switch(rc.getType()){
			case HQ:
				try {					
					int fate = rand.nextInt(10000);
					myRobots = rc.senseNearbyRobots(999999, myTeam);

					int numBarracks = 0;
					int numSoldiers = 0;
					int numBashers = 0;

					int numBeavers = 0;
					int numMiners = 0;
					int numMiningFactories = 0;

					int numTanks = 0;
					int numTankFactories = 0;

					int numSupplyDepos = 0;

					for (RobotInfo r : myRobots) {
						switch(r.type){

						case BEAVER:
							numBeavers++;
							break;
						case MINER:
							numMiners++;
							break;
						case MINERFACTORY:
							numMiningFactories++;
							break;

						case SOLDIER:
							numSoldiers++;
							break;
						case BASHER:
							numBashers++;
							break;
						case BARRACKS:
							numBarracks++;
							break;

						case TANK:
							numTanks++;
							break;
						case TANKFACTORY:
							numTankFactories++;
							break;


						case SUPPLYDEPOT:
							numSupplyDepos++;
							break;
						}
					}

					rc.broadcast(0, numBeavers);
					rc.broadcast(1, numMiners);
					rc.broadcast(2, numMiningFactories);
					rc.broadcast(3, numSoldiers);
					rc.broadcast(4, numBashers);
					rc.broadcast(5, numBarracks);
					rc.broadcast(6, numTanks);
					rc.broadcast(7,  numTankFactories);
					rc.broadcast(8,  numSupplyDepos);

					if (rc.isWeaponReady()) {
						attackSomething();
					}

					if (rc.isCoreReady() && rc.getTeamOre() >= 100 && numBeavers < 20) {
						trySpawn(directions[rand.nextInt(8)], RobotType.BEAVER);
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
					e.printStackTrace();
				}
				break;

			case TOWER:
				try {					
					if (rc.isWeaponReady()) {
						attackSomething();
					}
				} catch (Exception e) {
					System.out.println("Tower Exception");
					e.printStackTrace();
				}
				break;

			case BASHER:
				try {
					RobotInfo[] adjacentEnemies = rc.senseNearbyRobots(2, enemyTeam);

					// BASHERs attack automatically, so let's just move around mostly randomly
					if (rc.isCoreReady()) {
						int fate = rand.nextInt(1000);
						if (fate < 800) {
							tryMove(directions[rand.nextInt(8)]);
						} else {
							tryMove(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
						}
					}
				} catch (Exception e) {
					System.out.println("Basher Exception");
					e.printStackTrace();
				}
				break;

			case SOLDIER:
				try {
					if (rc.isWeaponReady()) {
						attackSomething();
					}
					if (rc.isCoreReady()) {
						int fate = rand.nextInt(1000);
						if (fate < 800) {
							tryMove(directions[rand.nextInt(8)]);
						} else {
							tryMove(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
						}
					}
				} catch (Exception e) {
					System.out.println("Soldier Exception");
					e.printStackTrace();
				}
				break;

			case BEAVER:
				try {
					if (rc.isWeaponReady()) {
						attackSomething();
					}

					int numSupplyDepots = rc.readBroadcast(8);
					int numBarracks = rc.readBroadcast(5);
					int numTankFactories = rc.readBroadcast(7);
					int numMinerFactories = rc.readBroadcast(2);

					if (rc.isCoreReady()) {
						if (rc.getTeamOre() >= 500 && numMinerFactories < 3) {
							tryBuild(directions[rand.nextInt(8)],RobotType.MINERFACTORY);
						} else if (rc.getTeamOre() >= 100 && numSupplyDepots < 5) {
							tryBuild(directions[rand.nextInt(8)],RobotType.SUPPLYDEPOT);
						} else if (rc.getTeamOre() >= 300 && numBarracks < 5) {
							tryBuild(directions[rand.nextInt(8)],RobotType.BARRACKS);
						} else if (rc.getTeamOre() >= 500 && numTankFactories < 2) {
							tryBuild(directions[rand.nextInt(8)],RobotType.TANKFACTORY);
						} else {
							mineAndMove();
						}
					}
				} catch (Exception e) {
					System.out.println("Beaver Exception");
					e.printStackTrace();
				}
				break;

			case BARRACKS:
				try {
					// get information broadcasted by the HQ
					int numBeavers = rc.readBroadcast(0);
					int numMiners = rc.readBroadcast(1);
					int numMinerFactories = rc.readBroadcast(2);


					int numSoldiers = rc.readBroadcast(3);
					int numBashers = rc.readBroadcast(4);
					int numBarracks = rc.readBroadcast(5);

					int numTanks = rc.readBroadcast(6);

					double soldierCap = 0.4;
					double basherCap = 0.4;

					double soldierProp = (numSoldiers / (numSoldiers + numBashers + numTanks));
					double basherProp = (numBashers / (numSoldiers + numBashers + numTanks));

					if (rc.isCoreReady() && rc.getTeamOre() >= 60) {
						if (rc.getTeamOre() > 80 && basherProp < basherCap) {
							trySpawn(directions[rand.nextInt(8)],RobotType.BASHER);
						} else {
							trySpawn(directions[rand.nextInt(8)],RobotType.SOLDIER);
						}
					}
				} catch (Exception e) {
					System.out.println("Barracks Exception");
					e.printStackTrace();
				}
				break;
			}

			rc.yield();
		}
	}

	// This method will attack an enemy in sight, if there is one
	static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if (enemies.length > 0) {
			rc.attackLocation(enemies[0].location);
		}
	}

	// This method will attempt to move in Direction d (or as close to it as possible)
	static void tryMove(Direction d) throws GameActionException {
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

	// This method will attempt to spawn in the given direction (or as close to it as possible)
	static void trySpawn(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}

	// This method will attempt to build in the given direction (or as close to it as possible)
	static void tryBuild(Direction d, RobotType type) throws GameActionException {
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

	static int directionToInt(Direction d) {
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
			return -1;
		}
	}

	private static void mineAndMove() throws GameActionException {
		if(rc.senseOre(rc.getLocation()) > 1){
			if (rc.canMine() == true){
				rc.mine();
			}
		} else {
			moveAround();
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
		}

		if (rc.canMove(facing)){
			rc.move(facing);
		}	
	}
	}

	private static Direction getRandomDirection() {
		return Direction.values()[(int)(rand.nextDouble()*8)];
	}

}