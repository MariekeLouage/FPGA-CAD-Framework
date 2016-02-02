package placers.analytical;

import java.util.List;

import circuit.Circuit;
import circuit.architecture.BlockCategory;
import circuit.architecture.BlockType;

abstract class Legalizer {

    protected Circuit circuit;
    protected int width, height;

    private List<BlockType> blockTypes;
    private List<Integer> blockTypeIndexStarts;
    private int numBlocks, numIOBlocks;

    protected double tileCapacity;

    protected double[] linearX, linearY;
    protected int[] legalX, legalY;
    protected int[] heights;

    // Properties of the blockType that is currently being legalized
    protected BlockType blockType;
    protected BlockCategory blockCategory;
    protected int blockStart, blockRepeat, blockHeight;

    Legalizer(
            Circuit circuit,
            List<BlockType> blockTypes,
            List<Integer> blockTypeIndexStarts,
            double[] linearX,
            double[] linearY,
            int[] heights) {

        // Store easy stuff
        this.circuit = circuit;
        this.width = this.circuit.getWidth();
        this.height = this.circuit.getHeight();

        // Store block types
        if(blockTypes.get(0).getCategory() != BlockCategory.IO) {
            throw new IllegalArgumentException("The first block type is not IO");
        }
        if(blockTypes.size() != blockTypeIndexStarts.size() - 1) {
            throw new IllegalArgumentException("The objects blockTypes and blockTypeIndexStarts don't have matching dimensions");
        }

        this.blockTypes = blockTypes;
        this.blockTypeIndexStarts = blockTypeIndexStarts;

        // Store linear solution (this array is updated by the linear solver
        this.linearX = linearX;
        this.linearY = linearY;
        this.heights = heights;

        // Cache the number of blocks
        this.numBlocks = linearX.length;
        this.numIOBlocks = blockTypeIndexStarts.get(1);

        // Initialize the solution with IO positions
        this.legalX = new int[this.numBlocks];
        this.legalY = new int[this.numBlocks];

        for(int i = 0; i < this.numIOBlocks; i++) {
            this.legalX[i] = (int) this.linearX[i];
            this.legalY[i] = (int) this.linearY[i];
        }
    }

    Legalizer(Legalizer legalizer) {
        this.circuit = legalizer.circuit;
        this.width = legalizer.width;
        this.height = legalizer.height;

        this.blockTypes = legalizer.blockTypes;
        this.blockTypeIndexStarts = legalizer.blockTypeIndexStarts;

        this.linearX = legalizer.linearX;
        this.linearY = legalizer.linearY;
        this.legalX = legalizer.legalX;
        this.legalY = legalizer.legalY;
        this.heights = legalizer.heights;

        this.numBlocks = legalizer.numBlocks;
        this.numIOBlocks = legalizer.numIOBlocks;
    }


    protected abstract void legalizeBlockType(double tileCapacity, int blocksStart, int blocksEnd);

    void legalize(double tileCapacity) {
        this.tileCapacity = tileCapacity;

        // Skip i = 0: these are IO blocks
        for(int i = 1; i < this.blockTypes.size(); i++) {
            this.blockType = this.blockTypes.get(i);
            int blocksStart = this.blockTypeIndexStarts.get(i);
            int blocksEnd = this.blockTypeIndexStarts.get(i + 1);

            if(blocksEnd > blocksStart) {
                this.blockCategory = this.blockType.getCategory();

                this.blockStart = Math.max(1, this.blockType.getStart());
                this.blockHeight = this.blockType.getHeight();
                this.blockRepeat = this.blockType.getRepeat();
                if(this.blockRepeat == -1) {
                    this.blockRepeat = this.width;
                }

                legalizeBlockType(tileCapacity, blocksStart, blocksEnd);
            }
        }
    }



    int[] getLegalX() {
        return this.legalX;
    }
    int[] getLegalY() {
        return this.legalY;
    }
}