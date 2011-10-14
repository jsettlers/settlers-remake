package jsettlers.logic.map.newGrid.partition;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.test.DummyEmptyPathfinderMap;
import synchronic.timer.NetworkTimer;

public class PartitionsGridTestingWnd extends JFrame {
	private static final short HEIGHT = 400;
	private static final short WIDTH = 400;

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;

	PartitionsGrid map;

	/**
	 * Launch the application.
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		NetworkTimer.get().schedule();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					PartitionsGridTestingWnd frame = new PartitionsGridTestingWnd();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PartitionsGridTestingWnd() {
		map = new PartitionsGrid(WIDTH, HEIGHT, new DummyEmptyPathfinderMap(WIDTH, HEIGHT) {
			@Override
			public boolean isBlocked(IPathCalculateable requester, short x, short y) {
				return map.getPlayerAt(x, y) != requester.getPlayer();
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				setPlayer(arg0.getX(), arg0.getY());
			}

		});
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setPlayer(arg0.getX(), arg0.getY());
			}
		});
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setText("0");
		textField.setBounds(628, 25, 46, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblTeam = new JLabel("player");
		lblTeam.setBounds(628, 11, 46, 14);
		contentPane.add(lblTeam);

		btnRepaint = new JButton("repaint");
		btnRepaint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				repaint();
			}
		});
		btnRepaint.setBounds(585, 56, 89, 23);
		contentPane.add(btnRepaint);
	}

	private void setPlayer(int mouseX, int mouseY) {
		short x = (short) (mouseX - 20);
		short y = (short) (this.getHeight() - mouseY - 40);

		if (map.isInBounds(x, y)) {
			byte player = new Byte(textField.getText());
			ISPosition2D position = new ShortPoint2D(x, y);
			map.changePlayerAt(position, player);

			for (ISPosition2D currPos : new MapNeighboursArea(position)) {
				map.changePlayerAt(currPos, player);
			}

			for (ISPosition2D currPos : new HexBorderArea(position, (short) 2)) {
				map.changePlayerAt(currPos, player);
			}
		}
		repaint();
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		Graphics2D g = (Graphics2D) graphics;

		for (short x = 0; x < WIDTH; x++) {
			for (short y = 0; y < HEIGHT; y++) {
				short partition = map.getPartition(x, y);
				if (partition >= 0) {
					g.setColor(partitionColors[partition]);
					g.drawLine(x + 20, this.getHeight() - y - 20, x + 20, this.getHeight() - y - 20);
				}
			}
		}
	}

	private static final Color[] partitionColors = { Color.ORANGE, Color.BLACK, Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.LIGHT_GRAY,
			Color.DARK_GRAY, Color.MAGENTA, Color.PINK, Color.YELLOW };
	private JButton btnRepaint;
}
