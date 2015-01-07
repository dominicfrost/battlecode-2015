package marksbot;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

	public static void run(RobotController rc) {

		while(true) {
			try {
				rc.setIndicatorString(0, "This is an indicator string.");
				rc.setIndicatorString(1, "I am a " + rc.getType());
			} catch (Exception e) {
				System.out.println("Unexpected exception");
				e.printStackTrace();
			}

			switch (rc.getType()) {
				case RobotType.AEROSPACELAB:
					try {
						AEROSPACE.execute(rc);
					} catch (Exception e) {
						System.out.println("AEROSPACELAB Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.BARRACKS:
					try {
						BARRACKS.execute(rc);
					} catch (Exception e) {
						System.out.println("BARRACKS Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.BASHER:
					try {
						BASHER.execute(rc);
					} catch (Exception e) {
						System.out.println("BASHER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.BEAVER:
					try {
						BEAVER.execute(rc);
					} catch (Exception e) {
						System.out.println("BEAVER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.COMMANDER:
					try {
						COMMANDER.execute(rc);
					} catch (Exception e) {
						System.out.println("COMMANDER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.COMPUTER:
					try {
						COMPUTER.execute(rc);
					} catch (Exception e) {
						System.out.println("COMPUTER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.DRONE:
					try {
						DRONE.execute(rc);
					} catch (Exception e) {
						System.out.println("DRONE Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.HANDWASHSTATION:
					try {
						HANDWASHSTATION.execute(rc);
					} catch (Exception e) {
						System.out.println("HANDWASHSTATION Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.HELIPAD:
					try {
						HELIPAD.execute(rc);
					} catch (Exception e) {
						System.out.println("HELIPAD Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.HQ:
					try {
						HQ.execute(rc);
					} catch (Exception e) {
						System.out.println("HQ Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.LAUNCHER:
					try {
						LAUNCHER.execute(rc);
					} catch (Exception e) {
						System.out.println("LAUNCHER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.MINER:
					try {
						MINER.execute(rc);
					} catch (Exception e) {
						System.out.println("MINER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.MINERFACTORY:
					try {
						MINERFACTORY.execute(rc);
					} catch (Exception e) {
						System.out.println("MINERFACTORY Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.MISSILE:
					try {
						MISSILE.execute(rc);
					} catch (Exception e) {
						System.out.println("MISSILE Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.SOLDIER:
					try {
						SOLDIER.execute(rc);
					} catch (Exception e) {
						System.out.println("SOLDIER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.SUPPLYDEPOT:
					try {
						SUPPLYDEPOT.execute(rc);
					} catch (Exception e) {
						System.out.println("SUPPLYDEPOT Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.TANK:
					try {
						TANK.execute(rc);
					} catch (Exception e) {
						System.out.println("TANK Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.TANKFACTORY:
					try {
						TANKFACTORY.execute(rc);
					} catch (Exception e) {
						System.out.println("TANKFACTORY Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.TECHNOLOGYINSTITUTE:
					try {
						TECHNOLOGYINSTITUTE.execute(rc);
					} catch (Exception e) {
						System.out.println("TECHNOLOGYINSTITUTE Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.TOWER:
					try {
						TOWER.execute(rc);
					} catch (Exception e) {
						System.out.println("TOWER Exception");
						e.printStackTrace();
					}
					break;
				case RobotType.TRAININGFIELD:
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
