import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import java.awt.Toolkit;


public class RiskPlus {

	private static final double AVERAGE_SIX_SIDED = 3.5;
	private static final double SIX_SIDED_STANDARD_DEVIATION = 1.87083;
	private static final double DEFENCE_BONUS = 0.5;
	
	private static final double SIZE_BONUS = 0.7;
	
	private JFrame frmRisk;

	private Random rand;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RiskPlus window = new RiskPlus();
					window.frmRisk.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RiskPlus() {
		rand = new Random();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRisk = new JFrame();
		frmRisk.setIconImage(Toolkit.getDefaultToolkit().getImage(RiskPlus.class.getResource("/Riskplusplusv2.png")));
		frmRisk.setTitle("Risk++");
		frmRisk.setBounds(100, 100, 450, 300);
		frmRisk.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRisk.getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][][][][grow,fill][]"));
		
		JLabel lblAttacking = new JLabel("Attacking");
		frmRisk.getContentPane().add(lblAttacking, "cell 0 0,alignx center");
		
		JLabel lblDefending = new JLabel("Defending");
		frmRisk.getContentPane().add(lblDefending, "cell 1 0,alignx center");
		
		JSpinner spnAttackingArmy = new JSpinner();
		spnAttackingArmy.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		frmRisk.getContentPane().add(spnAttackingArmy, "cell 0 1,growx");
		
		JSpinner spnDefendingArmy = new JSpinner();
		spnDefendingArmy.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		frmRisk.getContentPane().add(spnDefendingArmy, "cell 1 1,growx");
		
		JButton btnFight = new JButton("Fight");
		frmRisk.getContentPane().add(btnFight, "cell 0 2");
		
		JLabel lblAttackingZScore = new JLabel("Attacking ZScore");
		frmRisk.getContentPane().add(lblAttackingZScore, "cell 0 3,alignx center");
		
		JLabel lblDefendingzscore = new JLabel("DefendingZScore");
		frmRisk.getContentPane().add(lblDefendingzscore, "cell 1 3,alignx center");
		
		JScrollPane scrollPane = new JScrollPane();
		frmRisk.getContentPane().add(scrollPane, "cell 0 4,grow");
		
		JList<Integer> listAttackingRolls = new JList<Integer>();
		scrollPane.setViewportView(listAttackingRolls);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		frmRisk.getContentPane().add(scrollPane_1, "cell 1 4,grow");
		
		JList<Integer> listDefendingRolls = new JList<Integer>();
		scrollPane_1.setViewportView(listDefendingRolls);
		
		JLabel lblAttackinglosses = new JLabel("AttackingLosses");
		frmRisk.getContentPane().add(lblAttackinglosses, "cell 0 5,alignx center");
		
		JLabel lblDefendinglosses = new JLabel("DefendingLosses");
		frmRisk.getContentPane().add(lblDefendinglosses, "cell 1 5,alignx center");
		
		btnFight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int attackingArmySize = (int)spnAttackingArmy.getValue();
				int defendingArmySize = (int)spnDefendingArmy.getValue();
				Integer[] attackingRolls = rollDice(attackingArmySize);
				Integer[] defendingRolls = rollDice(defendingArmySize);
				double attackingZScore = generateZScore(attackingRolls);
				double defendingZScore = generateZScore(defendingRolls);
				
				int sizeDifference = attackingArmySize - defendingArmySize;
				if(sizeDifference < 0) {
					defendingZScore += SIZE_BONUS * ((int)Math.abs(sizeDifference) / 2);
				} else if(sizeDifference > 0) {
					attackingZScore += SIZE_BONUS * ((int)Math.abs(sizeDifference) / 2);
				}
				defendingZScore += DEFENCE_BONUS;
				
				listAttackingRolls.setListData(attackingRolls);
				listDefendingRolls.setListData(defendingRolls);
				
				lblAttackingZScore.setText("Z Score: " + attackingZScore);
				lblDefendingzscore.setText("Z Score: " + defendingZScore);
				
				int attackingLosses = 0;
				int defendingLosses = 0;
				
				double zScoreDifference = Math.max(defendingZScore, attackingZScore) - Math.min(defendingZScore, attackingZScore);
				if(zScoreDifference < 0.5) {
					// 50% loss both
					attackingLosses = (int)Math.ceil(attackingArmySize * 0.5);
					defendingLosses = (int)Math.ceil(defendingArmySize * 0.5);
				} else if(zScoreDifference < 1.0) {
					// 70% loser
					// 30% winner
					if(defendingZScore > attackingZScore) {
						attackingLosses = (int)Math.ceil(attackingArmySize * 0.7);
						defendingLosses = (int)Math.ceil(defendingArmySize * 0.3);
					} else {
						attackingLosses = (int)Math.ceil(attackingArmySize * 0.3);
						defendingLosses = (int)Math.ceil(defendingArmySize * 0.7);
					}
				} else if(zScoreDifference < 1.5) {
					// 90% loser
					if(defendingZScore > attackingZScore) {
						attackingLosses = (int)Math.ceil(attackingArmySize * 0.9);
						defendingLosses = (int)Math.ceil(defendingArmySize * 0.1);
					} else {
						attackingLosses = (int)Math.ceil(attackingArmySize * 0.1);
						defendingLosses = (int)Math.ceil(defendingArmySize * 0.9);
					}
				} else {
					// 100% loss
					if(defendingZScore > attackingZScore) {
						attackingLosses = attackingArmySize;
						defendingLosses = 0;
					} else {
						attackingLosses = 0;
						defendingLosses = defendingArmySize;
					}
				}
				lblAttackinglosses.setText("Losses: " + attackingLosses);
				lblDefendinglosses.setText("Losses: " + defendingLosses);
			}
		});
	}
	
	private Integer[] rollDice(int numDice) {
		Integer[] dice = new Integer[numDice];
		for(int i = 0; i < numDice; i++) {
			dice[i] = rand.nextInt(6) + 1;
		}
		return dice;
	}
	
	private double generateZScore(Integer[] rolls) {
		int sum = 0;
		for(int i = 0; i < rolls.length; i++) {
			sum += rolls[i];
		}
		double average = (double)sum / (double)rolls.length;
		return (average - AVERAGE_SIX_SIDED) / SIX_SIDED_STANDARD_DEVIATION;
	}
}
