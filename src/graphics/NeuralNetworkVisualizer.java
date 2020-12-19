package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;


import simulation.Agent;
import utils.Matrix;
import utils.NeuralNetwork;

public class NeuralNetworkVisualizer extends OverlayPanel{
	
	private final Color NODE_ACTIVE = Color.green;
	private final Color NODE_INACTIVE = Color.white;
	private final Color POSITIVE_WEIGHT = Color.blue;
	private final Color NEGATIVE_WEIGHT = Color.red;
	private final Color OUTLINE = Color.black;
	
	private static int nodeSize = 20;
	
	public NeuralNetworkVisualizer(Panel p, Dimension d) {
		super(p, d);
		this.borderBuffer = 20;
	}
	
	// draw a node
	private void drawNode(Graphics g, Color color, int x, int y) {
		g.setColor(color);
		g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
		g.setColor(OUTLINE);
		g.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
	}
	
	// draw line with a set color
	private void drawLine(Graphics g, Color color, int x1, int y1, int x2, int y2) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}
	
	// calculate color for a connection weight
	private Color calculateColor(float weight) {
		if(weight >= 0) {
			return POSITIVE_WEIGHT;
		} else {
			return NEGATIVE_WEIGHT;
		}
	}
	
	// calculate the color of a node based on activation 
	private Color calculateNodeColor(float input) {
		if(input > Agent.networkThreshold) {
			return NODE_ACTIVE;
		} else {
			return NODE_INACTIVE;
		}
	}
	
//	private Color calculateOutputNodeColor(int index, Matrix outputArray) {
//		List<Float> list = outputArray.toArray();
//		
//		int maxAt = 0;
//
//		for (int i = 0; i < list.size(); i++) {
//		    maxAt = list.get(i) > list.get(maxAt) ? i : maxAt;
//		}
//		
//		if(index == maxAt) {
//			return NODE_ACTIVE;
//		} else {
//			return NODE_INACTIVE;
//		}
//	}

	// draw the connection lines
	private void drawLines(Graphics g, NeuralNetwork nn) {
		int[] structure = nn.getStructure();
		int widthGap = this.partition(this.dimension.width - (2 * borderBuffer), structure.length - 1);
		
		int maxLayerSize = 0;
		for(int i = 0; i < structure.length; i++) {
			if(structure[i] > maxLayerSize) {
				maxLayerSize = structure[i];
			}
		}
		
		int standardPartition = this.partition(this.dimension.height - (2 * borderBuffer), maxLayerSize - 1);
		
		for(int i = 0; i < structure.length - 1; i++) {
			
			for(int j = 0; j < structure[i]; j++) {
				for(int k = 0; k < structure[i + 1]; k++) {
					Color color = this.calculateColor(nn.getWeight(i, k, j));
					this.drawLine(g, color, borderBuffer + this.x + i * widthGap, (int)((maxLayerSize - structure[i])/ 2.0 * standardPartition) + borderBuffer + this.y + j * standardPartition,
											borderBuffer + this.x + (i + 1) * widthGap, (int)((maxLayerSize - structure[i+1])/ 2.0 * standardPartition) + borderBuffer + this.y + k * standardPartition);
				}
			}
			
		}
	}
	
	// draw the nodes
	private void drawNodes(Graphics g, NeuralNetwork nn) {
		int[] structure = nn.getStructure();
		int widthGap = this.partition(this.dimension.width - (2 * borderBuffer), structure.length - 1);
		
		// find the max layer size 
		int maxLayerSize = 0;
		for(int i = 0; i < structure.length; i++) {
			if(structure[i] > maxLayerSize) {
				maxLayerSize = structure[i];
			}
		}
		
		// get the node values on a forward pass
		Matrix inputLayer = this.panel.getSelectedAgent().getInputLayer();
		List<Matrix> values = nn.propForwardValues(inputLayer);
		
		int standardPartition = this.partition(this.dimension.height - (2 * borderBuffer), maxLayerSize - 1);
		for(int i = 0; i < structure.length; i++) {
			//int layerHeightGap = this.partition(this.dimension.height - (2 * borderBuffer), structure[i]);
			
			for(int j = 0; j < structure[i]; j++) {			
				// select color based on the activation of the node
				Color color = this.calculateNodeColor(values.get(i).getWeight(j, 0));
				
				this.drawNode(g, color,  borderBuffer + this.x + i * widthGap, 
								(int)((maxLayerSize - structure[i])/ 2.0 * standardPartition) + borderBuffer + this.y + j * standardPartition);
			
			}
		}
	}
	
	// draw the entire panel
	private void drawAgentNeuralNet(Graphics g, Agent agent) {
		if(agent != null && this.panel.getTrackingID() != -1) {
			NeuralNetwork nn = agent.getNeuralNetwork();
			drawOutline(g, OUTLINE);
			drawLines(g, nn);
			drawNodes(g, nn);
		}
	}
	
	public void clicked(int x, int y) {
		return;
	}
	
	@Override
	public void render(Graphics g) {
		drawAgentNeuralNet(g, this.panel.getSelectedAgent());
	}
}
