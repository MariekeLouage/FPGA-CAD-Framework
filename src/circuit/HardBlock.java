package circuit;

import java.util.Vector;

public class HardBlock extends Block
{
	
	private Pin[] outputs;
	private Pin[] inputs;
	private boolean isClockEdge;
	private String typeName;
	
	public HardBlock(String name, Vector<String> outputNames, Vector<String> inputNames, String typeName, boolean isClockEdge)
	{
		super(name, BlockType.HARDBLOCK_UNCLOCKED);
		this.typeName = typeName;
		this.isClockEdge = isClockEdge;
		if(isClockEdge)
		{
			this.type = BlockType.HARDBLOCK_CLOCKED;
		}
		
		int nbOutputs = outputNames.size();
		outputs = new Pin[nbOutputs];
		for(int i = 0; i < nbOutputs; i++)
		{
			outputs[i] = new Pin(name + "_HBout_" + outputNames.get(i), PinType.SOURCE, this);
		}
		
		int nbInputs = inputNames.size();
		inputs = new Pin[nbInputs];
		for(int i = 0; i < nbInputs; i++)
		{
			inputs[i] = new Pin(name + "_HBin_" + inputNames.get(i), PinType.SINK, this);
		}
	}
	
	public Pin[] getInputs()
	{
		return inputs;
	}
	
	public Pin[] getOutputs()
	{
		return outputs;
	}
	
	public String getTypeName()
	{
		return typeName;
	}
	
	public boolean getIsClockEdge()
	{
		return isClockEdge;
	}
	
}