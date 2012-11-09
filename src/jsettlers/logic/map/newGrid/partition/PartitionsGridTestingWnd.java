package jsettlers.logic.map.newGrid.partition;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.normal.HexAStar;
import jsettlers.logic.algorithms.path.test.DummyEmptyAStarMap;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;
import synchronic.timer.NetworkTimer;

public class PartitionsGridTestingWnd extends JFrame {
	private static final Color[] partitionColors = { Color.ORANGE, Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.LIGHT_GRAY, Color.DARK_GRAY,
			Color.MAGENTA, Color.PINK, Color.YELLOW };
	private static final int X_OFFSET = 30;
	private static final int Y_OFFSET = 170;

	private final short BLOCK_SIZE = 60;

	private static final short HEIGHT = 3;
	private static final short WIDTH = 3;

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;
	private JButton btnRepaint;

	private PartitionsGrid partitionsGrid;
	private DummyEmptyAStarMap aStarMap;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		NetworkTimer.get().schedule();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final PartitionsGridTestingWnd frame = new PartitionsGridTestingWnd();
				frame.setVisible(true);

				new Thread(new Runnable() {
					@Override
					public void run() {
						initiateTests(frame);
					}
				}).start();

			}
		});
	}

	private static void initiateTests(PartitionsGridTestingWnd frame) {
		PartitionsGrid partitionsGrid = frame.partitionsGrid;

		changePlayerAt(partitionsGrid, 1, 0, 0);
		changePlayerAt(partitionsGrid, 2, 2, 0);
		changePlayerAt(partitionsGrid, 1, 2, 0);
		changePlayerAt(partitionsGrid, 2, 0, 0);

		partitionsGrid.pushMaterial(new ShortPoint2D(2, 0), EMaterialType.PLANK);
		partitionsGrid.pushMaterial(new ShortPoint2D(2, 0), EMaterialType.PLANK);
		partitionsGrid.pushMaterial(new ShortPoint2D(2, 0), EMaterialType.PLANK);

		partitionsGrid.request(new TestMaterialRequester(2, 0), EMaterialType.STONE, (byte) 1);
		partitionsGrid.request(new TestMaterialRequester(2, 0), EMaterialType.STONE, (byte) 1);
		partitionsGrid.request(new TestMaterialRequester(2, 0), EMaterialType.STONE, (byte) 1);
		partitionsGrid.request(new TestMaterialRequester(2, 0), EMaterialType.STONE, (byte) 1);

		frame.aStarMap.setBlocked(1, 1, true);

		changePlayerAt(partitionsGrid, 1, 1, 0);

		System.out.println("--------------------------(expected nothing)");

		changePlayerAt(partitionsGrid, 0, 1, 0);

		System.out.println("--------------------------(expected nothing)");

		changePlayerAt(partitionsGrid, 0, 0, 0);

		System.out.println();

	}

	private static void changePlayerAt(PartitionsGrid partitionsGrid, int x, int y, int playerId) {
		partitionsGrid.changePlayerAt((short) x, (short) y, partitionsGrid.getPlayerForId((byte) playerId));
	}

	/**
	 * Create the frame.
	 */
	public PartitionsGridTestingWnd() {
		aStarMap = new DummyEmptyAStarMap(WIDTH, HEIGHT) {
			@Override
			public boolean isBlocked(IPathCalculateable requester, short x, short y) {
				return super.isBlocked(requester, x, y) || requester != null && partitionsGrid.getPlayerIdAt(x, y) != requester.getPlayerId();
			}
		};
		IPartitionableGrid partitionableGrid = new IPartitionableGrid() {
			@Override
			public boolean isBlocked(short x, short y) {
				return aStarMap.isBlocked(null, x, y);
			}

			@Override
			public void changedPartitionAt(short x, short y) {
				// TODO Auto-generated method stub
			}

			@Override
			public void setDebugColor(short x, short y, jsettlers.common.Color color) {
				// TODO Auto-generated method stub
			}
		};

		partitionsGrid = new PartitionsGrid(WIDTH, HEIGHT, (byte) 10, partitionableGrid);
		partitionsGrid.initPartitionsAlgorithm(new HexAStar(aStarMap, WIDTH, HEIGHT));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
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

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		Graphics2D g = (Graphics2D) graphics;
		g.translate(0, 300);

		for (short x = 0; x < WIDTH; x++) {
			for (short y = 0; y < HEIGHT; y++) {
				short partition = partitionsGrid.getPartitionAt(x, y);
				int drawX = x * BLOCK_SIZE + X_OFFSET + (HEIGHT - y) * BLOCK_SIZE / 2;
				int drawY = -(HEIGHT - y) * BLOCK_SIZE + Y_OFFSET;
				if (partition >= 0) {
					g.setColor(partitionColors[partition]);
					g.fillRect(drawX, drawY, BLOCK_SIZE, -BLOCK_SIZE);
				} else {
					g.setColor(Color.BLACK);
					g.drawRect(drawX, drawY, BLOCK_SIZE, -BLOCK_SIZE);
				}

				g.setColor(Color.WHITE);
				g.drawString(x + "|" + y, drawX + BLOCK_SIZE / 2, drawY - BLOCK_SIZE / 2);
			}
		}
	}

	private static class TestMaterialRequester implements IMaterialRequester {

		private final ShortPoint2D pos;

		TestMaterialRequester(int x, int y) {
			this.pos = new ShortPoint2D(x, y);
		}

		@Override
		public ShortPoint2D getPos() {
			return pos;
		}

		@Override
		public boolean isDiggerRequestActive() {
			return true;
		}

		@Override
		public void requestFailed() {
			// TODO Auto-generated method stub

		}

	}
}
