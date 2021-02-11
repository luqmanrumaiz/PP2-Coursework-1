package CW1;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;
import java.time.LocalDate;
import java.util.*;

// @author Luqman Rumaiz w1761767
public class TrainBooking extends Application {
    final static int SEATING_CAPACITY = 42;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        List<String[][]> bookingInfoList = new ArrayList<>();
        stage.setTitle("Denuwara Mineke AC Train - Seat Booking System");
        /*creating an exit button that calls the menu and entering the bookingInfoList as a parameter
          and stage retrieving any updated information to the bookingInfoList */
        Button exit = new Button("Exit");
        exit.setOnAction(e -> {

            stage.close();
            menu(bookingInfoList,stage,exit);
        });
        exit.fire();
    }


    static void header(){
        //prints the heading for the console in between the main heading there is an = sign
        for (int unicodeChar = 0; unicodeChar < 30; unicodeChar++) {
            if (unicodeChar == 15)
                System.out.print("    Denuwara Mineke AC Train - Seat Booking System   ");
            System.out.print("=");
        }
    }


    /* This method is the menu for the program it is where the user can specify an option they want to run
     *
     * @param bookingInfoList   This is the 2D List that holds all the booking information based of upon Dates and
     *                          Destinations.
     * @param stage             This is used in GUI options to show a scene
     * @param exit              This is used to close the Stage and to loop the menu
     */

    public static void menu(List<String[][]> bookingInfoList,Stage stage,Button exit) {
        Scanner input = new Scanner(System.in);

        header();
        System.out.println("\n   Enter 'V; to View all Seats\n" +
                "   Enter 'A' to Add a Customer to a Seat\n" +
                "   Enter 'E' to Display all Empty Seats\n" +
                "   Enter 'D' to Delete a Seat\n" +
                "   Enter 'F' to Delete a Seat from a Specific Customer\n" +
                "   Enter 'S' to Store the Booking Data\n" +
                "   Enter 'L' to Load the Booking Data\n" +
                "   Enter 'O' to sort the Customer Names Alphabetically\n" +
                "   Enter 'Q' to Quit\n");
        boolean correctInput = false; /*boolean that is used in the while loop below to loop the input if an invalid
                                        input is written*/

            while (!correctInput) {
                correctInput = true;
                System.out.print("\nWhich Option to Execute : ");
                String option = input.nextLine().toUpperCase();
                switch (option) {   //exit button is fired for console options as button can't be shown visually
                    case "V":
                        viewSeats(bookingInfoList, stage, exit);
                        break;
                    case "A":
                        addSeat(bookingInfoList, stage, exit);
                        break;
                    case "E":
                        emptySeat(bookingInfoList, stage, exit);
                        break;
                    case "F":
                        findCustomer(bookingInfoList);
                        exit.fire();
                        break;
                    case "D":
                        deleteCustomer(bookingInfoList);
                        exit.fire();
                        break;
                    case "O":
                        bubbleSort(bookingInfoList);
                        exit.fire();
                        break;
                    case "S":
                        save(bookingInfoList);
                        exit.fire();
                        break;
                    case "L":
                        load(bookingInfoList);
                        exit.fire();
                        break;
                    case "Q":
                        System.exit(0);
                    default:
                        correctInput = false;
                        System.out.println("The Option you Entered is Invalid !");
                }
            }
    }


    /*Generates a Random ID for the Passenger
     *
     *@param passengersIds      This Array holds the randomly generated Passenger Ids
     *@param bookedSeatsIndex   This List holds the indexes based the booked seat numbers to store the generated Ids
     *                          in the passengerIds Array
     *@return                   Returns the passengerIds Array with the new randomly generated Ids
     */
    public static String[] randomPassengerId(String[] passengerIds, List<Integer> bookedSeatsIndex){
        Random rand = new Random();
        String randomId = "P" + rand.nextInt(100);
        for (int idIndex : bookedSeatsIndex) {
            if (!passengerIds[idIndex].equals(""))
                continue;
            for (String id : passengerIds) {
                if (randomId.equals(id))
                    randomId = "P" + rand.nextInt(100) + 1; //Concatenating "P" to random number between 0
            }
            passengerIds[idIndex] = randomId; //Adding the Id to the Passenger Id Array based on the Seat Number
        }
        return passengerIds;
    }

    /* Creates the seats for the GUI options and adds the common GUI elements like the date picker and choice box to the
     * GridPane
     *
     * @param allSeats    This Array holds each button in a button array that can be used in other GUI methods to set
     *                    button colors when a new date and destination is selected or if seats are booked
     * @param layout      The GridPane to which the date picker and choicebox as well as the seats are added to
     *                    in the passengerIds Array
     * @param from,to     This to Set the stops to from and to and to add them to the GridPane
     * @param datepicker  This is to set the date picker to the GridPane and to Not allow the user to book future seats
     */

    public static void makeButtons(Button[] allSeats, GridPane layout, ChoiceBox<String> from, ChoiceBox<String> to,
                                   DatePicker datePicker) {
        //adding the stops to from and to
        from.getItems().addAll("Badulla","Peradeniya","Nanuoya","Colombo");
        to.getItems().addAll("Badulla","Peradeniya","Nanuoya","Colombo");

        layout.add(new Label("Select the Date and Destination that you want to Load\nThen Enter the First and Last" +
                " Name of the Customer that you want to select a seat from\nThen Book the selected seats with the book" +
                " button"), 10, 1);

        layout.setHgap(15);
        layout.setVgap(20);
        layout.setPadding(new Insets(10));  //adding padding to the array lists
        layout.getColumnConstraints().addAll(new ColumnConstraints(40),new ColumnConstraints(40),
                new ColumnConstraints(40),new ColumnConstraints(75));
        /*adding column constraints for the gridpane where the middle constraint is higher to emulate a real life
        train where the left and right side are seperated*/

        layout.add(new Label("From : "),12,4);
        layout.add(from, 13, 5);
        layout.add(new Label("To : "),12,5);
        layout.add(to,13,4);

        layout.add(datePicker,13,3);

        //This disables the past day cells in the date picker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo( LocalDate.now()) < 0 );
            }
        });

        /*This is the two for loops used to create the seats (buttons), buttons are placed in first rows then columns
           to make sure that seat numbers stimulate a real train from left to right and then off to the next row
         */

        int seatNumber = 0; //This defines the button name which is the seat number
        for (int row = 1; row < 8; row++) {
            for (int column = 0; column < 7; column++) {
                if (column == 3)
                    continue;
                Button seat = new Button(Integer.toString(seatNumber + 1));
                seat.setMinSize(40,60);
                layout.add(seat, column, row);
                seat.setId("empty");
                allSeats[seatNumber] = seat;    //Adding the seat to the allSeats button array
                seatNumber++;
            }
        }
    }


    /* Finds data from a 2D List based off a given Date and Destination from each Array existing in the list,
     * if the data does not exist a new 2D Array is made, storing the Date and Destination in one of the Arrays of the
     * 2D Array.
     *
     * The Date Structure Looks Like This   <[[Date,Destination],[42 Customer Names],[42 Passenger IDs]]>
     *
     *
     * @param selectDate             This is the Date the User wants to obtain or create Booking Information from.
     * @param destination            This is the Destination the User wants to obtain or create Booking Information from.
     * @param bookingInfoList        This is the 2D List that holds all the booking information based of upon Dates and
     *                               Destinations.
     * @param customerNames          This is the Array that contains customerNames in the Different options, this method
     *                               loads the booking information into that Array.
     * @return  dateAndDestination   Returning the date and destination, which is used in the add method to update the
     *                               bookingInfoList.
     */
    public static String[] getArrayData(String selectDate, String destination, List<String[][]> bookingInfoList,
                                        String[][] customerNames){

        boolean dataExists = false; //boolean that checks if the data for the date and destination exists
        String[] dateAndDestination = new String[2];
        dateAndDestination[0] = selectDate;
        dateAndDestination[1] = destination;

        for (String[][] information: bookingInfoList) {

            if (information[0][0].equals(selectDate) && information[0][1].equals(destination)) {
                customerNames[0] = information[1].clone();  //Copying the data to the customerNames Array if the data is
                dataExists = true;                          //found
                break;
            }
        }
        if (!dataExists){   //Creates a new 2D Array to be stored in bookingInfoList if the data doesn't exist

            Arrays.fill(customerNames[0], "");
            String[] passengerIds = new String[SEATING_CAPACITY];
            Arrays.fill(passengerIds,"");
            String[][] bookingInfoAdd = {new String[2], new String[SEATING_CAPACITY],passengerIds};

            bookingInfoAdd[0][0] = selectDate;
            bookingInfoAdd[0][1] = destination;

            bookingInfoAdd[1] = customerNames[0].clone();
            bookingInfoList.add(bookingInfoAdd);

        }
        return dateAndDestination;
    }

    /* This method validates the date picker and choice box.
     *
     * @param to,from       This is the choice boxes where a destination is selected from its used her
     *                      to check for some validations
     *
     * @param destination   This is where the date is selected from it is used here for some validations
     * @return error        This is to ensure that data from getArray is not loaded if an error is made
     */
    public static boolean dateDestinationValidation(ChoiceBox<String> to, ChoiceBox<String> from, DatePicker datePicker){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        boolean error = false;
        if (datePicker.getValue() == null) {    //checks if no date is selected
            alert.setContentText("Enter the Date");
            alert.show();
        } else if (to.getValue() == null||from.getValue() == null){ //checks if the from or to choicebox is not selected
            alert.setContentText("Enter Both From and To, to Select the Destination");
            alert.show();
        }else if (from.getValue().equals(to.getValue())){   //checks if the values in the from and to is the same
            alert.setContentText("'From' and 'To' cannot be the same");
            alert.show();
        }
        else
            error = true;
        return error;
    }

    /* This method is used to view customer seats for a date and destination, where red buttons represent booked seats
     * and green buttons represent empty seats
     *
     * @param bookingInfoList     This is the 2D List that holds all the booking information based of upon Dates and
     *                             Destinations.
     *
     * @param stage         This is used to set the GUI scene to be shown
     * @param exit          This is used to close the stage and to loop the menu
     */
    public static void viewSeats(List<String[][]> bookingInfoList, Stage stage,Button exit) {

        GridPane layout = new GridPane();
        Label heading = new Label("VIEW SEATS");
        heading.setId("heading");
        BorderPane borderPane = new BorderPane();


        layout.add(exit,10,10);
        Button[] allSeats = new Button[SEATING_CAPACITY];
        String[][] customerNames = {new String[SEATING_CAPACITY]};
        //Array that holds booked customer names and loaded information based on date and destination
        DatePicker datePicker = new DatePicker();

        ChoiceBox<String> from = new ChoiceBox<>();
        ChoiceBox<String> to = new ChoiceBox<>();

        boolean[] noError = {false};

        makeButtons(allSeats,layout,from,to,datePicker);

        layout.add(new Label("Green Seats Represent Empty Seats"), 10, 3);
        layout.add(new Label("Green Seats Represent Empty Seats"), 10, 3);

        Button selectDataDestination = new Button("Confirm Date and Destination");
        selectDataDestination.setOnAction(event -> {
            noError[0] = dateDestinationValidation(to,from,datePicker);

            if (noError[0]) {   //checking if an error made when validating the date and destination
                String selectDate = (datePicker.getValue()).toString();
                String destinationChoice = to.getValue() + '-' + from.getValue();
                System.out.println(destinationChoice);
                getArrayData(selectDate, destinationChoice, bookingInfoList, customerNames);

                int seatNumber = 0;
                //updating buttons in the allSeats button array if the date and destination is changed
                for (Button button : allSeats) {
                    if (!customerNames[0][seatNumber].equals(""))
                        button.setId("booked");
                    else
                        button.setId("empty");
                    button.setDisable(false);
                    seatNumber++;
                }

            }
        });
        layout.add(selectDataDestination,13,7);
        borderPane.setCenter(layout);
        borderPane.setTop(heading);
        BorderPane.setAlignment(layout, Pos.TOP_CENTER);
        BorderPane.setAlignment(heading, Pos.TOP_CENTER);

        Scene scene = new Scene(borderPane,1500,900);
        scene.getStylesheets().add(TrainBooking.class.getResource("TrainBooking.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /* This method is used to add to a customer to a seat for a date and destination, where red buttons represent booked seats
     * and green buttons represent empty seats
     *
     * @param bookingInfoList     This is the 2D List that holds all the booking information based of upon Dates and
     *                             Destinations. Any Updated information is saved here.
     *
     * @param stage         This is used to set the GUI scene to be shown
     * @param exit          This is used to close the stage and to loop the menu
     */
    public static void addSeat(List<String[][]> bookingInfoList,Stage stage,Button exit) {

        GridPane layout = new GridPane();
        Label heading = new Label("ADD CUSTOMERS");
        heading.setId("heading");
        BorderPane borderPane = new BorderPane();


        Button book = new Button("Book");

        layout.add(exit,10,10);

        Button[] allSeats = new Button[SEATING_CAPACITY];
        String[][] customerNames = {new String[SEATING_CAPACITY]};
        //Array that holds oaded information based on date and destination
        String[][] tempCustomerNames = {new String[SEATING_CAPACITY]};


        Arrays.stream(customerNames).forEach(seatStatus -> Arrays.fill(seatStatus, ""));
        /*filling arrays with empty strings which represent empty seats that are later updated it also helps to prevent
           null pointer exceptions
         */

        Arrays.stream(tempCustomerNames).forEach(seatStatus -> Arrays.fill(seatStatus, ""));


        layout.add(new Label("Green Seats Represent Empty Seats"), 10, 3);
        layout.add(new Label("Red Seats Represent Booked Seats"), 10, 2);

        DatePicker datePicker = new DatePicker();

        ChoiceBox<String> from = new ChoiceBox<>();
        ChoiceBox<String> to = new ChoiceBox<>();

        String[][] dataAndDestination = {new String[2]};

        boolean[] noError = {false};

        Button selectDataDestination = new Button("Confirm Date and Destination");
        selectDataDestination.setOnAction(event -> {
            noError[0] = dateDestinationValidation(to,from,datePicker);

            if (noError[0]) {
                String selectDate = (datePicker.getValue()).toString();
                String destinationChoice = to.getValue()+ '-' + from.getValue();
                System.out.println(destinationChoice);
                dataAndDestination[0] = getArrayData(selectDate, destinationChoice, bookingInfoList, customerNames);

                int seatNumber1 = 0;
                for (Button button : allSeats) {
                    if (customerNames[0][seatNumber1].equals("")) {
                        button.setId("empty");
                        button.setDisable(false);
                    } else {
                        button.setId("booked");
                        button.setDisable(true);
                    }
                    seatNumber1++;
                }
                tempCustomerNames[0] = customerNames[0].clone();
            }
        });
        layout.add(selectDataDestination,13,7);


        //The below two temporary Arrays are used to store selected information that is not confirmed yet
        TextField firstName = new TextField();
        TextField surname = new TextField();

        firstName.setMaxWidth(250);
        surname.setMaxWidth(250);

        int seatNumber = 0;

        makeButtons(allSeats,layout,from,to,datePicker);
        for (Button button : allSeats) {
            int finalSeatNumber = seatNumber;
            button.setOnAction((ActionEvent event) -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (!noError[0]) {  //if a date and destination are not selected and user tries to select a seat
                    alert.setContentText("Select a Date and Destination First");
                    alert.show();
                }else if (firstName.getText().equals("") || surname.getText().equals("")) {
                    alert.setContentText("Enter Both First and Last Name to Book a Seat");
                    alert.show();
                }else {
                    button.setId("select");
                    button.setDisable(false);
                    System.out.println(finalSeatNumber);
                    tempCustomerNames[0][finalSeatNumber] = firstName.getText() + " " + surname.getText();
                }
            });
            seatNumber++;
        }

        /*This Event Handler confirms the selected seats and updates the name of the selected seats if changed from the
          temporary to the customerName Array which is then updated to the List*/
        book.setOnAction(event -> {
            boolean noSeatsSelected = true;
            for (String tempName : tempCustomerNames[0])
                if (!tempName.equals("")) {
                    noSeatsSelected = false;
                    break;
                }
            if (!noSeatsSelected) {
                List<Integer> bookedSeatsNumbers = new ArrayList<>();
                int confirmSeatNumber = 0;

                customerNames[0] = tempCustomerNames[0].clone();
                for (Button button : allSeats) {  //the color of booked seats are changed to red through the id
                    if (!customerNames[0][confirmSeatNumber].equals("")) {
                        button.setId("booked");
                        button.setDisable(true);
                        bookedSeatsNumbers.add(confirmSeatNumber);
                    }
                    confirmSeatNumber++;
                }
                String selectDate = dataAndDestination[0][0];
                String destination = dataAndDestination[0][1];

                System.out.println("Date : " + selectDate + "\nDestination : " + destination + "\nDate Successfully Booked");

                //for loop that updates each array of the bookingInfoList
                for (int i = 0; i < bookingInfoList.size(); i++) {

                    String[][] bookingInfo = bookingInfoList.get(i);

                    if (bookingInfo[0][0].equals(selectDate) && bookingInfo[0][1].equals(destination)) {
                        String[] customerNamesCopy;
                        customerNamesCopy = customerNames[0].clone();
                        String[] randomIds = randomPassengerId(bookingInfo[2], bookedSeatsNumbers);
                        String[][] updateBookingInfo = {{selectDate, destination}, customerNamesCopy, randomIds};
                        bookingInfoList.set(i, updateBookingInfo);

                    }
                }
            }else{  //validation if user tries to book without selecting a seat
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Select Seats to Book");
                alert.show();
            }
        });

        layout.add(new Label("First Name"),9,4);
        layout.add(firstName, 10, 4);
        layout.add(new Label("Last Name"),9,5);
        layout.add(surname,10,5);
        layout.add(book,10,6);
        borderPane.setCenter(layout);
        borderPane.setTop(heading);
        BorderPane.setAlignment(layout, Pos.TOP_CENTER);
        BorderPane.setAlignment(heading, Pos.TOP_CENTER);

        Scene scene = new Scene(borderPane,1500,900);
        scene.getStylesheets().add(TrainBooking.class.getResource("TrainBooking.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /* This method is used to view customer seats for a date and destination, where invisible buttons represent booked seats
     * and green buttons represent empty seats
     *
     * @param bookingInfoList     This is the 2D List that holds all the booking information based of upon Dates and
     *                               Destinations.
     *
     * @param stage         This is used to set the GUI scene to be shown
     * @param exit          This is used to close the stage and to loop the menu
     */
    public static void emptySeat(List<String[][]> bookingInfoList,Stage stage,Button exit) {

        Label heading = new Label("VIEW EMPTY SEATS");
        heading.setId("heading");

        BorderPane borderPane = new BorderPane();
        GridPane layout = new GridPane();

        layout.add(exit,10,10);
        Button[] allSeats = new Button[SEATING_CAPACITY];

        String[][] customerNames = {new String[SEATING_CAPACITY]};

        DatePicker datePicker = new DatePicker();

        ChoiceBox<String> from = new ChoiceBox<>();
        ChoiceBox<String> to = new ChoiceBox<>();

        boolean[] noError = {false};

        layout.add(new Label("Green Seats Represent Empty Seats"), 10, 3);
        layout.add(new Label("Invisible Seats Represent Booked Seats"), 10, 4);
        makeButtons(allSeats,layout,from,to,datePicker);

        Button selectDataDestination = new Button("Confirm Date and Destination");
        selectDataDestination.setOnAction(event -> {
            noError[0] = dateDestinationValidation(to,from,datePicker);

            if (noError[0]) {
                String selectDate = (datePicker.getValue()).toString();
                String destinationChoice = to.getValue() + '-' + from.getValue();
                System.out.println("DATE : "+selectDate+" DESTINATION : "+destinationChoice);
                getArrayData(selectDate, destinationChoice, bookingInfoList, customerNames);

                int seatNumber = 0;
                for (Button button : allSeats) {
                    if (!customerNames[0][seatNumber].equals(""))
                        button.setVisible(false);
                    else
                        button.setVisible(true);
                    button.setDisable(false);
                    seatNumber++;
                }
            }
        });
        layout.add(selectDataDestination,13,7);

        borderPane.setTop(heading);
        borderPane.setCenter(layout);

        BorderPane.setAlignment(layout, Pos.TOP_CENTER);
        BorderPane.setAlignment(heading, Pos.TOP_CENTER);
        Scene scene = new Scene(borderPane,1500,900);
        scene.getStylesheets().add(TrainBooking.class.getResource("TrainBooking.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /* This method shows the date and destination where the a specified customer name is saved in as well as the seat
     * number
     * @param bookingInfoList   The 2D List that has the arrays for different information based on date. It is used here
     *                      to find the customer name in each date and destination
     */

    public static void findCustomer(List<String[][]> bookingInfoList){
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the Customer Name to be Found : ");
        String customerName = input.nextLine();
        List<Integer> bookedSeats = new ArrayList<>();
        boolean nameFound = false;
        for (String[][] information : bookingInfoList){
            nameFound = false;
            for (int i = 0; i<42; i++){
                if(information[1][i].equals(customerName)) {
                    bookedSeats.add(i);
                    nameFound = true;
                }
            }

            if (nameFound){ //boolean that checks if the name was found
                System.out.println("\nDate : "+information[0][0]+"\nDestination : "+information[0][1]);
                for (Integer seat : bookedSeats) /*showing each seat the customer has booked through the index of the customer in the array*/
                    System.out.println("Seat : "+(seat+1));
            }
            bookedSeats.clear();    //clearing the booked seat for the current date and destination for the next date
                                    //and destination to find customer information from
        }

        if (!nameFound) //error message if customer is not found
            System.out.println("Customer Not Found");


        System.out.print("Press any Key to Continue to the Menu : ");
        String continueToMenu = input.next();
    }

    /* This method shows the date and destination where the a specified customer name is saved in as well as the seat
     * number
     * @param bookingInfoList   The 2D List that has the arrays for different information based on date. It is used here
     *                          to find the customer name in each date and destination then it deletes the seat number the
     *                          user specifies if it exists.
     */
    public static void deleteCustomer(List<String[][]> bookingInfoList){
        Scanner input = new Scanner(System.in);

        System.out.println("Please Enter your Date (FORMAT : yyyy-mm-dd) and The Destination (FORMAT : from-to) you " +
                "want to delete the customer from\n");

        System.out.print("Enter the Date : ");
        String date = input.nextLine();

        System.out.print("Enter the Destination : ");
        String destination = input.nextLine();

        System.out.print("Enter the Customer Name you want the Seat to be Deleted From :");
        String customerName = input.nextLine();

        System.out.print("Enter the Seat you want to be Delete :");
        int seatNumber = input.nextInt();

        String[][] customerNames = {new String[SEATING_CAPACITY]};
        getArrayData(date,destination,bookingInfoList,customerNames);

        boolean emptyArray = true;  //boolean used to check if the information based on the date and destination is empty
        for (int customer = 0; customer < SEATING_CAPACITY; customer++)
            if (!customerNames[0][customer].equals("")) {
                emptyArray = false;
                break;
            }
        boolean customerFound = false;
        if (!emptyArray) {
            for (String[][] information : bookingInfoList) {
                for (int i = 0; i < 42; i++) {
                    if (information[1][i].equals(customerName) && i == seatNumber) {
                        information[1][i] = ""; //deleting the customer name by setting it as empty
                        information[2][i] = ""; //deleting the customer id by setting it as empty
                        customerFound = true;
                        break;
                    }
                }
            }
            if (!customerFound) //error message if customer not found
                System.out.println("Customer not Found");
            else
                System.out.println("Customer Successfully Deleted");
        }else
            System.out.println("Information for Given Date and Destination was not Found !” ");

        System.out.print("Press any Key to Continue to the Menu : ");
        String continueToMenu = input.next();
    }

    /* This method sorts the name of customers with their seat numbers for a specified date and destination
     * number
     * @param bookingInfoList   The 2D List that has the arrays for different information based on date. It is used here
     *                          to sort the customer names for the specified date and destination.
     */
    public static void bubbleSort(List<String[][]> bookingInfoList) {
        Scanner input = new Scanner(System.in);
        System.out.println("Please Enter your Date (FORMAT : yyyy-mm-dd) and The Destination (FORMAT : from-to) you " +
                "want to get the information to sort from\n");

        System.out.print("Enter the Date : ");
        String date = input.nextLine();

        System.out.print("Enter the Destination : ");
        String destination = input.nextLine();

        String[][] customerNames = {new String[SEATING_CAPACITY]};
        getArrayData(date,destination,bookingInfoList,customerNames);

        boolean emptyArray = true;
        for (int customer = 0; customer < SEATING_CAPACITY; customer++)
            if (!customerNames[0][customer].equals("")) {
                emptyArray = false;
                break;
            }

        if (!emptyArray) {
            String temp;
            String[] sortCustomerNames;

            sortCustomerNames = customerNames[0].clone();   //copying the customer names to another array for sorting
            System.out.println("Names in sorted order:");

            //for loop that sorts the customer names
            for (int firstName = 0; firstName < SEATING_CAPACITY; firstName++) {
                for (int nextName = firstName + 1; nextName < SEATING_CAPACITY; nextName++) {
                    if ((sortCustomerNames[nextName].compareTo(sortCustomerNames[firstName]) < 0)) {
                        //comparing if the next name taken in the arrary is greater than the first name based on ASCII
                        temp = sortCustomerNames[firstName];
                        sortCustomerNames[firstName] = sortCustomerNames[nextName];
                        sortCustomerNames[nextName] = temp;
                    }
                }
            }

            List<String> names = new ArrayList<>(); //List that holds each customer name to avoid duplicates
            for (int i = 0; i < SEATING_CAPACITY; i++) {
                String name = sortCustomerNames[i];

                if (!names.contains(name) && !name.equals("")) {
                    names.add(name);
                    System.out.print("\nCustomer Name : " + name + " Seat Number : ");
                    for (int nameIndex = 0; nameIndex < SEATING_CAPACITY; nameIndex++) {
                        if (customerNames[0][nameIndex].equals(name)) {
                            /*printing each seat for the customer if the customer name of the names array is the same
                              as the customer name in customerNames array to get the index to which the customer is
                              saved in which is their seat number
                            */
                            System.out.print(nameIndex + 1 + " ");
                        }
                    }
                }
            }
        }else
            System.out.println("Information for Given Date and Destination was not Found !");

        System.out.print("\nEnter Any Key to Continue to Menu : ");
        String continueToMenu = input.nextLine();
    }


    /* This method saves each 2D Array from the List to the mongo database as a collection to be loaded later.
     *
     * @param bookingInfoList   The 2D List that has the arrays for different information based on date. It is used here
     *                          to save the information to the mongo database.
     */
    public static void save(List<String[][]> bookingInfoList) {
        Scanner input = new Scanner(System.in);
        if (bookingInfoList.size() > 0) {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase database = mongoClient.getDatabase("Train-Booking-Info");
            System.out.println("Database Connected");

            //Saving all the information in the 2D List to the Mongo Collections
            for (String[][] information : bookingInfoList) {

                String date = information[0][0];
                String destination = information[0][1];

                String[] customerNames = information[1];
                String[] passengerIds = information[2];

                try {
                    database.createCollection(date + " " + destination);
                } catch (MongoCommandException e) {
                    System.out.println("Collection Exists");
                    database.getCollection(date + " " + destination).deleteMany(new BasicDBObject());
                    //removing existing documents if the collection exists
                }

                MongoCollection<Document> collection = database.getCollection(date + " " + destination);

                for (int i = 0; i < 42; i++) {
                    //adding the customer name and id to the document
                    Document document = new Document("Customer ",customerNames[i]);
                    document.append("Passenger Id ", passengerIds[i]);
                    collection.insertOne(document);
                }
            }
        }else System.out.println("There is no Data to Save !"); //error message if list is empty
        System.out.print("Press any Key to Continue to the Menu : ");
        String continueToMenu = input.next();
    }


    /* This method documents from mongo collection to a 2D Array that is added to the List,
     *
     * @param bookingInfoList   The 2D List that has the arrays for different information based on date. It is used here
     *                          to load the information from the mongo collection
     */
    public static void load(List<String[][]> bookingInfoList) {
        Scanner input = new Scanner(System.in);

        bookingInfoList.clear();    //Clearing the List before loading to remove any existing data
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("Train-Booking-Info");

        MongoIterable<String> collectionNames = database.listCollectionNames(); //stores each collection name

        for (String s : collectionNames) {
            if (s.equals("collection"))
                continue;

            //Using String Builder to Split the Collection Name which first the date space the destination
            StringBuilder date = new StringBuilder();
            StringBuilder destination = new StringBuilder();
            boolean moveToDestination = false;


            for (int collNameIndex = 0; collNameIndex < s.length(); collNameIndex++) {
                String letter = Character.toString(s.charAt(collNameIndex));
                if (letter.equals(" ")) {       //if the next letter is a space it sets moveToDestination as True
                    moveToDestination = true;   //which adds the remaining letters to the destination
                    continue;
                }

                if (!moveToDestination)
                    date.append(letter);
                else
                    destination.append(letter);
            }


            MongoCollection<Document> collection = database.getCollection(s);   //getting the collection based on the
                                                                                //collection name

            int nameCount = 0; //index of the customerNames array to load the names in

            String[] customerNames = new String[42];
            Arrays.fill(customerNames,"");

            for (Document information : collection.find()) {

                customerNames[nameCount] =  information.getString("Customer ");
                nameCount++;

                //This is a 2D Array that holds information from the mongo collection to add to the bookingInfoList
            }
            String[][] updateList = {{String.valueOf(date), String.valueOf(destination)}, customerNames};
            bookingInfoList.add(updateList);
        }
        System.out.print("\nPress any Key to Continue to the Menu : ");
        String continueToMenu = input.next();
    }
}