/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.random.visualize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.instructions.BuildingInstruction;
import jsettlers.logic.map.random.instructions.LandBaseInstruction;
import jsettlers.logic.map.random.instructions.ObjectInstruction;
import jsettlers.logic.map.random.instructions.PlayerBaseInstruction;
import jsettlers.logic.map.random.instructions.PlayerObjectInstruction;
import jsettlers.logic.map.random.instructions.SettlerInstruction;
import jsettlers.logic.map.random.instructions.StackInstruction;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshEdge;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;
import jsettlers.logic.map.random.landscape.MeshSite;
import jsettlers.logic.map.random.landscape.Vertex;

public class LandscapeVisualization extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = -3683139071583704506L;
	private final LandscapeMesh mesh;
	private int height;

	LandscapeVisualization(LandscapeMesh mesh) {
		super();
		this.mesh = mesh;
		height = mesh.getHeight();
		setPreferredSize(new Dimension(mesh.getWidth(), height));
		setMinimumSize(new Dimension(mesh.getWidth(), height));
		setMaximumSize(new Dimension(mesh.getWidth(), height));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (MeshSite site : mesh.getSites()) {
			g2d.setColor(landscapeColor(site.getLandscape()));
			g2d.fill(getShape(site));
		}

		g2d.setColor(Color.GRAY);
		for (MeshEdge edge : mesh.getEdges()) {
			g2d.drawLine((int) edge.getStart().getX(), (int) edge.getStart()
					.getY(), (int) edge.getEnd().getX(), (int) edge.getEnd()
					.getY());
		}
	}

	private Shape getShape(MeshSite site) {
		MeshEdge[] edges = site.getEdges();
		if (edges.length > 0) {
			GeneralPath polygon = null;
			// TODO: cache
			if (polygon == null) {
				polygon = new GeneralPath();
				Vertex last = edges[edges.length - 1].getClockPoint(site);
				polygon.moveTo(last.getX(), last.getY());
				for (MeshEdge edge : edges) {
					Vertex point = edge.getClockPoint(site);
					polygon.lineTo(point.getX(), point.getY());
				}
				polygon.closePath();
			}
			return polygon;
		} else {
			throw new IllegalStateException("The edge was not initialized yet");
		}
	}

	public static Color landscapeColor(MeshLandscapeType landscape) {
		switch (landscape) {
		case GRASS:
			return new Color(0.133f, 0.545f, 0.133f);

		case MOUNTAIN:
			return Color.DARK_GRAY;

		case SEA:
			return Color.BLUE;

		case SAND:
			return Color.YELLOW;

		case DESERT:
			return new Color(200, 255, 10);

		default:
		case UNSPECIFIED:
			return Color.LIGHT_GRAY;
		}
	}

	public static void main(String[] args) {
		LandscapeMesh mesh =
				LandscapeMesh.getRandomMesh(1000, 1000, new Random());

		PlayerStart[] starts =
				new PlayerStart[] {
						new PlayerStart(100, 100, (byte) 1, (byte) 1),
						new PlayerStart(100, 900, (byte) 2, (byte) 1),
						new PlayerStart(900, 900, (byte) 3, (byte) 2),
						new PlayerStart(900, 100, (byte) 4, (byte) 2)
				};
		LandBaseInstruction base = new LandBaseInstruction();
		base.setParameter("type", "sea");
		base.execute(mesh, starts, new Random());

		PlayerBaseInstruction instr = new PlayerBaseInstruction();
		instr.setParameter("size", "10000000");
		instr.setParameter("distance", "0-300");
		instr.setParameter("type", "grass");
		instr.setParameter("fix", "false");
		instr.execute(mesh, starts, new Random());

		instr = new PlayerBaseInstruction();
		instr.setParameter("size", "10000");
		instr.setParameter("distance", "0-100");
		instr.setParameter("fix", "true");
		instr.setParameter("on", "sand");
		instr.execute(mesh, starts, new Random());

		instr = new PlayerBaseInstruction();
		instr.setParameter("type", "mountain");
		instr.setParameter("size", "400");
		instr.setParameter("distance", "50-200");
		instr.setParameter("on", "grass");
		instr.execute(mesh, starts, new Random());

		instr = new PlayerBaseInstruction();
		instr.setParameter("type", "mountain");
		instr.setParameter("size", "1000");
		instr.setParameter("distance", "50-200");
		instr.setParameter("on", "grass");
		instr.execute(mesh, starts, new Random());

		JFrame frame = new JFrame("mesh");
		frame.getContentPane().add(new LandscapeVisualization(mesh));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		MapGrid grid = MapGrid.createFromLandscapeMesh(mesh, new Random(), new ShortPoint2D[] {});

		ObjectInstruction objinstr = new BuildingInstruction();
		objinstr.setParameter("distance", "0");
		objinstr.setParameter("type", "TOWER");
		objinstr.execute(grid, starts, new Random());

		objinstr = new SettlerInstruction();
		objinstr.setParameter("distance", "10");
		objinstr.setParameter("dy", "10");
		objinstr.setParameter("count", "10");
		objinstr.setParameter("type", "BEARER");
		objinstr.execute(grid, starts, new Random());

		objinstr = new StackInstruction();
		objinstr.setParameter("distance", "10");
		objinstr.setParameter("type", "STONE");
		objinstr.setParameter("capacity", "8");
		objinstr.execute(grid, starts, new Random());

		objinstr = new StackInstruction();
		objinstr.setParameter("distance", "15");
		objinstr.setParameter("type", "PLANK");
		objinstr.setParameter("capacity", "8");
		objinstr.execute(grid, starts, new Random());

		objinstr = new PlayerObjectInstruction();
		objinstr.setParameter("distance", "30");
		objinstr.execute(grid, starts, new Random());

		objinstr = new PlayerObjectInstruction();
		objinstr.setParameter("distance", "20");
		objinstr.setParameter("capacity", "1..10");
		objinstr.setParameter("type", "stone");
		objinstr.setParameter("dx", "5..15");
		objinstr.setParameter("dy", "5..15");
		objinstr.execute(grid, starts, new Random());

		JFrame frame2 = new JFrame("grid");
		frame2.getContentPane().add(new MapGridVisualization(grid));
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.pack();
		frame2.setVisible(true);
	}
}
