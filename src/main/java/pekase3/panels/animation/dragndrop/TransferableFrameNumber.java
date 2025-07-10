package pekase3.panels.animation.dragndrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TransferableFrameNumber implements Transferable {
    protected static final DataFlavor frameNumberFlavor = new DataFlavor(Integer.class, "A frame number");
    protected static final DataFlavor frameImageFlavor = new DataFlavor(BufferedImage.class, "A frame number");
    protected static final DataFlavor[] supportedFlavors = {
            frameNumberFlavor,
            frameImageFlavor,
    };
    
    private int frameNumber;
    
    public TransferableFrameNumber(int frame) {
        this.frameNumber = frame;
    }

    @Deprecated    
    public TransferableFrameNumber(BufferedImage image) {
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(frameNumberFlavor);
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(frameNumberFlavor)) {
            return frameNumber;
        } else if (flavor.equals(frameImageFlavor)) {
            return frameImageFlavor;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
