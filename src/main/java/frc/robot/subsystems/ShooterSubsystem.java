package frc.robot.subsystems;

import frc.robot.Constants;
import frc.robot.calibrations.K_SHOT;
import frc.robot.subsystems.RFSLIB;


import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShooterSubsystem extends SubsystemBase {

public enum slctMtr {
    Left,
    Right;
}

private WPI_TalonFX[] ShooterMotor = new WPI_TalonFX[] {
  new WPI_TalonFX(Constants.SHOOTER_MTR_LT, "rio"),
  new WPI_TalonFX(Constants.SHOOTER_MTR_RT, "rio")  
};

Spark ShooterServoAim = new Spark(Constants.PWM_SHOOTER_ANGLE);



  /********************************/
  /* ShooterSubsystem Constructor */
  /********************************/
  public ShooterSubsystem() {




    /*****************************************************************/
    /* Shooter Motor Controller Configurations                       */
    /*****************************************************************/
    for (int i = 0; i < 2; i++) {
      ShooterMotor[i].configFactoryDefault();
      ShooterMotor[i].setSensorPhase(false);
      ShooterMotor[i].setNeutralMode(NeutralMode.Brake);
      ShooterMotor[i].config_kP(0, K_SHOT.KeSHOT_K_Prop);
      ShooterMotor[i].config_kI(0, K_SHOT.KeSHOT_K_Intgl);
      ShooterMotor[i].config_kD(0, K_SHOT.KeSHOT_K_Deriv);
      ShooterMotor[i].config_IntegralZone(0, K_SHOT.KeSHOT_r_IntglErrMaxEnbl);
      ShooterMotor[i].config_kF(0, K_SHOT.KeSHOT_K_FdFwd);      
    }
	
    ShooterMotor[0].setInverted(true);			  //Constants.SHOOTER_MTR_LT
    ShooterMotor[1].setInverted(false);			  //Constants.SHOOTER_MTR_RT
    ShooterMotor[1].follow(ShooterMotor[0]);  //Constants.SHOOTER_MTR_RT Slave to Constants.SHOOTER_MTR_LT

    ShooterServoAim.set(0);

  }




  /**
   * Method: getShooterMtr - Shooter Drive System - Gets the Master Shooter Motor Object 
   * @return ShooterMotor[Master]; (WPI_TalonFX: Shooter Motor Object)
   */  
  public WPI_TalonFX getShooterMtr() {
    return ShooterMotor[0];
  }

  public double getSpd(){
    return getShooterMtr().getSelectedSensorVelocity();
  }

  public boolean isShooterAtSpd(){
    return Math.abs(K_SHOT.KeSHOT_n_TgtLaunchCmd - getSpd()) < K_SHOT.KeSHOT_n_AtTgtDB;
  }

  public void runShooterAtSpd(double speed) {
    getShooterMtr().set(TalonFXControlMode.Velocity,speed);
  }


  public void runShooterAtPwr(double power) {
    // getShooterMtr().set(TalonFXControlMode.PercentOutput,power);
    ShooterMotor[0].set(TalonFXControlMode.PercentOutput,power);
    ShooterMotor[1].set(TalonFXControlMode.PercentOutput,power);
  }

  public void stopMotor() {
    getShooterMtr().disable();
  }


  public void pidShooterSpd(boolean activate){
    if (activate == true) {
      getShooterMtr().set(TalonFXControlMode.Velocity,K_SHOT.KeSHOT_n_TgtLaunchCmd);
    } else {
      getShooterMtr().set(TalonFXControlMode.Velocity,0);
    }
  }

  
  
  public double dtrmnShooterServoCmd(double distToTgt, boolean highTgt) {
    float percentServoCmd;
    float axisLookUp;
      if (highTgt) {
        axisLookUp = RFSLIB.AxisPieceWiseLinear_flt((float)distToTgt, K_SHOT.KnSHOT_l_LaunchServoAxisHi, (int)10);   
        percentServoCmd = RFSLIB.XY_Lookup_flt(K_SHOT.KtSHOT_Pct_LaunchServoCmdHi, axisLookUp, (int)10);
      } else {
        axisLookUp = RFSLIB.AxisPieceWiseLinear_flt((float)distToTgt, K_SHOT.KnSHOT_l_LaunchServoAxisLo, (int)10);   
        percentServoCmd = RFSLIB.XY_Lookup_flt(K_SHOT.KtSHOT_Pct_LaunchServoCmdLo, axisLookUp, (int)10);
      }          
    return (double)percentServoCmd;
  }



  /**
   * Method: aimShooter - Shooter Drive System - Used to Aim the Shooter by sending
   * a Percent to the Servo to Deflect the Shooter Mechanism Value (0 to 100%) 
   * @return ShooterMotor[Master]; (WPI_TalonFX: Shooter Motor Object)
   */  
  public void aimShooter(double percent) {
    ShooterServoAim.set(percent/100);
  }



  public void init_periodic() {} 

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

}
