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
				case RobotPlayer.AEROSPACELAB:
					try {

					} catch (Exception e) {
						System.out.println("AEROSPACELAB Exception");
						e.printStackTrace();
					}
				case RobotPlayer.BARRACKS:
					try {

					} catch (Exception e) {
						System.out.println("BARRACKS Exception");
						e.printStackTrace();
					}
				case RobotPlayer.BASHER:
					try {

					} catch (Exception e) {
						System.out.println("BASHER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.BEAVER:
					try {

					} catch (Exception e) {
						System.out.println("BEAVER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.COMMANDER:
					try {

					} catch (Exception e) {
						System.out.println("COMMANDER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.COMPUTER:
					try {

					} catch (Exception e) {
						System.out.println("COMPUTER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.DRONE:
					try {

					} catch (Exception e) {
						System.out.println("DRONE Exception");
						e.printStackTrace();
					}
				case RobotPlayer.HANDWASHSTATION:
					try {

					} catch (Exception e) {
						System.out.println("HANDWASHSTATION Exception");
						e.printStackTrace();
					}
				case RobotPlayer.HELIPAD:
					try {

					} catch (Exception e) {
						System.out.println("HELIPAD Exception");
						e.printStackTrace();
					}
				case RobotPlayer.HQ:
					try {

					} catch (Exception e) {
						System.out.println("HQ Exception");
						e.printStackTrace();
					}
				case RobotPlayer.LAUNCHER:
					try {

					} catch (Exception e) {
						System.out.println("LAUNCHER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.MINER:
					try {

					} catch (Exception e) {
						System.out.println("MINER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.MINERFACTORY:
					try {

					} catch (Exception e) {
						System.out.println("MINERFACTORY Exception");
						e.printStackTrace();
					}
				case RobotPlayer.MISSILE:
					try {

					} catch (Exception e) {
						System.out.println("MISSILE Exception");
						e.printStackTrace();
					}
				case RobotPlayer.SOLDIER:
					try {

					} catch (Exception e) {
						System.out.println("SOLDIER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.SUPPLYDEPOT:
					try {

					} catch (Exception e) {
						System.out.println("SUPPLYDEPOT Exception");
						e.printStackTrace();
					}
				case RobotPlayer.TANK:
					try {

					} catch (Exception e) {
						System.out.println("TANK Exception");
						e.printStackTrace();
					}
				case RobotPlayer.TANKFACTORY:
					try {

					} catch (Exception e) {
						System.out.println("TANKFACTORY Exception");
						e.printStackTrace();
					}
				case RobotPlayer.TECHNOLOGYINSTITUTE:
					try {

					} catch (Exception e) {
						System.out.println("TECHNOLOGYINSTITUTE Exception");
						e.printStackTrace();
					}
				case RobotPlayer.TOWER:
					try {

					} catch (Exception e) {
						System.out.println("TOWER Exception");
						e.printStackTrace();
					}
				case RobotPlayer.TRAININGFIELD:
					try {

					} catch (Exception e) {
						System.out.println("TRAININGFIELD Exception");
						e.printStackTrace();
					}
			}
			rc.yield();
		}
	}
}
