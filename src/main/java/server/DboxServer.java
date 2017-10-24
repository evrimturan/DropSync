package server;


import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DboxServer {

    protected static final String ACCESS_TOKEN = "EhQ82r3iuoAAAAAAAAAAC0AH95s0LShJsQHUMoJ2McvQc9Z7Q6MdPi4zOYbTnbIb";
    public static final String PATH_OF_SERVER = "serverdata/";

    public static void main(String[] args) {
        SocketManager x = new SocketManager();
    }

    private static List<String> fetchFromDropbox(){
        List<String> ret = new ArrayList<>();
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        ListFolderResult result;
        try {
            result = client.files().listFolder("/Apps/DropSync");
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    ret.add(metadata.getPathLower());
                }
                if (!result.getHasMore()) {
                    break;
                }
                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return ret;
    }


}
