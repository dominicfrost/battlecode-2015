package sprintbot;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

    static Team myTeam;
    static Team enemyTeam;
    static Random rand;
    static MapLocation enemyHq;
    static MapLocation myHq;
    static MapLocation[] enemyTowers;
    static MapLocation[] myTowers;
    static MapLocation[] avoid;

	public static void run(RobotController rc) {
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        rand = new Random(rc.getID());
        enemyHq = rc.senseEnemyHQLocation();
        enemyTowers = rc.senseEnemyTowerLocations();
        myHq = rc.senseEnemyHQLocation();
        myTowers = rc.senseEnemyTowerLocations();

		while(true) {

            RobotType rt = rc.getType();
            myTeam = rc.getTeam();
            enemyTeam = myTeam.opponent();

			switch (rt) {
				case AEROSPACELAB:
					try {
						AEROSPACELAB.execute(rc);
					} catch (Exception e) {
						System.out.println("AEROSPACELAB Exception");
						e.printStackTrace();
					}
					break;
				case BARRACKS:
					try {
						BARRACKS.execute(rc);
					} catch (Exception e) {
						System.out.println("BARRACKS Exception");
						e.printStackTrace();
					}
					break;
				case BASHER:
					try {
						BASHER.execute(rc);
					} catch (Exception e) {
						System.out.println("BASHER Exception");
						e.printStackTrace();
					}
					break;
				case BEAVER:
					try {
						BEAVER.execute(rc);
					} catch (Exception e) {
						System.out.println("BEAVER Exception");
						e.printStackTrace();
					}
					break;
				case COMMANDER:
					try {
						COMMANDER.execute(rc);
					} catch (Exception e) {
						System.out.println("COMMANDER Exception");
						e.printStackTrace();
					}
					break;
				case COMPUTER:
					try {
						COMPUTER.execute(rc);
					} catch (Exception e) {
						System.out.println("COMPUTER Exception");
						e.printStackTrace();
					}
					break;
				case DRONE:
					try {
						DRONE.execute(rc);
					} catch (Exception e) {
						System.out.println("DRONE Exception");
						e.printStackTrace();
					}
					break;
				case HANDWASHSTATION:
					try {
						HANDWASHSTATION.execute(rc);
					} catch (Exception e) {
						System.out.println("HANDWASHSTATION Exception");
						e.printStackTrace();
					}
					break;
				case HELIPAD:
					try {
						HELIPAD.execute(rc);
					} catch (Exception e) {
						System.out.println("HELIPAD Exception");
						e.printStackTrace();
					}
					break;
				case HQ:
					try {
						HQ.execute(rc);
					} catch (Exception e) {
						System.out.println("HQ Exception");
						e.printStackTrace();
					}
					break;
				case LAUNCHER:
					try {
						LAUNCHER.execute(rc);
					} catch (Exception e) {
						System.out.println("LAUNCHER Exception");
						e.printStackTrace();
					}
					break;
				case MINER:
					try {
						MINER.execute(rc);
					} catch (Exception e) {
						System.out.println("MINER Exception");
						e.printStackTrace();
					}
					break;
				case MINERFACTORY:
					try {
						MINERFACTORY.execute(rc);
					} catch (Exception e) {
						System.out.println("MINERFACTORY Exception");
						e.printStackTrace();
					}
					break;
				case MISSILE:
					try {
						MISSLE.execute(rc);
					} catch (Exception e) {
						System.out.println("MISSILE Exception");
						e.printStackTrace();
					}
					break;
				case SOLDIER:
					try {
						SOLDIER.execute(rc);
					} catch (Exception e) {
						System.out.println("SOLDIER Exception");
						e.printStackTrace();
					}
					break;
				case SUPPLYDEPOT:
					try {
						SUPPLYDEPOT.execute(rc);
					} catch (Exception e) {
						System.out.println("SUPPLYDEPOT Exception");
						e.printStackTrace();
					}
					break;
				case TANK:
					try {
						TANK.execute(rc);
					} catch (Exception e) {
						System.out.println("TANK Exception");
						e.printStackTrace();
					}
					break;
				case TANKFACTORY:
					try {
						TANKFACTORY.execute(rc);
					} catch (Exception e) {
						System.out.println("TANKFACTORY Exception");
						e.printStackTrace();
					}
					break;
				case TECHNOLOGYINSTITUTE:
					try {
						TECHNOLOGYINSTITUTE.execute(rc);
					} catch (Exception e) {
						System.out.println("TECHNOLOGYINSTITUTE Exception");
						e.printStackTrace();
					}
					break;
				case TOWER:
					try {
						TOWER.execute(rc);
					} catch (Exception e) {
						System.out.println("TOWER Exception");
						e.printStackTrace();
					}
					break;
				case TRAININGFIELD:
					try {
						TRAININGFIELD.execute(rc);
					} catch (Exception e) {
						System.out.println("TRAININGFIELD Exception");
						e.printStackTrace();
					}
					break;
			}
			rc.yield();
		}
	}
}
