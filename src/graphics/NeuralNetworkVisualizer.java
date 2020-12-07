package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;


import simulation.Agent;
import utils.Matrix;
import utils.NeuralNetwork;

public class NeuralNetworkVisualizer {
	
	private static int nodeSize = 20;
	private static int borderBuffer = 20;
	
	private Panel panel;
	private Dimension size;
	private int x;
	private int y;

	public NeuralNetworkVisualizer(Panel p, Dimension size) {
		this.panel = p;
		this.size = size;
	}
	
	private void drawNode(Graphics g, Color color, int x, int y) {
		g.setColor(color);
		g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
	}
	
	private void drawLine(Graphics g, Color color, int x1, int y1, int x2, int y2) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}
	
	private Color calculateColor(float weight) {
		if(weight >= 0) {
			return Color.BLUE;
		} else {
			return Color.RED;
		}
	}
	
	private Color calculateNodeColor(float input) {
		if(input > 0) {
			return Color.GREEN;
		} else {
			return Color.WHITE;
		}
	}
	
	private Color calculateOutputNodeColor(int index, Matrix outputArray) {
		List<Float> list = outputArray.toArray();
		
		int maxAt = 0;

		for (int i = 0; i < list.size(); i++) {
		    maxAt = list.get(i) > list.get(maxAt) ? i : maxAt;
		}
		
		if(index == maxAt) {
			return Color.GREEN;
		} else {
			return Color.WHITE;
		}
	}
	
	private void drawLines(Graphics g, NeuralNetwork nn) {
		int[] structure = nn.getStructure();
		int widthGap = this.partitionWidth(structure.length);
		
		int maxLayerSize = 0;
		for(int i = 0; i < structure.length; i++) {
			if(structure[i] > maxLayerSize) {
				maxLayerSize = structure[i];
			}
		}
		
		int standardPartition = this.partitionHeight(maxLayerSize);
		
		for(int i = 0; i < structure.length - 1; i++) {
			
			int oldLayerHeightGap = this.partitionHeight(structure[i]);
			int newLayerHeightGap = this.partitionHeight(structure[i + 1]);
			
			for(int j = 0; j < structure[i]; j++) {
				for(int k = 0; k < structure[i + 1]; k++) {
					Color color = this.calculateColor(nn.getWeight(i, k, j));
					this.drawLine(g, color, borderBuffer + this.x + i * widthGap, (int)((maxLayerSize - structure[i])/ 2.0 * standardPartition) + borderBuffer + this.y + j * standardPartition,
											borderBuffer + this.x + (i + 1) * widthGap, (int)((maxLayerSize - structure[i+1])/ 2.0 * standardPartition) + borderBuffer + this.y + k * standardPartition);
				}
			}
			
		}
	}
	
	private void drawOutline(Graphics g) {
		this.drawLine(g, Color.black, this.x, this.y, this.x + this.size.width, this.y);
		this.drawLine(g, Color.black, this.x + this.size.width, this.y, this.x + this.size.width, this.y + this.size.height);
		this.drawLine(g, Color.black, this.x + this.size.width, this.y + this.size.height, this.x, this.y + this.size.height);
		this.drawLine(g, Color.black, this.x, this.y + this.size.height, this.x, this.y);
		
	}
	
	private void drawNodes(Graphics g, NeuralNetwork nn) {
		int[] structure = nn.getStructure();
		int widthGap = this.partitionWidth(structure.length);
		
		int maxLayerSize = 0;
		for(int i = 0; i < structure.length; i++) {
			if(structure[i] > maxLayerSize) {
				maxLayerSize = structure[i];
			}
		}
		
		int standardPartition = this.partitionHeight(maxLayerSize);
		
		for(int i = 0; i < structure.length; i++) {
			
			int layerHeightGap = this.partitionHeight(structure[i]);
			
			for(int j = 0; j < structure[i]; j++) {
				Color color;
				if(i == 0) {
					color = this.calculateNodeColor(this.panel.getSelectedAgent().getInputLayer().getWeight(j,0));
				} else if(i == structure.length - 1) {
					color = this.calculateOutputNodeColor(j, this.panel.getSelectedAgent().getOutputLayer());
				} else {
					color = Color.WHITE;
				}
				this.drawNode(g, color,  borderBuffer + this.x + i * widthGap, 
								(int)((maxLayerSize - structure[i])/ 2.0 * standardPartition) + borderBuffer + this.y + j * standardPartition);
			}
		}
	}
	
	private void drawAgentNeuralNet(Graphics g, Agent agent) {
		if(agent != null && this.panel.getTrackingID() != -1) {
			NeuralNetwork nn = agent.getNeuralNetwork();
			drawOutline(g);
			drawLines(g, nn);
			drawNodes(g, nn);
		}
	}
	
	public void draw(Graphics g) {
		drawAgentNeuralNet(g, this.panel.getSelectedAgent());
	}
	
	public int partitionHeight(int numNodes) {
		return (this.size.height - (2 * borderBuffer)) / (numNodes - 1);
	}
	
	public int partitionWidth(int numLayers) {
		return (this.size.width - (2 * borderBuffer)) / (numLayers - 1);
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
}
