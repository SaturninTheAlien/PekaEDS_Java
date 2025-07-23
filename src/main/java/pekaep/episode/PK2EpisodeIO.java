package pekaep.episode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pk2.filesystem.PK2FileSystem;

public class PK2EpisodeIO {

    private static void zipAsset(PK2EpisodeAsset asset, ZipOutputStream zipOut, PK2Episode episode) throws IOException {

        if(!asset.isGood()){
            throw new FileNotFoundException("Asset \""+asset.getName()+"\" not found or corrupted!");
        }

        String entry;
        if(asset.getType().getDir()==null){
            entry = PK2FileSystem.EPISODES_DIR + "/"+ episode.getName() + "/" + asset.getName();
        }
        else{
            entry = asset.getType().getDir() + "/" + asset.getName();
        }

        ZipEntry zipEntry = new ZipEntry(entry);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = Files.readAllBytes(asset.file.toPath());
        zipOut.write(bytes, 0, bytes.length);

    }


    public static void saveZip(PK2Episode episode, File zipFile, boolean ignoreVanillaAssets) throws IOException{
        FileOutputStream out = new FileOutputStream(zipFile);
        
        ZipOutputStream zipOut = new ZipOutputStream(out);
        for(PK2EpisodeAsset asset: episode.getAssetList()){
            if(asset.file==null)continue;

            if(!asset.isVanillaAsset() || !ignoreVanillaAssets){
                zipAsset(asset, zipOut, episode);
            }
        }
        zipOut.close();
    }
}
