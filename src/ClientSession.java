import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Logger;


public class ClientSession implements Runnable {

    private static Logger log = Logger.getLogger(ClientSession.class.getName());

    private String paramName;
    private String paramSurname;
    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    @Override
    public void run() {
        try {
            String header = readHeader();
            log.info(header + "\n");

            String operation = header.split(" ")[0];
            String url = getURIFromHeader(header);
            DB getUserById = new DB();
            boolean result = false;
            switch (operation) {
                case "GET":
                    log.info("Resource: " + url + "\n");
                    String strm = getUserById.getUserByID(Integer.parseInt(url));
                    result = (strm == null) ? false : true;
                    int code = send(result, strm);
                    log.info("Result code: " + code + "\n");
                    break;

                case "POST":
                    strm = getUserById.saveNewUser(paramName, paramSurname);
                    result = (strm != null || strm != "") ? true : false;
                    code = send(result);
                    log.info("Result code: " + code + "\n");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        initialize();
    }

    private void initialize() throws IOException {
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;

        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }

            if (ln.contains("name")) {
                paramName = ln.substring(ln.indexOf(':') + 1, ln.length());
            }
            if (ln.contains("surname")) {
                paramSurname = ln.substring(ln.indexOf(':') + 1, ln.length());
            }

            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    private String getURIFromHeader(String header) {
        int from = header.indexOf(" ") + 2;
        int to = header.indexOf(" ", from);
        String uri = header.substring(from, to);
        int paramIndex = uri.indexOf("?");
        if (paramIndex != -1) {
            uri = uri.substring(0, paramIndex);
        }
        return uri;
    }

    private int send(boolean res) throws IOException {
        int code = res ? 200 : 404;
        String header = getHeader(code);
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(header);
        if (code == 200) {
            socket.getOutputStream().write("Everything is good".getBytes("UTF-8"));
        }
        return code;
    }

    private int send(boolean res, String userData) throws IOException {
        int code = res ? 200 : 404;
        String header = getHeader(code);
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(header);
        if (code == 200) {
            socket.getOutputStream().write(userData.getBytes("UTF-8"));
        }
        else socket.getOutputStream().write("No such user".getBytes("UTF-8"));
        return code;
    }

    private String getHeader(int code) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1 " + code + " " + getAnswer(code) + "\n");
        buffer.append("Date: " + new Date().toString() + "\n");
        buffer.append("Accept-Ranges: none\n");
        buffer.append("Content-Type: " + "\n");
        buffer.append("\n");
        return buffer.toString();
    }

    private String getAnswer(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }
}