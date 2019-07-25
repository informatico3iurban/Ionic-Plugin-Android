package cordova.plugin.utilsandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

//import com.iurban.cvpoll.Models.Mode;
//import com.iurban.cvpoll.Models.Printer;
//import com.iurban.cvpoll.Models.Product;
//import com.iurban.cvpoll.Utils.AsyncInternet;
//import com.iurban.cvpoll.Utils.CustomConstants;
//import com.iurban.cvpoll.Utils.SharedData;
//import com.iurban.cvpoll.Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class AsyncPrintTicket extends AsyncTask {
    private static final String TAG = "HOTEL_DIGITAL";
    short PRINTER_GAPS = 42;
    String TICKET = "TICKET_PRINTER";

    private Context context;
    private PrintWriter printer;

    private boolean connectionSuccessful;
    private String message;
    //private CartIurban cartIurban;
    private String day="";
    private String time="";
    private String location;

    private String ESC = "\u001B";
    private String GS = "\u001D";
    private String RS = "\u001E";

    private String initializePrinter;
    private String characterSet; //change font

    private String center;
    private String left;
    private String right;

    private String cutPaper;
    private String lineJump;
    private String bell;

    private String ip;
    private String port = "-1";

    //public AsyncInternet asyncInternet;
    //private Printer systemPrinter;

    public AsyncPrintTicket(Context context, String message, String ip, String port) {
        this.context = context;
        this.message = message;
        //this.systemPrinter = systemPrinter;
        this.ip = ip;
        this.port = port;
        initConfig();
    }

    public AsyncPrintTicket(Context context, String ip, String port) {
        this.context = context;
        //this.cartIurban = cartIurban;
        this.message = "1";
        //this.systemPrinter = systemPrinter;
        this.ip = ip;
        this.port = port;
        initConfig();
    }

    private void initConfig() {
        /*day = Utils.getDay("/", false);
        time = Utils.getTime(":");
        if (SharedData.getAppType(context) == CustomConstants.TYPE_RESTAURANT) {
            location = SharedData.getTableNumber(context);
        } else {
            location = SharedData.getHotelRoom(context);
        }*/



        initializePrinter = ESC + "@";
        characterSet = ESC + "\u0074" + "\u0013";
        center = ESC + "a" + "\u0001";
        left = ESC + "a" + "\0";
        right = ESC + "a" + "\u0002";
        cutPaper = "\u001B" + "\u006D";
        lineJump = "\u001B" + "\u0064" + "\u0002";
        bell = RS;
    }

    //Method to transform message_id (int) to string
    /*private String messageToString(int mns) {
        String textMessage = "";
        if (mns == (CustomConstants.action_call_waiter)) {
            textMessage = "Ha llamado al camarero";
        } else if (mns == (CustomConstants.action_get_bill_card)) {
            textMessage = "Ha pedido la cuenta con tarjeta";
        } else if (mns == (CustomConstants.action_get_bill_cash)) {
            textMessage = "Ha pedido la cuenta en efectivo";
        }

        return textMessage;
    }*/

    @Override
    protected Object doInBackground(Object[] objects) {
        //Log.d(TAG,"systemprinter "+systemPrinter);
        // Log.d(TAG,"printer ip "+systemPrinter.getPrinterIp());

        if (ip != null && port != "-1") {
            try {
                Socket socket = new Socket();

                socket.connect(new InetSocketAddress( ip, Integer.parseInt(port) ), 1000);
                //socket.connect (new InetSocketAddress("192.168.0.254", 9100), 2500);

                printer = new PrintWriter(socket.getOutputStream());
                printTicket(socket);
                connectionSuccessful = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                connectionSuccessful = false;
            }
        } else {
            connectionSuccessful = false;
        }

        return null;
    }

    private void printTicket(Socket socket) {
        printHeader();
       /* if (message == CustomConstants.action_print_order) {
            printOrder();
        } else {
            printer.println(left + doubleSize(messageToString(message)));
            printer.println("");
        }*/

        //Pitido
        printer.println(bell + bell + bell);

        cutTicket();

        try {
            //printer.flush();
            printer.close();
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

   /* private void printOrder() {
        for (int i = 0; i < Utils.cartIurban.getSize(); i++) {
            Product currentProduct = Utils.cartIurban.getProduct(i);
            if (currentProduct.getPrinter() == systemPrinter.getPrinterId()) {
                String pointsToPrint = getPoint(Utils.cartIurban.getProduct(i).getNameToPrint(context), Utils.cartIurban.getQuantity(i));

                printer.println(doubleSize(Utils.cartIurban.getProduct(i).getNameToPrint(context)) + pointsToPrint + doubleSize(" x ".concat(String.valueOf(Utils.cartIurban.getQuantity(i)))));
                if (!Utils.cartIurban.getProduct(i).getDish().isEmpty()) {
                    printer.print(doubleSize("(".concat(Utils.cartIurban.getProduct(i).getDish()).concat(")")));
                }

                if (!Utils.cartIurban.getProduct(i).getModes().isEmpty()) {
                    printModes(Utils.cartIurban.getProduct(i).getModes(), false);
                }
                printSeparator();
            }
        }
    }*/

    private String getPoint(String productName, int productQuantity) {
        short quantity = (short) (PRINTER_GAPS - (productName.length() + 3 + String.valueOf(productQuantity).length()));
        String gapsToPrint = "";

        for (int i = 0; i < quantity; i++) {
            gapsToPrint = gapsToPrint.concat(".");
        }

        return gapsToPrint;
    }
/*
    private void printModes(ArrayList<Mode> modeSelected, boolean isSecondLevel) {
        if (!isSecondLevel)
            printer.println();
        for (Mode currentMode : modeSelected) {
            if (!isSecondLevel) {
                printer.println(doubleSize("  > ".concat(currentMode.getNameToPrint(context))));
            } else
                printer.println(doubleSize(setBold("     > ".concat(currentMode.getNameToPrint(context)))));
            if (!currentMode.getSelected().isEmpty())
                printModes(currentMode.getSelected(), true);
        }
    }*/

    private void printSeparator() {
        printer.println();
        printer.println("__________________________________________");
        printer.println();
    }

    private void printHeader() {
        Log.e(TICKET, "Header start");
        printer.println(initializePrinter);

        printer.append(right).append(day);
        printer.println();

        printer.append(right).append(time);
        printer.println();

        printer.append(left).append(doubleSize(location));
        printer.println();
        printer.println(" ");
        Log.e(TICKET, "Header end");
    }

    private void cutTicket() {
        Log.e(TICKET, "Cutting start");
        printer.println(lineJump);
        printer.println(cutPaper);
        Log.e(TICKET, "Cut end");
    }

    //region ticket format
    public String setUnderline(String line) {
        String underlineOn = ESC + "" + "\u0001";
        String underlineOff = ESC + "" + "\0";

        return underlineOn + line + underlineOff;
    }

    public String setBold(String line) {
        String boldOn = ESC + "E" + "\u0001";
        String boldOff = ESC + "E" + "\0";

        return boldOn + line + boldOff;
    }

    private String doubleSize(String line) {
        String doubleOn = GS + "!" + "\u0001";  // 2x sized text (doublehigh + doublewide)
        String doubleOff = GS + "!" + "\0";

        return doubleOn + line + doubleOff;
    }
    //endregion

    @Override
    protected void onPostExecute(Object o) {
       /*if (connectionSuccessful)
            asyncInternet.onCartPrinted();
        else
            asyncInternet.onPrintFailed();*/
        Log.d(TAG,"on post execute in asyncPrintTicket ");
    }
}