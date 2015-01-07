package marksbot2;

import java.util.Random;
import battlecode.common.*;

public class RobotPlayer{
	static RobotController rc;
	static Direction facing;
	static Random rand;


	public static void run(RobotController RC){
		rc = RC;
		rand = new Random(rc.getID());
		facing = getRandomDirection();

		while(true){
			try{
				switch(rc.getType()){
				case HQ:
					if (rc.isCoreReady()){
						attackEnemyZero();
						spawnUnit(RobotType.BEAVER);
					}	

				case BEAVER:
					if(rc.isCoreReady()){
						if(rc.getTeamOre() > RobotType.MINERFACTORY.oreCost){
							
							
							Direction buildDir = getRandomDirection();
							if (rc.canBuild(buildDir, RobotType.MINERFACTORY)){
								rc.build(buildDir, RobotType.MINERFACTORY);
							}
						}
						
						mineAndMove();
					}
					
				case MINER:
					if(rc.isCoreReady()){
						mineAndMove();
					}
					
				case MINERFACTORY:
					if (rc.isCoreReady()){
						spawnUnit(RobotType.MINER);
					}
					

				case TOWER:
					attackEnemyZero();
					
					
				case BARRACKS:
					spawnUnit(RobotType.SOLDIER);
				}

			} catch (GameActionException e){
				e.printStackTrace();}

			rc.yield();
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
}
